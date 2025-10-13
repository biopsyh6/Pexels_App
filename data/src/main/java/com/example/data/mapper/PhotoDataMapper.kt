package com.example.data.mapper

import com.example.data.local.entity.PhotoEntity
import com.example.data.model.PhotoDataModel
import com.example.domain.model.PhotoDomainModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PhotoDataMapper {
    fun toDomainModelFromData(data: PhotoDataModel): PhotoDomainModel = PhotoDomainModel(
        id = data.id,
        width = data.width,
        height = data.height,
        url = data.url,
        photographer = data.photographer,
        photographerUrl = data.photographerUrl,
        photographerId = data.photographerId,
        avgColor = data.avgColor,
        src = data.src,
        liked = data.liked,
        alt = data.alt,
        isBookmarked = false
    )

    fun toDomainListFromData(dataList: List<PhotoDataModel>): List<PhotoDomainModel> =
        dataList.map { toDomainModelFromData(it) }

    fun toDomainModelFromEntity(entity: PhotoEntity): PhotoDomainModel {
        val srcType = object : TypeToken<Map<String, String>>() {}.type
        val src: Map<String, String> = Gson().fromJson(entity.srcJson, srcType)
        return PhotoDomainModel(
            id = entity.id,
            width = entity.width,
            height = entity.height,
            url = entity.url,
            photographer = entity.photographer,
            photographerUrl = entity.photographerUrl,
            photographerId = entity.photographerId,
            avgColor = entity.avgColor,
            src = src,
            liked = entity.liked,
            alt = entity.alt,
            isBookmarked = entity.isBookmarked
        )
    }

    fun toEntityFromDomain(domain: PhotoDomainModel, timestamp: Long, type: String): PhotoEntity {
        val srcJson = Gson().toJson(domain.src)
        return PhotoEntity(
            id = domain.id,
            width = domain.width,
            height = domain.height,
            url = domain.url,
            photographer = domain.photographer,
            photographerUrl = domain.photographerUrl,
            photographerId = domain.photographerId,
            avgColor = domain.avgColor,
            srcJson = srcJson,
            liked = domain.liked,
            alt = domain.alt,
            timestamp = timestamp,
            type = type,
            isBookmarked = domain.isBookmarked
        )
    }


    fun toDomainListFromEntity(entities: List<PhotoEntity>): List<PhotoDomainModel> = entities.map { toDomainModelFromEntity(it) }
    fun toEntityListFromDomain(domains: List<PhotoDomainModel>, timestamp: Long, type: String): List<PhotoEntity> =
        domains.map { toEntityFromDomain(it, timestamp, type) }
}