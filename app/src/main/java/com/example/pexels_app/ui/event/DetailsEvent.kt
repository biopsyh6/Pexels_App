package com.example.pexels_app.ui.event

sealed interface DetailsEvent {
    data object NavigateBack : DetailsEvent
    data class ShowToast(val message: String) : DetailsEvent
}