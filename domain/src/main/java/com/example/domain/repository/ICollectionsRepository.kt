package com.example.domain.repository

import com.example.domain.TResult
import com.example.domain.model.CollectionDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel

interface ICollectionsRepository {
    suspend fun getFeaturedCollections(): TResult<List<CollectionDomainModel>, PexelsExceptionDomainModel>
}