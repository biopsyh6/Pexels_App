package com.example.pexels_app.ui.intent

sealed interface BookmarksIntent {
    data object LoadBookmarks : BookmarksIntent
    data object LoadMore : BookmarksIntent
    data class PhotoClicked(val id: Int) : BookmarksIntent
    data object NavigateToHome : BookmarksIntent
}