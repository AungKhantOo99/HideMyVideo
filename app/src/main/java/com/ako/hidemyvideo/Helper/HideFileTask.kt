package com.ako.hidemyvideo.Helper

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class HideFileTask(
    private val context: Context,
    private val filePath: ArrayList<String>,
    private val callback: MoveFilesCallback,
    private val unHideFiles:Boolean
) : AsyncTask<Unit, Int, Unit>() {
    val deleteRequestCode = 123
    private lateinit var progressDialog: ProgressDialog
    override fun onPreExecute() {

        //intializa progress dialog
        progressDialog = ProgressDialog(context)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setTitle("Moving File")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.max = 100
        progressDialog.show()

    }

    override fun doInBackground(vararg params: Unit?) {
        filePath.forEach {
            var destinationFolder:File?=null
            var destinationFile:File?=null
            val sourceFile = File(it)
            //check unhide or hide
            if(unHideFiles){
                 destinationFolder = File( "/storage/emulated/0", "unhide")
                destinationFolder!!.mkdir()
                val file=getLastSubstring(it, "/")
                destinationFile= File(destinationFolder.absolutePath,decrypt(file, KEY) )
            }else{
                 destinationFolder = File(getRootDirPath(context),".hide")
                destinationFolder!!.mkdir()
                val file=getLastSubstring(it, "/")
                destinationFile= File(destinationFolder.absolutePath,file.getEncryptedFileNameFromhidePath() )
            }

            try {
                FileInputStream(sourceFile).use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        var totalBytesRead: Long = 0
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

                    try {

                            val isDeleted = sourceFile.delete()

                            if (isDeleted) {
                                Log.d("TAG", "Success")
                            } else {
                                Log.d("TAG", "FAil")
                            }

                    } catch (e: SecurityException) {
                        Log.d("TAG",e.message.toString())
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.d("TAG",e.message.toString())
                        e.printStackTrace()
                    }


                // Delete the source file if needed
            //    sourceFile.delete()

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