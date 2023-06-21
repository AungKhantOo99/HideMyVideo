package com.ako.hidemyvideo.Helper

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat
import com.ako.hidemyvideo.model.VideoItem
import com.google.android.gms.common.util.Hex
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.File
import java.text.DecimalFormat
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


const val CREATE="create"
const val LOGIN="login"
const val FROMHIDE="fromhide"
const val KEY="hidevideokey1600"



//to Show only folder name in folder full path
fun getLastSubstring(str: String, delimiter: String): String {
    return str.substringAfterLast(delimiter)
}

// to handle video rotation
fun retrieveVideoSize(videoPath: String): String {
    val file = File(videoPath)
    val fileSizeInBytes = file.length()
    val decimalFormat = DecimalFormat("#.##")

    return when {
        fileSizeInBytes < 1024L -> "${fileSizeInBytes} B"
        fileSizeInBytes < 1024L * 1024L -> "${decimalFormat.format(fileSizeInBytes.toDouble() / 1024)} KB"
        fileSizeInBytes < 1024L * 1024L * 1024L -> "${decimalFormat.format(fileSizeInBytes.toDouble() / (1024 * 1024))} MB"
        else -> "${decimalFormat.format(fileSizeInBytes.toDouble() / (1024 * 1024 * 1024))} GB"
    }
}

//to show duration in hour::mm::ss format
fun retrieveVideoDuration(duration:Long):String{
    val durationInSeconds = duration / 1000
    val hours = durationInSeconds / 3600
    val minutes = (durationInSeconds % 3600) / 60
    val seconds = durationInSeconds % 60
    val formattedDuration = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
    return formattedDuration
}

// for vibrate phone when password incorrect
fun vibratePhone(context: Context, durationMillis: Long) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Check if the device supports vibration
    if (vibrator.hasVibrator()) {
        // Create a VibrationEffect with the desired duration
        val vibrationEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        // Vibrate the phone with the specified VibrationEffect
        vibrator.vibrate(vibrationEffect)
    }
}

// get package name to save hidden files
 fun getRootDirPath(context: Context): String {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file: File = ContextCompat.getExternalFilesDirs(
            context.applicationContext,
            null
        )[0]
        file.absolutePath
    } else {
        context.applicationContext.filesDir.absolutePath
    }
}


// for security encrypt video name
fun String.getEncryptedFileNameFromhidePath(): String {
    val f = File(this)
    val name = f.name
    return encrypt(name, KEY)
}
fun encrypt(passKey: String, myKey: String): String {
    var plainText = myKey.toByteArray(Charsets.UTF_8)
    val secretKey = SecretKeySpec(plainText, "AES")

    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)

    val hex = Hex.bytesToStringLowercase(cipher.doFinal(passKey.toByteArray(Charsets.UTF_8)))
    Log.d("test_key", "hex = $hex")
    return hex
}
// decrypt encrypt file name
fun decrypt(passKey: String, myKey: String): String {
    val plainText = myKey.toByteArray(Charsets.UTF_8)
    val secretKey = SecretKeySpec(plainText, "AES")

    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)

    val dec = String(cipher.doFinal(Hex.stringToBytes(passKey)))
    Log.d("test_key", "decrypt = $dec")
    return dec
}

// get video in hidden folder
fun getFilesDetails(context: Context, directoryPath: String): ArrayList<VideoItem> {
    val fileDetailsList = ArrayList<VideoItem>()
    val directory = File(directoryPath)

    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()

        if (files != null) {
            val retriever = MediaMetadataRetriever()

            for (file in files) {
                val title = file.nameWithoutExtension
                val thumbnailPath = file.absolutePath
                val videoPath = file.absolutePath
                retriever.setDataSource(file.absolutePath)
                val videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
              //  val videoDuration = getVideoDuration(retriever, file.canonicalPath)

                val fileDetails = VideoItem(decrypt(title, KEY), thumbnailPath, videoPath, videoDuration)
                fileDetailsList.add(fileDetails)
            }
        }
    }

    return fileDetailsList
}

fun fetchVideoDuration( videoPath: String): Long {
    val retriever = FFmpegMediaMetadataRetriever()
    retriever.setDataSource(videoPath)
    return retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0
}





