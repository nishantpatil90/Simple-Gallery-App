package com.example.simplegalleryapp.data.data_source

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.simplegalleryapp.domain.model.AlbumImage
import com.example.simplegalleryapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImagePagingSource(
    private val albumId: Long,
    private val context: Context
) : PagingSource<Int, AlbumImage>() {

    private var initialLoadSize: Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumImage> {
        return try {
            val pageNumber = params.key ?: STARTING_PAGE_INDEX

            if (params.key == null) {
                initialLoadSize = params.loadSize
            }

            // work out the offset into the database to retrieve records from the page number,
            // allow for a different load size for the first page
            val offset = if (pageNumber == 1) {
                initialLoadSize
            } else {
                ((pageNumber - 1) * params.loadSize) + (initialLoadSize - params.loadSize)
            }

            val images = queryImages(offset, params.loadSize)

            LoadResult.Page(
                data = images,
                prevKey = null,
                nextKey = if (images.size == params.loadSize) pageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun queryImages(pageNumber: Int, pageSize: Int): List<AlbumImage> {
        return withContext(Dispatchers.IO) {
            val images = ArrayList<AlbumImage>()
            val contentResolver = context.contentResolver
            createCursor(contentResolver, pageNumber, pageSize)?.use {
                it.moveToFirst()
                while (!it.isAfterLast) {
                    images.add(it.getMediaFromCursor())
                    it.moveToNext()
                }
            }
            return@withContext images
        }
    }

    private fun createCursor(
        contentResolver: ContentResolver,
        pageNumber: Int,
        pageSize: Int
    ): Cursor? {
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val selection =
            "${MediaStore.MediaColumns.BUCKET_ID}=? AND ${MediaStore.Images.Media.MIME_TYPE} LIKE ?"
        val selectionArgs = arrayOf(albumId.toString(), "image%")
        val cursor =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                contentResolver.query(
                    /* uri = */ MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    /* projection = */
                    Constants.ImagesQueryProjection,
                    /* queryArgs = */
                    getQueryBundle(selection, selectionArgs, pageNumber, pageSize),
                    /* cancellationSignal = */
                    null
                )
            } else {
                contentResolver.query(
                    /* uri = */ MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    /* projection = */ Constants.ImagesQueryProjection,
                    /* selection = */ selection,
                    /* selectionArgs = */ selectionArgs,
                    /* sortOrder = */ sortOrder
                )
            }

        return cursor
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getQueryBundle(
        selection: String,
        selectionArgs: Array<String>,
        pageNumber: Int,
        pageSize: Int
    ): Bundle {
        return Bundle().apply {
            // Limit & Offset
            putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
            putInt(ContentResolver.QUERY_ARG_OFFSET, pageNumber)
            // Sort function
            putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.Files.FileColumns.DATE_ADDED)
            )
            // Sorting direction
            putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
            // Selection
            putString(
                ContentResolver.QUERY_ARG_SQL_SELECTION,
                selection
            )
            putStringArray(
                ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                selectionArgs
            )
        }
    }

    @Throws(Exception::class)
    private fun Cursor.getMediaFromCursor(): AlbumImage {
        val id: Long =
            getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
        val path: String =
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
        val relativePath: String =
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH))
        val title: String =
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
        val albumID: Long =
            getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
        val albumLabel: String = try {
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME))
        } catch (_: Exception) {
            Build.MODEL
        }

        val modifiedTimestamp: Long =
            getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
        val duration: String? = try {
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION))
        } catch (_: Exception) {
            null
        }
        val orientation: Int =
            getInt(getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION))
        val mimeType: String =
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
        val isFavorite: Int =
            getInt(getColumnIndexOrThrow(MediaStore.MediaColumns.IS_FAVORITE))
        val isTrashed: Int =
            getInt(getColumnIndexOrThrow(MediaStore.MediaColumns.IS_TRASHED))
        val expiryTimestamp: Long? = try {
            getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_EXPIRES))
        } catch (_: Exception) {
            null
        }
        val contentUri = if (mimeType.contains("image")) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val uri = ContentUris.withAppendedId(contentUri, id)
        return AlbumImage(
            id = id,
            label = title,
            uri = uri,
            path = path,
            relativePath = relativePath,
            albumID = albumID,
            albumLabel = albumLabel,
            timestamp = modifiedTimestamp,
            expiryTimestamp = expiryTimestamp,
            duration = duration,
            favorite = isFavorite,
            trashed = isTrashed,
            orientation = orientation,
            mimeType = mimeType
        )
    }

    override fun getRefreshKey(state: PagingState<Int, AlbumImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }
}


