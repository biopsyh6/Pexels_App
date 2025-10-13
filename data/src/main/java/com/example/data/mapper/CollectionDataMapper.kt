package com.example.data.mapper

import com.example.data.local.entity.CollectionEntity
import com.example.domain.model.CollectionDomainModel

object CollectionDataMapper {
    fun toDomain(entity: CollectionEntity): CollectionDomainModel =
        CollectionDomainModel(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            private = entity.private,
            mediaCount = entity.mediaCount,
            photosCount = entity.photosCount,
            videosCount = entity.videosCount
        )

    fun toEntity(domain: CollectionDomainModel, timestamp: Long): CollectionEntity =
        CollectionEntity(
            id = domain.id,
            title = domain.title,
            description = domain.description,
            private = domain.private,
            mediaCount = domain.mediaCount,
            photosCount = domain.photosCount,
            videosCount = domain.videosCount,
            timestamp = timestamp
        )

    fun toDomainList(entities: List<CollectionEntity>): List<CollectionDomainModel> = entities.map { toDomain(it) }
    fun toEntityList(domains: List<CollectionDomainModel>, timestamp: Long): List<CollectionEntity> =
        domains.map { toEntity(it, timestamp) }
}