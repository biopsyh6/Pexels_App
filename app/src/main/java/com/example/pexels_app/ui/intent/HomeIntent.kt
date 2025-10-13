package com.example.pexels_app.ui.intent

sealed interface HomeIntent {
    data class Search(val query: String) : HomeIntent
    data object Explore : HomeIntent
    data object LoadMore : HomeIntent
    data class PhotoClicked(val id: Int) : HomeIntent
}