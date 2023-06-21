package com.ako.hidemyvideo.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.ako.hidemyvideo.Helper.FROMHIDE
import com.ako.hidemyvideo.R
import com.ako.hidemyvideo.adapter.AllFoldersAdapter
import com.ako.hidemyvideo.databinding.ActivityMainBinding
import com.ako.hidemyvideo.getvideodata.getVideosFromDevice
import com.ako.hidemyvideo.model.VideoItem

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: AllFoldersAdapter
    lateinit var videoMap: Map<String, List<VideoItem>>
    var checkfromhide: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title="All Videos"

        //get all video from device
        if (checkPermission()) {
            videoMap = getVideosFromDevice(contentResolver)
        } else {
            requestPermission()
        }

        //get intent data to handle backPressed and menuItem show hide
        checkfromhide = intent.getStringExtra(FROMHIDE)

        //set up adapter
        adapter = AllFoldersAdapter(this, checkfromhide)
        //submit data
        adapter.submitListwithFolder(videoMap)

        /*** check all folder name
        for ((s1,) in videoMap) {
        Log.d("AllFolder",s1)
        }
         **/
        for ((folder, data) in videoMap) {
            Log.d("Alldata", data.toString())

        }
        binding.showAllVideo.layoutManager = GridLayoutManager(this@MainActivity, 3)
        binding.showAllVideo.adapter = adapter
        //    binding.showAllVideo.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val menuItem = menu.findItem(R.id.go_private)
        if (checkfromhide != null) {
            menuItem.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.go_private -> {
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermission()) {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(permission, 100)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (checkfromhide != null) {
            startActivity(Intent(this, PrivateActivity::class.java))
        } else {
            finishAffinity()
        }

    }

    override fun onResume() {
        super.onResume()
        videoMap = getVideosFromDevice(contentResolver)
        adapter.submitListwithFolder(videoMap)
        adapter.notifyDataSetChanged()

    }

}