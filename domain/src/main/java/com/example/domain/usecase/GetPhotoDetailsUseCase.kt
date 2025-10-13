package com.example.domain.usecase

import com.example.domain.TResult
import com.example.domain.model.PhotoDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.IPhotosRepository

class GetPhotoDetailsUseCase(
    private val photosRepository: IPhotosRepository
) {
    suspend operator fun invoke(id: Int): TResult<PhotoDomainModel, PexelsExceptionDomainModel> =
        photosRepository.getPhotoDetails(id)
}