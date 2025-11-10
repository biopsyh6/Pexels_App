package com.example.pexels_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pexels_app.ui.screens.BookmarksScreen
import com.example.pexels_app.ui.screens.DetailsScreen
import com.example.pexels_app.ui.screens.HomeScreen
import com.example.pexels_app.ui.theme.Pexels_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            Pexels_AppTheme {
                Main()
            }
        }
    }
}


@Composable
fun Main() {
    val navController = rememberNavController()
    val destinations = listOf(Destination.HOME, Destination.BOOKMARKS)
//    var selectedDestination by rememberSaveable { mutableIntStateOf(Destination.HOME.ordinal) }

    val currentRoute = currentRoute(navController)
    val showBottomBar =
        currentRoute == Destination.HOME.route || currentRoute == Destination.BOOKMARKS.route

    Scaffold(
        containerColor = colorResource(id = R.color.white),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = colorResource(R.color.white),
                    modifier = Modifier.height(85.dp)
                ) {
                    destinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Box(
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    if (currentRoute == destination.route) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .offset(y = (-16).dp)
                                                .height(3.dp)
                                                .width(24.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(colorResource(id = R.color.red))
                                        )
                                    }
                                    Icon(
                                        painter = if (currentRoute == destination.route) {
                                            destination.activeIcon()
                                        } else {
                                            destination.inactiveIcon()
                                        },
                                        contentDescription = destination.contentDescription,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Destination.HOME.route,
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
//                    top = contentPadding.calculateTopPadding(),
                        end = contentPadding.calculateEndPadding(LocalLayoutDirection.current)
                    )
                )
                .background(colorResource(id = R.color.white))
        ) {
            composable(Destination.HOME.route) {
                HomeScreen(navController = navController)
            }
            composable(Destination.BOOKMARKS.route) {
                BookmarksScreen(navController = navController)
            }
            composable("details/{photoId}?isFromBookmarks={isFromBookmarks}",
                arguments = listOf(
                    navArgument("photoId") { type = NavType.IntType },
                    navArgument("isFromBookmarks") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                DetailsScreen(
                    navController = navController,
                    photoId = backStackEntry.arguments?.getInt("photoId") ?: -1,
                    isFromBookmarks = backStackEntry.arguments?.getBoolean("isFromBookmarks")
                        ?: false
                )
            }
        }
    }

//    NavHost(navController = navController, startDestination = "home") {
//        composable("home") { HomeScreen(navController) }
//        composable("details/{photoId}?isFromBookmarks={isFromBookmarks}",
//            arguments = listOf(
//                navArgument("photoId") { type = NavType.IntType },
//                navArgument("isFromBookmarks") { type = NavType.BoolType; defaultValue = false }
//            )
//        ) { backStackEntry ->
//            DetailsScreen(
//                navController = navController,
//                photoId = backStackEntry.arguments?.getInt("photoId") ?: -1,
//                isFromBookmarks = backStackEntry.arguments?.getBoolean("isFromBookmarks") ?: false
//            )
//        }
//    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
