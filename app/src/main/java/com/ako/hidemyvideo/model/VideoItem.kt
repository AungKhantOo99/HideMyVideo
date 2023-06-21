package com.ako.hidemyvideo.model

data class VideoItem(val title: String,
                     val thumbnailPath: String,
                     val videoPath: String,
                     val videoDuration: String?,
                     var isSelected: Boolean = false):java.io.Serializable

