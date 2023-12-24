package com.example.simplegalleryapp.presentation.album_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.domain.use_case.GetAlbumDetailUseCase
import com.example.simplegalleryapp.presentation.album_detail.ui_state.AlbumDetailState
import com.example.simplegalleryapp.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAlbumDetailUseCase: GetAlbumDetailUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<Event<AlbumDetailState?>> = MutableStateFlow(Event(null))
    val state = _state.asStateFlow()

    private val albumId = savedStateHandle.get<Long>(ALBUM_ID) ?: -1
    val albumName = savedStateHandle.get<String>(ALBUM_NAME) ?: ""

    val albumImageList: Flow<PagingData<AlbumImage>> =
        getAlbumDetailUseCase(albumId).cachedIn(viewModelScope)

    fun handleImageClick(albumImage: AlbumImage) {
        _state.value = Event(AlbumDetailState.LaunchImageDetailScreen(albumImage))
    }

    companion object {
        const val ALBUM_ID = "album_id"
        const val ALBUM_NAME = "album_name"
    }
}
