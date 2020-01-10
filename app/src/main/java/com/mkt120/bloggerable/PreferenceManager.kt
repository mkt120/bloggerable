package com.mkt120.bloggerable

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private lateinit var prefs: SharedPreferences

    private const val KEY_ACCESS_EXPIRES_MILLIS = "KEY_ACCESS_EXPIRES_MILLIS"
    private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"

    fun init(context: Context) {
        prefs =
            context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
    }

    var accessToken: String
        set(accessToken) = prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
        get() = prefs.getString(KEY_ACCESS_TOKEN, "")!!

    var tokenExpiredDateMillis: Long
        set(expiredMillis) = prefs.edit().putLong(KEY_ACCESS_EXPIRES_MILLIS, expiredMillis).apply()
        get() = prefs.getLong(KEY_ACCESS_EXPIRES_MILLIS, 0L)

    var refreshToken: String
        set(refreshToken) = prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        get() = prefs.getString(KEY_REFRESH_TOKEN, "")!!
}