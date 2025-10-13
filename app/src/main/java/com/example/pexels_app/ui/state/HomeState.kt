package com.example.pexels_app.ui.state

import com.example.domain.model.CollectionDomainModel
import com.example.domain.model.PhotoDomainModel

sealed interface HomeState {
    object Loading : HomeState
    data class Success(
        val collections: List<CollectionDomainModel>,
        val photos: List<PhotoDomainModel>,
        val isLoadingMore: Boolean = false
    ) : HomeState
    data class Error(val exception: String? = null) : HomeState
    object Empty : HomeState
    data class NetworkStub(val lastQuery: String? = null) : HomeState
}