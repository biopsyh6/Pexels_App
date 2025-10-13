package com.example.pexels_app.di

import com.example.domain.usecase.GetFeaturedCollectionsUseCase
import com.example.domain.usecase.GetPhotoDetailsUseCase
import com.example.domain.usecase.GetPhotosUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<GetPhotosUseCase> {
        GetPhotosUseCase(get())
    }

    factory<GetPhotoDetailsUseCase> {
        GetPhotoDetailsUseCase(get())
    }

    factory<GetFeaturedCollectionsUseCase> {
        GetFeaturedCollectionsUseCase(get())
    }
}