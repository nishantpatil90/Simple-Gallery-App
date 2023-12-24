/*
 * SPDX-FileCopyrightText: 2023 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.simplegalleryapp.injection

import android.content.Context
import com.example.simplegalleryapp.data.data_source.AlbumDataSource
import com.example.simplegalleryapp.data.repository.GalleryRepositoryImpl
import com.example.simplegalleryapp.domain.repository.GalleryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMediaRepository(
        albumDataSource: AlbumDataSource,
        @ApplicationContext context: Context
    ): GalleryRepository {
        return GalleryRepositoryImpl(albumDataSource, context)
    }
}
