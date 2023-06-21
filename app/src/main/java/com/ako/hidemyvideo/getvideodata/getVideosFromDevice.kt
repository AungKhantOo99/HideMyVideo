package com.ako.hidemyvideo.getvideodata

import android.content.ContentResolver
import android.provider.MediaStore
import com.ako.hidemyvideo.model.VideoItem

 fun getVideosFromDevice(contentResolver: ContentResolver): Map<String, List<VideoItem>> {
    val videoMap = mutableMapOf<String, MutableList<VideoItem>>()

    val projection = arrayOf(
        MediaStore.Video.Media.TITLE,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Thumbnails.DATA,
        MediaStore.Video.Media.DURATION
    )
   // val sortOrder = "${MediaStore.Video.Media.TITLE} ASC"

    contentResolver.query(
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        val thumbnailColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        val duration=cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        while (cursor.moveToNext()) {
            val title = cursor.getString(titleColumn)
            val videoPath = cursor.getString(dataColumn)
            val thumbnailPath = cursor.getString(thumbnailColumn)
            val duration=cursor.getLong(duration)
            val videoItem = VideoItem(title, thumbnailPath, videoPath,duration.toString())
            // Get the folder path of the video
            val folderPath = videoPath.substringBeforeLast("/")

            // Check if the folder already exists in the map
            if (videoMap.containsKey(folderPath)) {
                // Add the video to the existing folder
                videoMap[folderPath]?.add(videoItem)
            } else {
                // Create a new folder and add the video to it
                val videoList = mutableListOf<VideoItem>()
                videoList.add(videoItem)
                videoMap[folderPath] = videoList
            }
        }
    }

    return videoMap
}
