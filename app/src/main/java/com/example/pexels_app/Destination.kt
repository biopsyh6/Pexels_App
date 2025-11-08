package com.example.pexels_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

enum class Destination(
    val route: String,
    val label: String,
    val inactiveIcon: @Composable () -> Painter,
    val activeIcon: @Composable () -> Painter,
    val contentDescription: String
) {
    HOME(
        route = "home",
        label = "Home",
        inactiveIcon = { painterResource(id = R.drawable.home_button_inactive) },
        activeIcon = { painterResource(id = R.drawable.home_button_active) },
        contentDescription = "Home Screen"
    ),
    BOOKMARKS(
        route = "bookmarks",
        label = "Bookmarks",
        inactiveIcon = { painterResource(id = R.drawable.bookmark) },
        activeIcon = { painterResource(id = R.drawable.bookmark_red) },
        contentDescription = "Bookmarks"
    )
}