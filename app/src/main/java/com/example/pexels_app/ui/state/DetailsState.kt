package com.example.pexels_app.ui.state

import com.example.domain.model.PhotoDomainModel

sealed interface DetailsState {
    object Loading : DetailsState
    data class Success(val photo: PhotoDomainModel) : DetailsState
    data class Error(val message: String) : DetailsState
}