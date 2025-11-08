package com.example.pexels_app.di

import com.example.data.local.AppDatabase
import com.example.data.remote.AuthInterceptor
import com.example.data.remote.PexelsApi
import com.example.data.repository.CollectionsRepositoryImpl
import com.example.data.repository.PhotosRepositoryImpl
import com.example.domain.repository.ICollectionsRepository
import com.example.domain.repository.IPhotosRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().photoDao() }
    single { get<AppDatabase>().collectionDao() }

    single { "5YejkjGIGmLaOJLiDTfEbnkIXvg1idlw6wRA2b3JLaUShjo98VeDcGSh" }
    single<AuthInterceptor> { AuthInterceptor(get()) }
    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<PexelsApi> { get<Retrofit>().create(PexelsApi::class.java) }

    single<ICollectionsRepository> {
        CollectionsRepositoryImpl(
            api = get<PexelsApi>(),
            collectionDao = get(),
            ioDispatcher = get()
        )
    }
    single<IPhotosRepository> {
        PhotosRepositoryImpl(
            api = get<PexelsApi>(),
            photoDao = get(),
            ioDispatcher = get()
        )
    }

//    single { Dispatchers.IO }
}