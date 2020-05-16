package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.GoogleAccountRepository

class GetGoogleAccount(private val googleAccountRepository: GoogleAccountRepository) {
    fun getDisplayName(): String = googleAccountRepository.getDisplayName()
    fun getPhotoUrl(): String = googleAccountRepository.getPhotoUrl()


}