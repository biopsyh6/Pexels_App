package com.example.pexels_app.ui.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.domain.model.CollectionDomainModel
import com.example.domain.model.PhotoDomainModel
import com.example.pexels_app.R
import com.example.pexels_app.ui.event.HomeEvent
import com.example.pexels_app.ui.intent.HomeIntent
import com.example.pexels_app.ui.screens.common.ShimmerEffect
import com.example.pexels_app.ui.state.HomeState
import com.example.pexels_app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var event by remember { mutableStateOf(viewModel.event) }
    var searchQuery by remember { mutableStateOf("") }
    var activeCollectionTitle by remember { mutableStateOf<String?>(null) }

    val initialCollections by remember(state) {
        mutableStateOf((state as? HomeState.Success)?.collections ?: emptyList())
    }

    val listState = rememberLazyListState()

    LaunchedEffect(searchQuery) {
        listState.scrollToItem(0)
        activeCollectionTitle = if (searchQuery.isNotEmpty()) {
            (state as? HomeState.Success)?.collections?.find { it.title == searchQuery }?.title
        } else {
            null
        }
    }

    LaunchedEffect(Unit) {
        event.filterIsInstance<HomeEvent.ShowToast>()
            .onEach { event ->
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
            .launchIn(this)
    }

//    LaunchedEffect(Unit) {
//        event.filterIsInstance<HomeEvent.NavigateToDetails>()
//            .onEach {
//                navController.navigate("details")
//            }
//            .launchIn(this)
//    }

    LaunchedEffect(Unit) {
        event.filterIsInstance<HomeEvent.NavigateToHome>()
            .onEach {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
            .launchIn(this)
    }

    LaunchedEffect(Unit) {
        event.filterIsInstance<HomeEvent.ScrollToPhoto>()
            .onEach { }
            .launchIn(this)
    }

    LaunchedEffect(listState.isScrollInProgress) {
        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
        val totalItems = listState.layoutInfo.totalItemsCount
        if (lastVisibleItem != null && lastVisibleItem.index >= totalItems - 5 &&
            state is HomeState.Success && (state as HomeState.Success).photos.size >= 30 * (viewModel.currentPage - 1)
        ) {
            viewModel.onIntent(HomeIntent.LoadMore)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { query ->
                                searchQuery = query
                                viewModel.onIntent(HomeIntent.Search(query))
                            },
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(end = 16.dp),
                            textStyle = TextStyle(fontSize = 16.sp),
                            singleLine = true,
                            placeholder = {
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Search",
                                        color = colorResource(id = R.color.dark_gray)
                                    )
                                }
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = { viewModel.onIntent(HomeIntent.Search(searchQuery)) },
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = colorResource(id = R.color.red)
                                    )
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        viewModel.onIntent(HomeIntent.Search(""))
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Input",
                                            tint = colorResource(id = R.color.dark_gray)
                                        )
                                    }
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedTextColor = colorResource(id = R.color.black),
                                unfocusedTextColor = colorResource(id = R.color.black),
                                disabledTextColor = colorResource(id = R.color.black),
                                cursorColor = colorResource(id = R.color.black),
                                containerColor = colorResource(id = R.color.gray),
                                focusedIndicatorColor = colorResource(id = R.color.white),
                                unfocusedIndicatorColor = colorResource(id = R.color.white)
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    viewModel.onIntent(HomeIntent.Search(searchQuery))
                                }
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.white),
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(colorResource(id = R.color.white))
        ) {
            item {

                if (state is HomeState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                }

            }
            item {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val collections = (state as? HomeState.Success)?.collections ?: emptyList()
                    val sortedCollections = if (activeCollectionTitle != null) {
                        val activeCollection =
                            collections.find { it.title == activeCollectionTitle }
                        val remainingColletions =
                            collections.filter { it.title != activeCollectionTitle }
                        if (activeCollection != null) listOf(activeCollection) + remainingColletions
                        else collections
                    } else {
                        initialCollections
                    }
                    items(
                        sortedCollections
                    ) { collection ->
                        CollectionItem(
                            collection = collection,
                            isActive = activeCollectionTitle == collection.title,
                            onClick = {
                                searchQuery = collection.title
                                viewModel.onIntent(HomeIntent.Search(collection.title))
                            }
                        )
                    }
                }
            }
            item {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (state is HomeState.Loading || (state is HomeState.Success && (state as HomeState.Success).isLoadingMore)) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .heightIn(max = 850.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items((state as? HomeState.Success)?.photos ?: emptyList()) { photo ->
                            PhotoItem(photo = photo,
                                onClick = {
                                    navController.navigate("details/${photo.id}?isFromBookmarks=false")
                                    viewModel.onIntent(HomeIntent.PhotoClicked(photo.id))
                                })
                        }
                    }
                }
            }
            item {
                when (state) {
                    HomeState.Empty -> {
                        EmptyState { viewModel.onIntent(HomeIntent.Explore) }
                    }

                    is HomeState.Error -> {
                        ErrorState((state as HomeState.Error).exception ?: "Unknown error") {
                            viewModel.onIntent(HomeIntent.Explore)
                        }
                    }

                    HomeState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        }
                    }

                    is HomeState.NetworkStub -> {
                        NetworkStubState((state as HomeState.NetworkStub).lastQuery) {
                            viewModel.onIntent(HomeIntent.Explore)
                        }
                    }

                    is HomeState.Success -> {}
                }
            }
        }
    }
}

