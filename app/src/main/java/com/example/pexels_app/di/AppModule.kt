package com.example.pexels_app.di

import android.app.DownloadManager
import android.content.Context
import com.example.pexels_app.ui.viewmodel.BookmarksViewModel
import com.example.pexels_app.ui.viewmodel.DetailsViewModel
import com.example.pexels_app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
//    single { androidContext() }
    single { Dispatchers.IO }
    viewModel<HomeViewModel> {
        HomeViewModel(
            getPhotosUseCase = get(),
            getPhotoDetailsUseCase = get(),
            getFeaturedCollectionsUseCase = get(),
            ioDispatcher = get()
        )
    }
    viewModel<DetailsViewModel> {
        DetailsViewModel(
            getPhotoDetailsUseCase = get(),
            getBookmarkedPhotoUseCase = get(),
            toggleBookmarkUseCase = get(),
            downloadManager = get(),
            ioDispatcher = get(),
            savedStateHandle = get()
        )
    }

    viewModel<BookmarksViewModel> {
        BookmarksViewModel(
            getBookmarkedPhotosUseCase = get(),
            getPhotoDetailsUseCase = get(),
            ioDispatcher = get()
        )
    }

    single { get<Context>().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager }
}