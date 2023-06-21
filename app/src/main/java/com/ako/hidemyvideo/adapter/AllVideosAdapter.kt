package com.ako.hidemyvideo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ako.hidemyvideo.Helper.MoveFilesCallback
import com.ako.hidemyvideo.Helper.retrieveVideoDuration
import com.ako.hidemyvideo.R
import com.ako.hidemyvideo.databinding.VideosItemsBinding
import com.ako.hidemyvideo.model.VideoItem
import com.ako.hidemyvideo.ui.PlayVideoActivity
import com.bumptech.glide.Glide


class AllVideosAdapter(
    val context: Context,
    val videodata: ArrayList<VideoItem>,
    val checkfromhide: String?,
    private val selectionCallback: SelectionCallback,
    private val fromPrivateActivity: Boolean
) : RecyclerView.Adapter<AllVideosAdapter.ViewHolder>(), MoveFilesCallback {
    lateinit var binding: VideosItemsBinding
    var isSelectionModeEnabled = false
    private val selectedItems: MutableList<VideoItem> = mutableListOf()

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView){
        fun bind(videodata: VideoItem) {
            itemView.setOnClickListener {
                toggleItemSelection(videodata)
                selectionCallback.onItemSelectionChanged(selectedItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllVideosAdapter.ViewHolder {
        binding = VideosItemsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: AllVideosAdapter.ViewHolder, position: Int) {

        binding.title.setText(videodata[position].title)

        /**** hide size becoz i am using gridlayout
        binding.size.setText(retrieveVideoSize(videodata[position].videoPath)) ***/

        Glide.with(context)
            .load(videodata[position].thumbnailPath)
            .into(binding.thumbnailImageView)
//        val retriever = MediaMetadataRetriever()
//        retriever.setDataSource(File(videodata[position].videoPath).absolutePath)
//        val videoDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if(videodata[position].videoDuration!=null && videodata[position].videoDuration!!.isNotEmpty()){
                binding.length.setText(retrieveVideoDuration(videodata[position].videoDuration!!.toLong()))
                Log.d("Checkduration",videodata[position].videoDuration!!)
            }else{
//                Log.d("Checkduration",videodata[position].videoDuration!!)
                binding.length.setText(retrieveVideoDuration(0))
            }

    //    binding.length.setText(fetchVideoDuration(videodata[position].videoPath).toString())
        if(fromPrivateActivity){
            val item = videodata[position]

            // long click to unhide video
            binding.root.setOnLongClickListener {
                isSelectionModeEnabled=true
                toggleItemSelection(videodata[position])
                selectionCallback.onItemSelectionChanged(selectedItems)
                notifyDataSetChanged()
                true
            }
            if(selectedItems.size==0) isSelectionModeEnabled=false
            holder.itemView.setOnClickListener {
                if (isSelectionModeEnabled) {
                    toggleItemSelection(videodata[position])
                    selectionCallback.onItemSelectionChanged(selectedItems)
                    // Toggle the selection state of the item
                    notifyDataSetChanged()
                } else {
                    val intent = Intent(context, PlayVideoActivity::class.java)
                    intent.putExtra("Video_data", videodata[position])
                    context.startActivity(intent)
                }
            }

            // Update the item's appearance based on the selection mode and selection state
            if (isSelectionModeEnabled && item.isSelected) {
                // Apply the selected item styling
                val transparentBlueColor = Color.parseColor("#800000FF") // Transparent blue color (ARGB format)
                holder.itemView.findViewById<ImageView>(R.id.thumbnailImageView).foreground = ColorDrawable(transparentBlueColor)
                holder.itemView.findViewById<ImageView>(R.id.add_video).setImageResource(R.drawable.check_circle)
            } else {
                // Apply the default item styling
                holder.itemView.findViewById<ImageView>(R.id.thumbnailImageView).foreground = null
                holder.itemView.findViewById<ImageView>(R.id.add_video).setImageResource(R.drawable.unchecked_circle)
            }

        }
        // check from hide or not to select video to hide
        else if (checkfromhide != null) {
            val layoutParams = binding.title.layoutParams as ViewGroup.MarginLayoutParams
            val endMargin = 64
            layoutParams.marginEnd = endMargin
            binding.title.layoutParams = layoutParams
            binding.addVideo.visibility = View.VISIBLE

                val item = videodata[position]
                holder.bind(item)
                if (item.isSelected) {
                    val transparentBlueColor = Color.parseColor("#800000FF") // Transparent blue color (ARGB format)
                    holder.itemView.findViewById<ImageView>(R.id.thumbnailImageView).foreground = ColorDrawable(transparentBlueColor)
                   holder.itemView.findViewById<ImageView>(R.id.add_video).setImageResource(R.drawable.check_circle)
                } else {
                    holder.itemView.findViewById<ImageView>(R.id.thumbnailImageView).foreground = null
                    holder.itemView.findViewById<ImageView>(R.id.add_video).setImageResource(R.drawable.unchecked_circle)
                }

                //  HideFileTask(context,videodata[position].videoPath,videodata[position].title.getEncryptedFileNameFromhidePath(),this).execute()
        }
        //Play video if not from hide
        else{
            binding.root.setOnClickListener {
                val intent = Intent(context, PlayVideoActivity::class.java)
                intent.putExtra("Video_data", videodata[position])
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return videodata.size
    }

    // use call back to hide select video from current activity
    interface SelectionCallback {
        fun onItemSelectionChanged(selectedItems: List<VideoItem>)
    }

    //add select item to selectedItems list
    @SuppressLint("NotifyDataSetChanged")
    fun toggleItemSelection(item: VideoItem) {
        item.isSelected = !item.isSelected
        if (item.isSelected) {
            selectedItems.add(item)
        } else {
           selectedItems.remove(item)
        }
         notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onMoveCompleted() {
        notifyDataSetChanged()
    }

    override fun onMoveFailed(errorMessage: String) {

    }

}