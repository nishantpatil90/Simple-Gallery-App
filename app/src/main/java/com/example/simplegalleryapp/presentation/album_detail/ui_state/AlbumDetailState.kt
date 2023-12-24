package com.example.simplegalleryapp.presentation.album_detail.ui_state

import com.example.simplegalleryapp.domain.model.AlbumImage

sealed interface AlbumDetailState {

    data class LaunchImageDetailScreen(val albumImage: AlbumImage) : AlbumDetailState
}
