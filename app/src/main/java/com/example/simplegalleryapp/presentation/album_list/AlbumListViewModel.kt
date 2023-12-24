package com.example.simplegalleryapp.presentation.album_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplegalleryapp.domain.model.Album
import com.example.simplegalleryapp.domain.use_case.GetAlbumsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val getAlbumUseCase: GetAlbumsUseCase
) : ViewModel() {

    private val _albumList: MutableStateFlow<List<Album>> = MutableStateFlow(listOf())
    val albumsList = _albumList.asStateFlow()

    fun fetchAlbums() {
        viewModelScope.launch {
            val result = getAlbumUseCase()
            result.onSuccess {
                if (it.isEmpty()) {
                    return@launch
                }
                _albumList.value = it
            }
            result.onFailure {
                it.printStackTrace()
            }
        }
    }
}
