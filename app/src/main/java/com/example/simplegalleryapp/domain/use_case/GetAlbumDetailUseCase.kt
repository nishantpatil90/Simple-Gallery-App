package com.example.simplegalleryapp.domain.use_case

import androidx.paging.PagingData
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.domain.repository.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlbumDetailUseCase @Inject constructor(private val galleryRepository: GalleryRepository) {

    operator fun invoke(albumID: Long): Flow<PagingData<AlbumImage>> {
        return galleryRepository.getImageByAlbumId(albumID)
    }
}
