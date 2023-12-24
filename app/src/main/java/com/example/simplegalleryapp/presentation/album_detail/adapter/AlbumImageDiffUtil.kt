package com.example.simplegalleryapp.presentation.album_detail.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.simplegalleryapp.domain.model.AlbumImage

class AlbumImageDiffUtil : DiffUtil.ItemCallback<AlbumImage>() {
    override fun areItemsTheSame(oldItem: AlbumImage, newItem: AlbumImage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlbumImage, newItem: AlbumImage): Boolean {
        return oldItem == newItem
    }
}
