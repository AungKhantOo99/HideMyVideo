package com.ako.hidemyvideo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ako.hidemyvideo.Helper.FROMHIDE
import com.ako.hidemyvideo.Helper.getLastSubstring
import com.ako.hidemyvideo.R
import com.ako.hidemyvideo.model.VideoItem
import com.ako.hidemyvideo.ui.AllVideoActivity
import java.util.ArrayList

class AllFoldersAdapter(val cont: Context,val checkfromhide:String?) : RecyclerView.Adapter<AllFoldersAdapter.VideoViewHolder>() {

    private var videoFolders: Map<String, List<VideoItem>> = emptyMap()
    fun submitListwithFolder(data : Map<String, List<VideoItem>>){
        videoFolders=data
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(cont).inflate(R.layout.folders_item, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        // Bind the data to each item

        val foldername= videoFolders.keys.elementAt(position)
        val videos = videoFolders[foldername] ?: emptyList()
        holder.bind(foldername,videos,cont,checkfromhide)
    }

    override fun getItemCount(): Int {
        return videoFolders.size
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(foldername: String, videoItem: List<VideoItem>, cont: Context,check:String?) {
            itemView.findViewById<TextView>(R.id.titleTextView).setText(getLastSubstring(foldername, "/"))
            itemView.findViewById<TextView>(R.id.count).setText("${videoItem.size} videos")
            // Set a click listener to handle item click events
            itemView.setOnClickListener {
                val intent=Intent(cont,AllVideoActivity::class.java)
                intent.putExtra(FROMHIDE,check)
                intent.putExtra("title",getLastSubstring(foldername, "/"))
                intent.putParcelableArrayListExtra("Data",videoItem as ArrayList<out Parcelable>)
                cont.startActivity(intent)
            }
        }
    }

}
