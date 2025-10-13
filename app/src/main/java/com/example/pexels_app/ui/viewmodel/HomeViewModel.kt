package com.example.pexels_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.TResult
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.usecase.GetFeaturedCollectionsUseCase
import com.example.domain.usecase.GetPhotoDetailsUseCase
import com.example.domain.usecase.GetPhotosUseCase
import com.example.pexels_app.ui.SingleFlowEvent
import com.example.pexels_app.ui.event.HomeEvent
import com.example.pexels_app.ui.intent.HomeIntent
import com.example.pexels_app.ui.state.HomeState
import com.example.pexels_app.util.parseToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val getPhotoDetailsUseCase: GetPhotoDetailsUseCase,
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _event = SingleFlowEvent<HomeEvent>(viewModelScope)
    val event = _event.flow

    private var currentPage = 1
    private var currentQuery: String? = null

    init {
        fetchInitialData()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Search -> handleSearch(intent.query)
            is HomeIntent.Explore -> handleExplore()
            is HomeIntent.LoadMore -> loadMorePhotos()
            is HomeIntent.PhotoClicked -> handlePhotoClicked(intent.id)
        }
    }

    private fun fetchInitialData() {
        viewModelScope.launch(ioDispatcher) {
            _state.update { HomeState.Loading }
            val collectionsResult = getFeaturedCollectionsUseCase()
            val photosResult = getPhotosUseCase(page = currentPage, query = null)

            when {
                collectionsResult is TResult.Error || photosResult is TResult.Error -> {
                    val exception = (collectionsResult as? TResult.Error)?.exception
                        ?: (photosResult as? TResult.Error)?.exception
                    _state.update { HomeState.Error(exception?.parseToString()) }
                    _event.emit(HomeEvent.ShowToast(exception?.parseToString() ?: "Unknown error"))
                }
                collectionsResult is TResult.Success && photosResult is TResult.Success -> {
                    val collections = collectionsResult.data
                    val photos = photosResult.data
                    if (collections.isEmpty() && photos.isEmpty()) {
                        _state.update { HomeState.Empty }
                    } else {
                        _state.update { HomeState.Success(collections, photos) }
                    }
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        currentQuery = query
        currentPage = 1
        viewModelScope.launch(ioDispatcher) {
            _state.update { HomeState.Loading }
            val result = getPhotosUseCase(page = currentPage, query = query)
            when (result) {
                is TResult.Success -> {
                    _state.update { HomeState.Success(collections = emptyList(), photos = result.data) }
                }
                is TResult.Error -> {
                    _state.update { HomeState.NetworkStub(query) }
                    _event.emit(HomeEvent.ShowToast(result.exception.parseToString()))
                }
            }
        }
    }

    private fun handleExplore() {
        currentQuery = null
        currentPage = 1
        fetchInitialData()
    }

    private fun loadMorePhotos() {
        if (state.value !is HomeState.Success) return
        val currentState = state.value as HomeState.Success
        if (currentState.isLoadingMore) return

        viewModelScope.launch(ioDispatcher) {
            _state.update { currentState.copy(isLoadingMore = true) }
            currentPage++
            val result = getPhotosUseCase(page = currentPage, query = currentQuery)
            when (result) {
                is TResult.Success -> {
                    val newPhotos = result.data
                    _state.update { currentState.copy(collections = currentState.collections, photos = currentState.photos + newPhotos, isLoadingMore = false) }
                }
                is TResult.Error -> {
                    _state.update { currentState.copy(isLoadingMore = false) }
                    _event.emit(HomeEvent.ShowToast(result.exception.parseToString()))
                }
            }
        }
    }

    private fun handlePhotoClicked(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            val result = getPhotoDetailsUseCase(id)
            if (result is TResult.Success) {
                _event.emit(HomeEvent.NavigateToDetails)
            } else {
                _event.emit(HomeEvent.ShowToast(result.exception!!.parseToString()))
            }
        }
    }
}