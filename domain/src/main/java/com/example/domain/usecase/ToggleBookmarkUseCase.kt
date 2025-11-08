package com.example.domain.usecase

import com.example.domain.TResult
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.IPhotosRepository

class ToggleBookmarkUseCase(
    private val photosRepository: IPhotosRepository
) {
    suspend operator fun invoke(
        id: Int,
        isBookmarked: Boolean
    ): TResult<Unit, PexelsExceptionDomainModel> =
        photosRepository.toggleBookmark(id, isBookmarked)
}