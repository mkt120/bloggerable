package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.util.PreferenceManager

class PreferenceDataSource {

    fun saveLastSelectBlogId(blogId: String) {
        PreferenceManager.lastSelectBlogId = blogId
    }
    fun getLastSelectBlogId() :String = PreferenceManager.lastSelectBlogId

    fun getExpiredDateMillis(): Long {
        return  PreferenceManager.tokenExpiredDateMillis
    }
    fun saveExpiredDateMillis(millis: Long) {
        PreferenceManager.tokenExpiredDateMillis = millis
    }

    fun getRefreshToken(): String = PreferenceManager.refreshToken
    fun saveRefreshToken (refreshToken: String) {
        PreferenceManager.refreshToken = refreshToken
    }

    fun getAccessToken() :String = PreferenceManager.accessToken
    fun saveAccessToken(accessToken: String) {
        PreferenceManager.accessToken = accessToken
    }

    fun getPhotoUrl() :String = PreferenceManager.photoUrl

    fun savePhotoUrl(photoUrl: String) {
        PreferenceManager.photoUrl = photoUrl
    }

    fun getDisplayName() :String = PreferenceManager.displayName
    fun saveDisplayName(displayName: String) {
        PreferenceManager.displayName = displayName
    }

}