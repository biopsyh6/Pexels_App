package com.example.pexels_app.ui.screens

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.domain.model.PhotoDomainModel
import com.example.pexels_app.R
import com.example.pexels_app.ui.event.DetailsEvent
import com.example.pexels_app.ui.intent.DetailsIntent
import com.example.pexels_app.ui.state.DetailsState
import com.example.pexels_app.ui.viewmodel.DetailsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: DetailsViewModel = koinViewModel(),
    photoId: Int,
    isFromBookmarks: Boolean
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                DetailsEvent.NavigateBack -> navController.popBackStack()
                is DetailsEvent.ShowToast -> Toast.makeText(
                    context,
                    event.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val photo = (state as? DetailsState.Success)?.photo
                    Text(
                        text = photo?.photographer ?: "Unknown Author",
//                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onIntent(DetailsIntent.NavigateBack) }) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = colorResource(id = R.color.gray),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = colorResource(id = R.color.black)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.white)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(id = R.color.white))
        ) {
            when (state) {
                is DetailsState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.image_not_found),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                navController.navigate("home") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.white)
                            ),
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = colorResource(id = R.color.red),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp)
                        ) {
                            Text(
                                stringResource(R.string.explore),
                                color = colorResource(id = R.color.red)
                            )
                        }
                    }
                }

                DetailsState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                        )
                    }
                }

                is DetailsState.Success -> {
                    val photo = (state as DetailsState.Success).photo
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (isLoading) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.TopCenter),
                                )
                            }
                        }
                        ZoomableImage(
                            photoUrl = photo.src["original"] ?: photo.url,
                            photo = photo,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            onImageLoaded = { isLoading = false }
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorResource(R.color.white))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { viewModel.onIntent(DetailsIntent.Download) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.gray)
                                ),
                                contentPadding = PaddingValues(
                                    start = 0.dp,
                                    top = 0.dp,
                                    end = 12.dp,
                                    bottom = 0.dp
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(ButtonDefaults.MinHeight)
                                        .background(
                                            colorResource(id = R.color.red),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.download),
                                        contentDescription = "Download",
                                        tint = colorResource(id = R.color.white),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    stringResource(R.string.download),
                                    color = colorResource(R.color.black),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Button(
                                onClick = { viewModel.onIntent(DetailsIntent.ToggleBookmark) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(
                                        id = R.color.gray
                                    )
                                )
                            ) {
                                Icon(
                                    painter = if (photo.isBookmarked) painterResource(id = R.drawable.bookmark_red)
                                    else
                                        painterResource(id = R.drawable.bookmark),
                                    contentDescription = "Bookmark",
                                    tint = if (photo.isBookmarked) colorResource(id = R.color.red)
                                    else
                                        colorResource(id = R.color.black),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ZoomableImage(
    photoUrl: String,
    photo: PhotoDomainModel,
    modifier: Modifier = Modifier,
    onImageLoaded: () -> Unit = {}
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .transformable(state = transformableState)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val touchCount = event.changes.count { it.pressed }

                        if (touchCount == 0) {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .aspectRatio(photo.width.toFloat() / photo.height.toFloat()),
            onSuccess = { onImageLoaded() },
            onError = { onImageLoaded() }
        )
    }
}