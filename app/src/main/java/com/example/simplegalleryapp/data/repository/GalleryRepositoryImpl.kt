package com.example.simplegalleryapp.data.repository

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.simplegalleryapp.data.data_source.AlbumDataSource
import com.example.simplegalleryapp.data.data_source.ImagePagingSource
import com.example.simplegalleryapp.domain.model.Album
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.domain.repository.GalleryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GalleryRepositoryImpl(
    private val albumDataSource: AlbumDataSource,
    private val context: Context
) : GalleryRepository {

    override suspend fun getAlbums(): Result<List<Album>> {
        return safeCall {
            albumDataSource.getImagesByFolder(context)
        }
    }

    override fun getImageByAlbumId(albumId: Long): Flow<PagingData<AlbumImage>> {
        val pagingConfig = PagingConfig(
            pageSize = IMAGE_PAGE_SIZE,
        )
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { ImagePagingSource(albumId, context) }
        ).flow
    }

    private suspend fun <T> safeCall(call: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(call.invoke())
            } catch (throwable: Throwable) {
                Result.failure(throwable)
            }
        }
    }

    companion object {
        const val IMAGE_PAGE_SIZE = 20
    }
}
