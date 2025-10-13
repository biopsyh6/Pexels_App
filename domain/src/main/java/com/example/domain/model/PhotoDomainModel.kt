package com.example.domain.model

data class PhotoDomainModel(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographerUrl: String,
    val photographerId: Int,
    val avgColor: String?,
    val src: Map<String, String>,
    val liked: Boolean,
    val alt: String
)
