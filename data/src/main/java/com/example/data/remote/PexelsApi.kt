package com.example.data.remote

import com.example.domain.model.CollectionDomainModel
import com.example.domain.model.PhotoDomainModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApi {
    @GET("v1/curated")
    suspend fun getCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PexelsResponse<PhotoDomainModel>

    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PexelsResponse<PhotoDomainModel>

    @GET("v1/collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 7
    ): PexelsResponse<CollectionDomainModel>

    @GET("v1/photos/{id}")
    suspend fun getPhotoDetails(@Path("id") id: Int): PhotoDomainModel


    data class PexelsResponse<T>(
        val page: Int,
        val per_page: Int,
        val photos: List<T>? = null,
        val collections: List<T>? = null,
        val total_results: Int? = null,
        val next_page: String? = null,
        val prev_page: String? = null
    )
}