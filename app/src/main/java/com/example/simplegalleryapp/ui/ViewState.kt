package com.example.simplegalleryapp.ui

sealed interface ViewState {

    data object LaunchAlbumListScreen : ViewState
    data object RequestPermission : ViewState
}
