package com.ako.hidemyvideo.Helper

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileInputStream

// Call this class to move Movies directory
class MoveFileTask(
    private val context: Context,
    private val filePath: String,
    private val callback: MoveFilesCallback
) : AsyncTask<Unit, Int, Unit>() {

    private lateinit var progressDialog: ProgressDialog

    override fun onPreExecute() {
        progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Moving File")
        progressDialog.setMessage("Please wait...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)
        progressDialog.max = 100
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Unit?) {
        val sourceFile = File(filePath)
        val destinationFileName = sourceFile.name

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, destinationFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            //You cam change directory name in lower code
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
        }

        val resolver: ContentResolver = context.contentResolver
        val collectionUri: Uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val itemUri: Uri? = resolver.insert(collectionUri, contentValues)

        itemUri?.let { uri ->
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    FileInputStream(sourceFile).use { inputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        var totalBytesRead = 0L
                        val fileSize = sourceFile.length()

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            val progress = ((totalBytesRead * 100) / fileSize).toInt()
                            publishProgress(progress)
                        }
                        outputStream.flush()
                    }
                }

                // Delete the source file if needed
                sourceFile.delete()
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the move file failure here
                callback.onMoveFailed("Failed to move the file.")
            }
        }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        val progress = values[0] ?: 0
        progressDialog.progress = progress
    }

    override fun onPostExecute(result: Unit?) {
        progressDialog.dismiss()
        callback.onMoveCompleted()
    }
}