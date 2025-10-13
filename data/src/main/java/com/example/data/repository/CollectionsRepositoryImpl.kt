package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.CollectionDao
import com.example.data.mapper.CollectionDataMapper
import com.example.data.mapper.exception.toPexelsExceptionDomainModel
import com.example.data.remote.PexelsApi
import com.example.domain.TResult
import com.example.domain.model.CollectionDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.ICollectionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CollectionsRepositoryImpl(
    private val api: PexelsApi,
    private val collectionDao: CollectionDao,
    private val ioDispatcher: CoroutineDispatcher
) : ICollectionsRepository {
    private val cacheDuration = 60 * 60 * 1000L

    override suspend fun getFeaturedCollections(): TResult<List<CollectionDomainModel>, PexelsExceptionDomainModel> =
        withContext(ioDispatcher) {
            val expiration = System.currentTimeMillis() - cacheDuration
            collectionDao.clearExpiredCollections(expiration)
            val local = CollectionDataMapper.toDomainListFromEntity(collectionDao.getCachedCollections(expiration))
            if (local.isNotEmpty()) return@withContext TResult.Success(local)
            runCatching {
                val response = api.getFeaturedCollections(perPage = 7)
                val items = response.collections?.let { CollectionDataMapper.toDomainListFromData(it) } ?: emptyList()
                val entities = CollectionDataMapper.toEntityListFromDomain(items, System.currentTimeMillis())
                collectionDao.insertCollections(entities)
                TResult.Success<List<CollectionDomainModel>, PexelsExceptionDomainModel>(items)
            }.getOrElse {
                Log.e("CollectionsRepository", "Error fetching collections: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }
}