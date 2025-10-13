package com.example.pexels_app.util

import android.content.Context
import androidx.annotation.StringRes
import com.example.domain.model.exception.PexelsExceptionDomainModel
import com.example.pexels_app.R

fun PexelsExceptionDomainModel.parseToString(context: Context, @StringRes defaultRes: Int = R.string.error_unknown): String {
    return context.getString(
        when (this) {
            is PexelsExceptionDomainModel.NoInternet -> R.string.error_no_internet
            is PexelsExceptionDomainModel.Other -> R.string.error_other
            is PexelsExceptionDomainModel.NoAuth -> R.string.error_no_auth
        }
    )
}