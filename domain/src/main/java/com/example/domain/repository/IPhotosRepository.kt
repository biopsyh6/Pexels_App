package com.example.domain.repository

import com.example.domain.TResult
import com.example.domain.model.PhotoDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel

interface IPhotosRepository {
    suspend fun getPhotos(page: Int, query: String?): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel>
    suspend fun getPhotoDetails(id: Int): TResult<PhotoDomainModel, PexelsExceptionDomainModel>
}