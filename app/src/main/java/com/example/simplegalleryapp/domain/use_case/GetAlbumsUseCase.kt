package com.example.simplegalleryapp.domain.use_case

import com.example.simplegalleryapp.domain.model.Album
import com.example.simplegalleryapp.domain.repository.GalleryRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(private val galleryRepository: GalleryRepository) {

    suspend operator fun invoke(): Result<List<Album>> {
        return galleryRepository.getAlbums()
    }
}
