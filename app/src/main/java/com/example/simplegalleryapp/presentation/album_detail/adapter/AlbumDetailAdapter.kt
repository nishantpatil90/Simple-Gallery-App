package com.example.simplegalleryapp.presentation.album_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.databinding.AlbumImageItemLayoutBinding
import com.example.simplegalleryapp.presentation.album_detail.view_holder.ImageListViewHolder

class AlbumDetailAdapter(
    private val listener: (AlbumImage) -> Unit
) : PagingDataAdapter<AlbumImage, ImageListViewHolder>(AlbumImageDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val binding = AlbumImageItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageListViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
