package com.example.pexels_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pexels_app.ui.theme.Pexels_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Pexels_AppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


//@Composable
//fun Main() {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "task_list") {
//        composable("task_list") { TaskListScreen(navController) }
//        composable("login") { LoginScreen(navController) }
//        composable("success") { SuccessScreen() }
//        composable("posts") { PostsScreen(navController) }
//        composable("postsComments") { PostsCommentsScreen(navController) }
//        composable(
//            route = "comments/{postId}?post={post}",
//            arguments = listOf(
//                navArgument("postId") { type = NavType.IntType },
//                navArgument("post") { type = NavType.StringType; nullable = true }
//            )
//        ) { backStackEntry ->
//            CommentsScreen(
//                navController = navController,
//                postId = backStackEntry.arguments?.getInt("postId") ?: -1
//            )
//        }
//    }
//}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Pexels_AppTheme {
        Greeting("Android")
    }
}