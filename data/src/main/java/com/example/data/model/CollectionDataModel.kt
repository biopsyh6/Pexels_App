package com.example.data.model

data class CollectionDataModel(
    val id: String,
    val title: String,
    val description: String,
    val private: Boolean,
    val mediaCount: Int,
    val photosCount: Int,
    val videosCount: Int
)
