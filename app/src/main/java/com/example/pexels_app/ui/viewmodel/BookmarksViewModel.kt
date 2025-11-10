package com.example.pexels_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.TResult
import com.example.domain.usecase.GetBookmarkedPhotosUseCase
import com.example.domain.usecase.GetPhotoDetailsUseCase
import com.example.pexels_app.di.AppEventBus
import com.example.pexels_app.ui.SingleFlowEvent
import com.example.pexels_app.ui.event.BookmarksEvent
import com.example.pexels_app.ui.intent.BookmarksIntent
import com.example.pexels_app.ui.state.BookmarksState
import com.example.pexels_app.util.parseToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val getBookmarkedPhotosUseCase: GetBookmarkedPhotosUseCase,
    private val getPhotoDetailsUseCase: GetPhotoDetailsUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _state = MutableStateFlow<BookmarksState>(BookmarksState.Loading)
    val state: StateFlow<BookmarksState> = _state.asStateFlow()

    private val _event = SingleFlowEvent<BookmarksEvent>(viewModelScope)
    val event = _event.flow

    private var currentOffset = 0
    private val pageSize = 30

    init {
        loadBookmarks()
        viewModelScope.launch {
            AppEventBus.events
                .filterIsInstance<BookmarksEvent.BookmarkedChanged>()
                .collect {
                    loadBookmarks()
                }
        }
    }

    fun onIntent(intent: BookmarksIntent) {
        when (intent) {
            BookmarksIntent.LoadBookmarks -> loadBookmarks()
            BookmarksIntent.LoadMore -> loadMore()
            is BookmarksIntent.PhotoClicked -> handlePhotoClicked(intent.id)
            BookmarksIntent.NavigateToHome -> _event.emit(BookmarksEvent.NavigateToHome)
        }
    }

    private fun loadBookmarks() {
        viewModelScope.launch(ioDispatcher) {
            _state.update { BookmarksState.Loading }
            val result = getBookmarkedPhotosUseCase(offset = 0, limit = pageSize)
            when (result) {
                is TResult.Error -> {
                    _state.update { BookmarksState.Error(result.exception.parseToString()) }
                    _event.emit(BookmarksEvent.ShowToast(result.exception.parseToString()))
                }

                is TResult.Success -> {
                    currentOffset = pageSize
                    if (result.data.isEmpty()) {
                        _state.update { BookmarksState.Empty }
                    } else {
                        _state.update {
                            BookmarksState.Success(
                                photos = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadMore() {
        val current = state.value as? BookmarksState.Success ?: return
        if (current.isLoadingMore) return

        viewModelScope.launch(ioDispatcher) {
            _state.update { current.copy(isLoadingMore = true) }
            val result = getBookmarkedPhotosUseCase(offset = currentOffset, limit = pageSize)
            when (result) {
                is TResult.Error -> {
                    _state.update { current.copy(isLoadingMore = false) }
                    _event.emit(BookmarksEvent.ShowToast(result.exception.parseToString()))
                }

                is TResult.Success -> {
                    currentOffset += pageSize
                    _state.update {
                        current.copy(
                            photos = current.photos + result.data,
                            isLoadingMore = result.data.size == pageSize
                        )
                    }
                }
            }
        }
    }

    private fun handlePhotoClicked(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            val result = getPhotoDetailsUseCase(id)
            if (result is TResult.Success) {
                _event.emit(BookmarksEvent.NavigateToDetails)
            } else {
                _event.emit(BookmarksEvent.ShowToast(result.exception!!.parseToString()))
            }
        }
    }
}