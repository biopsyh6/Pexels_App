package com.example.pexels_app.ui.event


sealed interface HomeEvent {
    data class ShowToast(val message: String) : HomeEvent
    data object NavigateToDetails : HomeEvent
    data object NavigateToHome : HomeEvent
    data class ScrollToPhoto(val index: Int) : HomeEvent
}