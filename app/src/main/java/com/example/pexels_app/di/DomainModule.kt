package com.example.pexels_app.di

import com.example.domain.repository.ICollectionsRepository
import com.example.domain.repository.IPhotosRepository
import com.example.domain.usecase.GetBookmarkedPhotoUseCase
import com.example.domain.usecase.GetFeaturedCollectionsUseCase
import com.example.domain.usecase.GetPhotoDetailsUseCase
import com.example.domain.usecase.GetPhotosUseCase
import com.example.domain.usecase.ToggleBookmarkUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<GetPhotosUseCase> {
        GetPhotosUseCase(get<IPhotosRepository>())
    }

    factory<GetPhotoDetailsUseCase> {
        GetPhotoDetailsUseCase(get<IPhotosRepository>())
    }

    factory<GetFeaturedCollectionsUseCase> {
        GetFeaturedCollectionsUseCase(get<ICollectionsRepository>())
    }

    factory<GetBookmarkedPhotoUseCase> {
        GetBookmarkedPhotoUseCase(get<IPhotosRepository>())
    }

    factory<ToggleBookmarkUseCase> {
        ToggleBookmarkUseCase(get<IPhotosRepository>())
    }
}