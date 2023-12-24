package com.example.simplegalleryapp.domain.repository

import androidx.paging.PagingData
import com.example.simplegalleryapp.domain.model.Album
import com.example.simplegalleryapp.domain.model.AlbumImage
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {

    suspend fun getAlbums(): Result<List<Album>>

    fun getImageByAlbumId(albumId: Long): Flow<PagingData<AlbumImage>>
}
