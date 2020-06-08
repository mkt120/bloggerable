package com.mkt120.bloggerable.model

data class Account(
    private val id: String,
    private val name: String,
    private val url: String,
    private var accessToken: String,
    private var tokenExpiredDateMillis: Long,
    private var refreshToken: String,
    private var lastBlogListRequest: Long = 0,
    private var currentBlogId: String = ""
) {

    fun getName(): String {
        return name
    }

    fun getPhotoUrl(): String {
        return url
    }

    fun getId(): String {
        return id
    }

    fun getRefreshToken(): String {
        return refreshToken
    }

    fun setCurrentBlogId(blogId: String) {
        currentBlogId = blogId
    }

    fun getCurrentBlogId(): String = currentBlogId

    fun updateAccessToken(accessToken: String, refreshToken: String, expired: Long) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.tokenExpiredDateMillis = expired
    }

    fun getAccessToken(now: Long): String {
        return if (now > tokenExpiredDateMillis) {
            ""
        } else {
            accessToken
        }
    }

    fun isExpiredBlogList(now: Long): Boolean = now - lastBlogListRequest > 24 * 60 * 60 * 1000L

    fun updateLastBlogListRequest(now: Long) {
        lastBlogListRequest = now
    }
}