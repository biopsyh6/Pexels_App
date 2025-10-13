package com.example.data.mapper.exception

import com.example.domain.model.exception.PexelsExceptionDomainModel
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

fun Throwable.toPexelsExceptionDomainModel(): PexelsExceptionDomainModel {
    return when (this) {
        is UnknownHostException, is ConnectException -> PexelsExceptionDomainModel.NoInternet(this)
        is HttpException -> {
            when(this.code()) {
                401 -> PexelsExceptionDomainModel.NoAuth(this)
                else -> PexelsExceptionDomainModel.Other(this)
            }
        }
        is PexelsExceptionDomainModel -> this
        else -> PexelsExceptionDomainModel.Other(this)
    }
}