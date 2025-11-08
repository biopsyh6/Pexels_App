package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey var id: Int = 0,
    var width: Int = 0,
    var height: Int = 0,
    var url: String = "",
    var photographer: String = "",
    var photographerUrl: String? = null,
    var photographerId: Int = 0,
    var avgColor: String? = "",
    var srcJson: String = "", // map to json
    var liked: Boolean = false,
    var alt: String = "",
    var timestamp: Long = 0, // for cache expiration
    var type: String = "", // for search query
    var isBookmarked: Boolean = false
)
