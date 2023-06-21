package com.ako.hidemyvideo.Helper

import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.google.android.exoplayer2.ui.PlayerView

class ZoomTouchListener(private val playerView: PlayerView) : View.OnTouchListener {
    private val scaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(playerView.context, ScaleListener())

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        // Pass touch events to the ScaleGestureDetector
        scaleGestureDetector.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var scaleFactor = 1f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Calculate the scale factor from the gesture detector
            scaleFactor *= detector.scaleFactor

            // Limit the minimum and maximum scale factor as desired
            scaleFactor = scaleFactor.coerceIn(0.5f, 2.0f)

            // Apply the scale factor to the player view
            playerView.scaleX = scaleFactor
            playerView.scaleY = scaleFactor

            return true
        }
    }
}
