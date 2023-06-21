package com.ako.hidemyvideo.Helper

// for show progress bar when move
interface MoveFilesCallback {
    fun onMoveCompleted()
    fun onMoveFailed(errorMessage: String)
}
