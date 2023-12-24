package com.example.simplegalleryapp.data.data_source

import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import com.example.simplegalleryapp.domain.model.Album
import com.example.simplegalleryapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumDataSource @Inject constructor() {

    suspend fun getImagesByFolder(context: Context): List<Album> {
        return withContext(Dispatchers.IO) {
            val contentResolver = context.contentResolver
            val sortOrder = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} ASC"
            val cursor = contentResolver.query(
                /* uri = */ MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                /* projection = */ Constants.AlbumQueryProjection,
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder = */ sortOrder
            )
            cursor ?: return@withContext listOf()
            val albums = mutableListOf<Album>()
            with(cursor) {
                moveToFirst()
                while (!isAfterLast) {
                    try {
                        val album = getAlbumFromCursor()
                        val currentAlbum = albums.find { it.id == album.id }
                        if (currentAlbum == null)
                            albums.add(album)
                        else {
                            val i = albums.indexOf(currentAlbum)
                            albums[i].count++
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    moveToNext()
                }
                close()
            }
            return@withContext albums
        }
    }

    @Throws(Exception::class)
    fun Cursor.getAlbumFromCursor(): Album {
        val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
        val label: String? = try {
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
        } catch (e: Exception) {
            Build.MODEL
        }
        val thumbnailRelativePath =
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH))
        val thumbnailDate =
            getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
        return Album(
            id = id,
            label = label ?: "",
            relativePath = thumbnailRelativePath,
            timestamp = thumbnailDate,
            count = 1
        )
    }
}