@Composable
fun CollectionItem(collection: CollectionDomainModel, isActive: Boolean, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
//    val elevation = if (isPressed) 4.dp else 0.dp
    Box(
        modifier = Modifier
            .height(50.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isActive) colorResource(id = R.color.red)
                else colorResource(id = R.color.gray)
            )
            .clickable(
                onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = interactionSource
            )
            .then(
                if (isPressed) Modifier.offset(y = 2.dp) else Modifier
            )
//            .shadow(elevation, shape = RoundedCornerShape(24.dp))
            .animateContentSize(animationSpec = tween(durationMillis = 100))
            .padding(8.dp)
            .run {
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        when (interaction) {
                            is PressInteraction.Press -> isPressed = true
                            is PressInteraction.Release -> isPressed = false
                            is PressInteraction.Cancel -> isPressed = false
                        }
                    }
                }
                this
            }
    ) {
        Text(
            text = collection.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.6f
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PhotoItem(photo: PhotoDomainModel, onClick: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick,
                indication = LocalIndication.current,
                interactionSource = interactionSource)
            .background(
                if (isPressed) colorResource(R.color.white).copy(alpha = 0.9f)
                else colorResource(R.color.white).copy(alpha = 0.8f)
            )
            .then(
                if (isPressed) Modifier
                    .offset(y = 2.dp)
//                    .shadow(4.dp, RoundedCornerShape(12.dp))
                else Modifier
//                    .shadow(2.dp, RoundedCornerShape(12.dp))
            )
            .animateContentSize(animationSpec = tween(durationMillis = 100))
            .run {
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        when(interaction) {
                            is PressInteraction.Press -> isPressed = true
                            is PressInteraction.Release -> isPressed = false
                            is PressInteraction.Cancel -> isPressed = false
                        }
                    }
                }
                this
            }
    ) {
        AsyncImage(
            model = photo.src["medium"] ?: photo.url,
            contentDescription = photo.alt,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            onSuccess = { isLoading = false },
            onError = { isLoading = false },
//            placeholder = painterResource(id = R.drawable.vector)
            placeholder = null
        )
        if (isLoading) {
            ShimmerEffect(modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
fun EmptyState(onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_data),
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
fun ErrorState(message: String, onExploreClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onExploreClick,
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(stringResource(R.string.explore), color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun NetworkStubState(lastQuery: String?, onTryAgainClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.network_error),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        if (lastQuery != null) {
            Text(
                text = stringResource(R.string.last_query, lastQuery),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onTryAgainClick,
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.white)
            )
        ) {
            Text(stringResource(R.string.try_again), color = colorResource(R.color.red))
        }
    }
}