package com.ako.hidemyvideo.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ako.hidemyvideo.Helper.FROMHIDE
import com.ako.hidemyvideo.Helper.HideFileTask
import com.ako.hidemyvideo.Helper.MoveFilesCallback
import com.ako.hidemyvideo.adapter.AllVideosAdapter
import com.ako.hidemyvideo.databinding.ActivityAllVideoBinding
import com.ako.hidemyvideo.model.VideoItem

class AllVideoActivity : AppCompatActivity(),AllVideosAdapter.SelectionCallback,MoveFilesCallback {
    lateinit var binding: ActivityAllVideoBinding
    var checkfromhide:String?=null
    lateinit var adapter: AllVideosAdapter
    var TAG="VideoInFolderActivity"
    lateinit var title: String
    companion object{
        var deletePermissioin:Boolean=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAllVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title=intent.getStringExtra("title")!!
        supportActionBar?.title=title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val receivedList = intent.getParcelableArrayListExtra<Parcelable>("Data")
        Log.d("Check",receivedList?.size.toString())
        checkfromhide=intent.getStringExtra(FROMHIDE)
        adapter=AllVideosAdapter(this@AllVideoActivity,receivedList as ArrayList<VideoItem>,checkfromhide,this,false)
        binding.allVideo.layoutManager=GridLayoutManager(this@AllVideoActivity,2)
        binding.allVideo.adapter=adapter
    //    binding.allVideo.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }
    override fun onResume() {
        super.onResume()
        if(checkfromhide!=null){
            if(!deletePermissioin)
                checkAndRequestPermission()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemSelectionChanged(selectedItems: List<VideoItem>) {
        val path= ArrayList<String>()

        selectedItems.forEach {
            path.add(it.videoPath)
        }
       // Log.d(TAG,path.toString())
        if(checkfromhide != null && selectedItems.isNotEmpty()){
            val selectvideocount=selectedItems.size
            if(selectvideocount==1) supportActionBar?.title= "${selectedItems.size} Video selected"
            else supportActionBar?.title= "${selectedItems.size} Videos selected"
            binding.hideVideo.visibility=View.VISIBLE

            binding.hideVideo.setOnClickListener {

                if(!deletePermissioin){
                    checkAndRequestPermission()
                }else{
                    HideFileTask(this,path,this,false).execute()
                }
            }
        }else{
            supportActionBar?.title= title
            binding.hideVideo.visibility=View.GONE
        }

        Log.d(TAG,selectedItems.toString())
    }

    override fun onMoveCompleted() {
        Log.d(TAG,"Success")

        startActivity(Intent(this,PrivateActivity::class.java))
    }

    private val DELETE_PERMISSION_REQUEST_CODE = 1002

    private fun checkAndRequestPermission()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, DELETE_PERMISSION_REQUEST_CODE)
        } else {
            deletePermissioin=true
            // For devices below Android 12, no special permission is required
        //    deleteVideo()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted or not
            val permissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                TODO("VERSION.SDK_INT < R")
            }
            deletePermissioin = Environment.isExternalStorageManager()
        }
    }
    override fun onMoveFailed(errorMessage: String) {
        Log.d(TAG,"Fail")

    }
}