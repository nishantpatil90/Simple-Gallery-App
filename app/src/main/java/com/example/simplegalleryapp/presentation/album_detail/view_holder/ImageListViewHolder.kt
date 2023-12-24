package com.example.simplegalleryapp.presentation.album_detail.view_holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.databinding.AlbumImageItemLayoutBinding

class ImageListViewHolder(
    private val binding: AlbumImageItemLayoutBinding,
    private val listener: (AlbumImage) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: AlbumImage) {
        Glide.with(binding.imageItem)
            .load(model.uri)
            .into(binding.imageItem)

        binding.imageItemContainer.setOnClickListener {
            listener(model)
        }
    }
}
