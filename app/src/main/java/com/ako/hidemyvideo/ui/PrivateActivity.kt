package com.ako.hidemyvideo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ako.hidemyvideo.Helper.*
import com.ako.hidemyvideo.R
import com.ako.hidemyvideo.adapter.AllVideosAdapter
import com.ako.hidemyvideo.databinding.ActivityPrivateBinding
import com.ako.hidemyvideo.model.VideoItem
import java.io.File

class PrivateActivity : AppCompatActivity(), AllVideosAdapter.SelectionCallback, MoveFilesCallback {
    lateinit var binding: ActivityPrivateBinding
    var selectVideos = false
    private var menuItem: MenuItem? = null
    private var unhidemenuitem: MenuItem? = null
    private var path=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "My Private Videos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val hiddenfolder = File(getRootDirPath(this), ".hide")
        val data = getFilesDetails(this, hiddenfolder.absolutePath)
        Log.d("Checkfile", data.toString())
        binding.recyclerPrivateVideo.apply {
            layoutManager = GridLayoutManager(this@PrivateActivity, 2)
            adapter =
                AllVideosAdapter(
                    this@PrivateActivity,
                    data as ArrayList<VideoItem>,
                    null,
                    this@PrivateActivity,
                    true
                )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        menuItem = menu.findItem(R.id.add)
        unhidemenuitem = menu.findItem(R.id.un_hide)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(FROMHIDE, "FROMHIDE")
                startActivity(intent)
            }
            R.id.un_hide ->{
                HideFileTask(this,path,this,true).execute()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun updateMenuItemIcon(isChecked: Boolean) {
//        val iconResId = if (isChecked) R.drawable.twotone_done else R.drawable.add_circle
//        menuItem?.setIcon(iconResId)
        if (isChecked) {
            menuItem?.isVisible = false
            unhidemenuitem?.isVisible = true
        } else {
            menuItem?.isVisible=true
            unhidemenuitem?.isVisible = false
        }
    }

    override fun onItemSelectionChanged(selectedItems: List<VideoItem>) {
        selectedItems.forEach {
            path.add(it.videoPath)
        }
        if (selectedItems.isNotEmpty()) {
            selectVideos = true
            if (selectedItems.size == 1) supportActionBar?.title =
                "${selectedItems.size} Video selected"
            else supportActionBar?.title = "${selectedItems.size} Videos selected"
            updateMenuItemIcon(selectVideos)
        } else {
            selectVideos = false
            supportActionBar?.title = "My Private Videos"
            updateMenuItemIcon(selectVideos)
        }
        Log.d("CheckSelectVideo", selectedItems.size.toString())
    }

    override fun onMoveCompleted() {
        startActivity(Intent(this,MainActivity::class.java))
    }

    override fun onMoveFailed(errorMessage: String) {

    }
}