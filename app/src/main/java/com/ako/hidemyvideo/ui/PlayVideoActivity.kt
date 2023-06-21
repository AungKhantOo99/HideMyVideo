package com.ako.hidemyvideo.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ako.hidemyvideo.Helper.ScreenOrientationListener
import com.ako.hidemyvideo.R
import com.ako.hidemyvideo.model.VideoItem
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

class PlayVideoActivity : AppCompatActivity() {
    //   lateinit var videoView:VideoView
    // lateinit var binding: ActivityPlayVideoBinding
    lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var orientationListener: ScreenOrientationListener
    private var autoRotationEnabled = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   binding= ActivityPlayVideoBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_play_video)
        supportActionBar?.hide()
        // Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val data = intent.getSerializableExtra("Video_data") as VideoItem

        exoPlayer = SimpleExoPlayer.Builder(this).build()

       // Set up the PlayerView
        val playerView: PlayerView = findViewById(R.id.playerView)
        playerView.player = exoPlayer
        playerView.useController=true
    //    playerView.setOnTouchListener(ZoomTouchListener(playerView))
        // Create a MediaItem for your video
        val videoUri = Uri.parse(data.videoPath)
        val mediaItem = MediaItem.fromUri(videoUri)

        orientationListener = ScreenOrientationListener(this)
        orientationListener.enable()
        // Set the MediaItem to the ExoPlayer
        exoPlayer.setMediaItem(mediaItem)

        // Prepare the ExoPlayer
        exoPlayer.prepare()
        exoPlayer.play()

    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        orientationListener.disable()
    }
}