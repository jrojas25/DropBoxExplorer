package com.jmr.dropboxbrowser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jmr.data.model.FileResponse
import com.jmr.domain.model.DropboxTypes
import com.jmr.dropboxbrowser.R
import com.jmr.dropboxbrowser.databinding.ItemDropboxContentBinding
import com.jmr.dropboxbrowser.util.FileThumbnailRequestHandler
import com.squareup.picasso.Picasso

class DropboxContentAdapter(
    private val dropboxContent: List<FileResponse>,
    private val picassoInstance: Picasso,
    private val clickCallback: (FileResponse) -> Unit
) :
    RecyclerView.Adapter<DropboxContentAdapter.DropboxContentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DropboxContentHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dropbox_content, parent, false)
        return DropboxContentHolder(view)
    }

    override fun onBindViewHolder(holder: DropboxContentHolder, position: Int) {
        val item = dropboxContent[position]
        holder.bind(item, picassoInstance)
        holder.itemView.setOnClickListener { clickCallback(item) }
    }

    override fun getItemCount(): Int = dropboxContent.size

    class DropboxContentHolder(dropboxContentView: View) :
        RecyclerView.ViewHolder(dropboxContentView) {
        private val binding = ItemDropboxContentBinding.bind(itemView)
        private val contentImage = binding.contentImage
        private val contentName = binding.contentName

        fun bind(item: FileResponse, picassoInstance: Picasso) {
            contentName.text = item.name
            when (item.tag) {
                DropboxTypes.FILE.value -> {
                    picassoInstance.load(FileThumbnailRequestHandler.buildPicassoUri(item.path_display))
                        .centerInside()
                        .fit()
                        .placeholder(R.drawable.ic_file_placeholder)
                        .error(R.drawable.ic_file_placeholder)
                        .into(contentImage)
                }
                DropboxTypes.FOLDER.value -> {
                    picassoInstance.load(R.drawable.ic_folder_placeholder)
                        .placeholder(R.drawable.ic_folder_placeholder)
                        .into(contentImage)
                }
                else -> {
                    picassoInstance.load(R.drawable.ic_file_placeholder)
                        .placeholder(R.drawable.ic_file_placeholder)
                        .error(R.drawable.ic_file_placeholder)
                        .into(contentImage)
                }
            }
        }
    }

}