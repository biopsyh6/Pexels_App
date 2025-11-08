package com.example.pexels_app.ui.intent

sealed interface DetailsIntent {
    object Download : DetailsIntent
    object ToggleBookmark : DetailsIntent
    object NavigateBack : DetailsIntent
}