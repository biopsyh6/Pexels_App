package com.example.domain.usecase

import com.example.domain.TResult
import com.example.domain.model.PhotoDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.IPhotosRepository

class GetPhotosUseCase(
    private val photosRepository: IPhotosRepository
) {
    suspend operator fun invoke(
        page: Int,
        query: String?
    ): TResult<List<PhotoDomainModel>, PexelsExceptionDomainModel> =
        photosRepository.getPhotos(page, query)
}