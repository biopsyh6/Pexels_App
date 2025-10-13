package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.PhotoDao
import com.example.data.mapper.PhotoDataMapper
import com.example.data.mapper.exception.toPexelsExceptionDomainModel
import com.example.data.remote.PexelsApi
import com.example.domain.TResult
import com.example.domain.model.PhotoDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.IPhotosRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class PhotosRepositoryImpl(
    private val api: PexelsApi,
    private val photoDao: PhotoDao,
    private val ioDispatcher: CoroutineDispatcher
    ) : IPhotosRepository {
        private val cacheDuration = 60 * 60 * 1000L
    override suspend fun getPhotos(
        page: Int,
        query: String?
    ): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel> =
        withContext(ioDispatcher) {
            val isCurated = query.isNullOrEmpty()
            val type = if (isCurated) "curated" else query
            val expiration = System.currentTimeMillis() - cacheDuration
            photoDao.clearExpiredPhotos(type!!, expiration)
            val local = PhotoDataMapper.toDomainList(photoDao.getCachedPhotos(type, expiration))
            if (local.isNotEmpty() && page == 1) return@withContext TResult.Success(local)
            runCatching {
                val response = if (isCurated) api.getCuratedPhotos(page) else api.searchPhotos(query!!, page)
                val items = response.photos ?: emptyList()
                if (page == 1) {
                    val entities = PhotoDataMapper.toEntityList(items, System.currentTimeMillis(), type)
                    photoDao.insertPhotos(entities)
                }
                TResult.Success<List<PhotoDomainModel>, PexelsExceptionDomainModel>(items)
            }.getOrElse {
                Log.e("PhotosRepository", "Error fetching photos: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }

    override suspend fun getPhotoDetails(id: Int): TResult<PhotoDomainModel, PexelsExceptionDomainModel> =
        withContext(ioDispatcher) {
            runCatching {
                val photo = api.getPhotoDetails(id)
                TResult.Success<PhotoDomainModel, PexelsExceptionDomainModel>(photo)
            }.getOrElse {
                Log.e("PhotosRepository", "Error fetching photo details for id $id: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }
}