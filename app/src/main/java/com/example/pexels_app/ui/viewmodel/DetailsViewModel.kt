package com.example.pexels_app.ui.viewmodel

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.TResult
import com.example.domain.usecase.GetBookmarkedPhotoUseCase
import com.example.domain.usecase.GetPhotoDetailsUseCase
import com.example.domain.usecase.ToggleBookmarkUseCase
import com.example.pexels_app.ui.SingleFlowEvent
import com.example.pexels_app.ui.event.DetailsEvent
import com.example.pexels_app.ui.intent.DetailsIntent
import com.example.pexels_app.ui.state.DetailsState
import com.example.pexels_app.util.parseToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val getPhotoDetailsUseCase: GetPhotoDetailsUseCase,
    private val getBookmarkedPhotoUseCase: GetBookmarkedPhotoUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val downloadManager: DownloadManager,
    private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<DetailsState>(DetailsState.Loading)
    val state: StateFlow<DetailsState> = _state.asStateFlow()

    private val _event = SingleFlowEvent<DetailsEvent>(viewModelScope)
    val event = _event.flow

    private val photoId: Int = savedStateHandle.get<Int>("photoId") ?: -1
    private val isFromBookmarks: Boolean = savedStateHandle.get<Boolean>("isFromBookmarks") ?: false

    init {
        fetchPhotoDetails()
    }

    fun onIntent(intent: DetailsIntent) {
        when (intent) {
            DetailsIntent.Download -> handleDownload()
            DetailsIntent.NavigateBack -> _event.emit(DetailsEvent.NavigateBack)
            DetailsIntent.ToggleBookmark -> handleToggleBookmark()
        }
    }

    private fun fetchPhotoDetails() {
        viewModelScope.launch(ioDispatcher) {
            _state.update { DetailsState.Loading }
            val result = if (isFromBookmarks) {
                getBookmarkedPhotoUseCase(photoId)
            } else {
                getPhotoDetailsUseCase(photoId)
            }

            when (result) {
                is TResult.Error -> {
                    _state.update { DetailsState.Error(result.exception.parseToString()) }
                    _event.emit(DetailsEvent.ShowToast("Failed to load image: ${result.exception.parseToString()}"))
                }

                is TResult.Success -> {
                    var photo = result.data
                    val bookmarkedPhoto = getBookmarkedPhotoUseCase(photoId)
                    if (bookmarkedPhoto is TResult.Success) {
                        photo = photo.copy(isBookmarked = true)
                    }
                    _state.update { DetailsState.Success(photo) }
                }
            }
        }
    }

    private fun handleDownload() {
        viewModelScope.launch(ioDispatcher) {
            val photo = (state.value as? DetailsState.Success)?.photo
            if (photo != null) {
                val imageUrl = photo.src["original"] ?: photo.src["large"] ?: photo.url
                try {
                    val request = DownloadManager.Request(Uri.parse(imageUrl))
                        .setTitle("Photo_${photo.id}.jpg")
                        .setDescription("Downloaded from Pexels")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(false)
                        .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_PICTURES,
                            "Pexels/Photo_${photo.id}.jpg"
                        )
                    val downloadId = downloadManager.enqueue(request)
                    _event.emit(DetailsEvent.ShowToast("Download started with ID: $downloadId"))
                } catch (e: Exception) {
                    _event.emit(DetailsEvent.ShowToast("Download failed: ${e.message}"))
                }
            } else {
                _event.emit(DetailsEvent.ShowToast("No image to download"))
            }
        }
    }

    private fun handleToggleBookmark() {
        viewModelScope.launch(ioDispatcher) {
            val photo = (state.value as? DetailsState.Success)?.photo
            if (photo != null) {
                toggleBookmarkUseCase(photo.id, !photo.isBookmarked)
                val updatedPhoto = photo.copy(isBookmarked = !photo.isBookmarked)
                _state.update { (state.value as DetailsState.Success).copy(photo = updatedPhoto) }
                _event.emit(DetailsEvent.ShowToast(
                    if (updatedPhoto.isBookmarked) "Added to bookmarks" else "Removed from bookmarks"
                ))
            }
        }
    }
}