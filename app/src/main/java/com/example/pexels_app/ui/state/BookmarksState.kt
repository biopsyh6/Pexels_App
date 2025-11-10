package com.example.pexels_app.ui.state

import com.example.domain.model.PhotoDomainModel

sealed interface BookmarksState {
    object Loading : BookmarksState
    data class Success(
        val photos: List<PhotoDomainModel>,
        val isLoadingMore: Boolean = false
    ) : BookmarksState
    object Empty : BookmarksState
    data class Error(val exception: String) : BookmarksState
}