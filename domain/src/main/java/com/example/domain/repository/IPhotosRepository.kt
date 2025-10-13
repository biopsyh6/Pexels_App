package com.example.domain.repository

import com.example.domain.TResult
import com.example.domain.model.PhotoDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel


interface IPhotosRepository {
    suspend fun getPhotos(page: Int, query: String?): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel>
    suspend fun getPhotoDetails(id: Int): TResult<PhotoDomainModel, PexelsExceptionDomainModel>
    suspend fun getBookmarkedPhoto(id: Int): TResult<PhotoDomainModel, PexelsExceptionDomainModel>
    suspend fun toggleBookmark(id: Int, isBookmarked: Boolean): TResult<Unit, PexelsExceptionDomainModel>
    suspend fun getBookmarkedPhotos(offset: Int, limit: Int): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel>
}