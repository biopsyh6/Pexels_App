package com.example.domain.usecase

import com.example.domain.TResult
import com.example.domain.model.PhotoDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.IPhotosRepository

class GetBookmarkedPhotosUseCase(
    private val photosRepository: IPhotosRepository
) {
    suspend operator fun invoke(
        offset: Int,
        limit: Int
    ): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel> =
        photosRepository.getBookmarkedPhotos(offset, limit)
}