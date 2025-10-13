package com.example.data.mapper

import com.example.data.local.entity.CollectionEntity
import com.example.data.model.CollectionDataModel
import com.example.domain.model.CollectionDomainModel

object CollectionDataMapper {
    fun toDomainModelFromData(data: CollectionDataModel): CollectionDomainModel = CollectionDomainModel(
        id = data.id,
        title = data.title,
        description = data.description,
        private = data.private,
        mediaCount = data.mediaCount,
        photosCount = data.photosCount,
        videosCount = data.videosCount
    )

    fun toDomainListFromData(dataList: List<CollectionDataModel>): List<CollectionDomainModel> =
        dataList.map { toDomainModelFromData(it) }

    fun toDomainFromEntity(entity: CollectionEntity): CollectionDomainModel =
        CollectionDomainModel(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            private = entity.private,
            mediaCount = entity.mediaCount,
            photosCount = entity.photosCount,
            videosCount = entity.videosCount
        )

    fun toDomainListFromEntity(entities: List<CollectionEntity>): List<CollectionDomainModel> =
        entities.map { toDomainFromEntity(it) }

    fun toEntityFromDomain(domain: CollectionDomainModel, timestamp: Long): CollectionEntity =
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

    fun toEntityListFromDomain(domains: List<CollectionDomainModel>, timestamp: Long): List<CollectionEntity> =
        domains.map { toEntityFromDomain(it, timestamp) }

}