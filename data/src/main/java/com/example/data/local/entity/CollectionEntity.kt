package com.example.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey var id: String = "",
    var title: String = "",
    var description: String = "",
    var private: Boolean = false,
    var mediaCount: Int = 0,
    var photosCount: Int = 0,
    var videosCount: Int = 0,
    var timestamp: Long = 0
)
