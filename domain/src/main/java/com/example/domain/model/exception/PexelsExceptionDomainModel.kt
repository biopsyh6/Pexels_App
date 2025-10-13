package com.example.domain.model.exception

sealed class PexelsExceptionDomainModel(exception: Throwable) : Throwable(exception) {
    override val cause: Throwable = exception

    data class NoInternet(val exception: Throwable) : PexelsExceptionDomainModel(exception)
    data class NoAuth(val exception: Throwable) : PexelsExceptionDomainModel(exception)
    data class Other(val exception: Throwable) : PexelsExceptionDomainModel(exception)
}
