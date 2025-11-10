package com.example.pexels_app.ui.event

sealed interface BookmarksEvent {
    data class ShowToast(val message: String) : BookmarksEvent
    data object NavigateToDetails : BookmarksEvent
    data object NavigateToHome : BookmarksEvent
    data object BookmarkedChanged : BookmarksEvent
}