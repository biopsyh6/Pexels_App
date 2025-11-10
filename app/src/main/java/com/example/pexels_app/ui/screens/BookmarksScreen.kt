package com.example.pexels_app.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
import com.example.pexels_app.ui.event.BookmarksEvent
import com.example.pexels_app.ui.intent.BookmarksIntent
import com.example.pexels_app.ui.screens.common.ShimmerEffect
import com.example.pexels_app.ui.state.BookmarksState
import com.example.pexels_app.ui.viewmodel.BookmarksViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    navController: NavController,
    viewModel: BookmarksViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var event by remember { mutableStateOf(viewModel.event) }
    val listState = rememberLazyListState()
    val gridState = rememberLazyStaggeredGridState()

    var showProgress by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        event.filterIsInstance<BookmarksEvent.ShowToast>()
            .onEach { event ->
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
            .launchIn(this)
    }

    LaunchedEffect(Unit) {
        event.filterIsInstance<BookmarksEvent.NavigateToHome>()
            .onEach {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
            .launchIn(this)
    }

    LaunchedEffect(gridState.isScrollInProgress) {
        val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
        val totalItems = listState.layoutInfo.totalItemsCount
        if (lastVisibleItem != null && lastVisibleItem.index >= totalItems - 5) {
            viewModel.onIntent(BookmarksIntent.LoadMore)
        }
    }

    LaunchedEffect(state) {
        showProgress =
            state is BookmarksState.Success && (state as BookmarksState.Success).isLoadingMore
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.bookmarks),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.white)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(id = R.color.white)),
        ) {
            when (state) {
                BookmarksState.Empty -> {
                    EmptyBookmarksState {
                        viewModel.onIntent(BookmarksIntent.NavigateToHome)
                    }
                }

                is BookmarksState.Error -> {
                    ErrorStateBookmarks(message = (state as BookmarksState.Error).exception) {
                        viewModel.onIntent(BookmarksIntent.LoadBookmarks)
                    }
                }

                BookmarksState.Loading -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(6) { BookmarkPhotoItemShimmerGrid() }
                    }
                }

                is BookmarksState.Success -> {
                    val photos = (state as BookmarksState.Success).photos
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        state = gridState,
                        modifier = Modifier.fillMaxSize()
                            .padding(PaddingValues(bottom = 76.dp)),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 8.dp)

                    ) {
                        items(photos) { photo ->
                            BookmarkPhotoItemGrid(
                                photo = photo,
                                onClick = {
                                    navController.navigate("details/${photo.id}?isFromBookmarks=true")
                                    viewModel.onIntent(BookmarksIntent.PhotoClicked(photo.id))
                                }
                            )
                        }
                        if (showProgress) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
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
fun BookmarkPhotoItemGrid(photo: PhotoDomainModel, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .aspectRatio(photo.width.toFloat() / photo.height.toFloat())
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            )
            .then(
                if (isPressed) Modifier.offset(y = 2.dp) else Modifier
            )
            .animateContentSize()
    ) {
        AsyncImage(
            model = photo.src["medium"] ?: photo.url,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(48.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = 48f
                    )
                )
        ) {
            Text(
                text = photo.photographer,
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.white),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 12.dp, bottom = 8.dp)
            )
        }
    }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
    }
}

@Composable
fun BookmarkPhotoItemShimmerGrid() {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
    ) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun EmptyBookmarksState(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.not_saved),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.white)
            )
        ) {
            Text(stringResource(R.string.explore), color = colorResource(R.color.red))
        }
    }
}

@Composable
fun ErrorStateBookmarks(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(stringResource(id = R.string.retry), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}