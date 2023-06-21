package com.ako.hidemyvideo.Helper

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import androidx.appcompat.app.AppCompatActivity

class ScreenOrientationListener(context: Context) : OrientationEventListener(context) {
    private var currentOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private val activity = context as AppCompatActivity
    override fun onOrientationChanged(orientation: Int) {
        val newOrientation = when (orientation) {
            in 45..134 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            in 135..224 -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
            in 225..314 -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (newOrientation != currentOrientation) {
            currentOrientation = newOrientation
            activity.requestedOrientation = newOrientation
        }
    }
}