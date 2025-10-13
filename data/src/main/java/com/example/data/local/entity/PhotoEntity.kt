package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val photographerId: Int,
    val avgColor: String?,
    val srcJson: String, // map to json
    val liked: Boolean,
    val alt: String,
    val timestamp: Long, // for cache expiration
    val type: String // for search query
)
