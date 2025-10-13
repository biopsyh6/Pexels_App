package com.example.pexels_app.util

import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.pexels_app.R

//fun PexelsExceptionDomainModel.parseToString(): Int {
//    return when (this) {
//        is PexelsExceptionDomainModel.NoInternet -> R.string.error_no_internet
//        is PexelsExceptionDomainModel.Other -> R.string.error_other
//        is PexelsExceptionDomainModel.NoAuth -> R.string.error_no_auth
//    }
//}

fun PexelsExceptionDomainModel.parseToString(): String {
    return when (this) {
        is PexelsExceptionDomainModel.NoInternet -> "No internet connection"
        is PexelsExceptionDomainModel.Other -> "An unknown error occurred"
        is PexelsExceptionDomainModel.NoAuth -> "Authentication failed"
    }
}