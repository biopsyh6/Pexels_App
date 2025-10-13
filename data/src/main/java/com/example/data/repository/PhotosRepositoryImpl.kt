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
import kotlinx.coroutines.flow.first
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
            val local = PhotoDataMapper.toDomainListFromEntity(photoDao.getCachedPhotos(type, expiration))
            if (local.isNotEmpty() && page == 1) return@withContext TResult.Success(local)
            runCatching {
                val response = if (isCurated) api.getCuratedPhotos(page) else api.searchPhotos(query!!, page)
                val items = response.photos?.let { PhotoDataMapper.toDomainListFromData(it) } ?: emptyList()
                if (page == 1) {
                    val entities = PhotoDataMapper.toEntityListFromDomain(items, System.currentTimeMillis(), type)
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
                TResult.Success<PhotoDomainModel, PexelsExceptionDomainModel>(
                    PhotoDataMapper.toDomainModelFromData(photo)
                )
            }.getOrElse {
                Log.e("PhotosRepository", "Error fetching photo details for id $id: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }

    override suspend fun getBookmarkedPhoto(id: Int): TResult<PhotoDomainModel, PexelsExceptionDomainModel> =
        withContext(ioDispatcher) {
            runCatching {
                val entity = photoDao.getPhotoById(id)
                TResult.Success<PhotoDomainModel, PexelsExceptionDomainModel>(
                    PhotoDataMapper.toDomainModelFromEntity(entity!!)
                )
            }.getOrElse {
                Log.e("PhotosRepository", "Error fetching bookmarked photo: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }

    override suspend fun toggleBookmark(
        id: Int,
        isBookmarked: Boolean
    ): TResult<Unit, PexelsExceptionDomainModel> =
        withContext(ioDispatcher) {
            runCatching {
                val existing = photoDao.getPhotoById(id)
                if (existing == null && isBookmarked) {
                    val resp = api.getPhotoDetails(id)
                    val domain = PhotoDataMapper.toDomainModelFromData(resp)
                    val entity = PhotoDataMapper.toEntityFromDomain(domain, System.currentTimeMillis(), "bookmark")
                    photoDao.insertPhotos(listOf(entity.copy(isBookmarked = true)))
                } else {
                    photoDao.updateBookmarkStatus(id, isBookmarked)
                }
                TResult.Success<Unit, PexelsExceptionDomainModel>(Unit)
            }.getOrElse {
                Log.e("PhotosRepository", "Error toggling bookmark: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }

    override suspend fun getBookmarkedPhotos(
        offset: Int,
        limit: Int
    ): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel> =
        withContext(ioDispatcher) {
            runCatching {
                val entities = photoDao.getBookmarkedPhotos(offset, limit)
                TResult.Success<List<PhotoDomainModel>, PexelsExceptionDomainModel>(PhotoDataMapper.toDomainListFromEntity(entities))
            }.getOrElse {
                Log.e("PhotosRepository", "Error fetching bookmarked photos: ${it.stackTraceToString()}")
                TResult.Error(it.toPexelsExceptionDomainModel())
            }
        }

}