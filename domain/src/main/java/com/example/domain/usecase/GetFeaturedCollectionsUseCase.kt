package com.example.domain.usecase

import com.example.domain.TResult
import com.example.domain.model.CollectionDomainModel
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.domain.repository.ICollectionsRepository

class GetFeaturedCollectionsUseCase(
    private val collectionsRepository: ICollectionsRepository
) {
    suspend operator fun invoke(): TResult<List<CollectionDomainModel>, PexelsExceptionDomainModel> =
        collectionsRepository.getFeaturedCollections()
}