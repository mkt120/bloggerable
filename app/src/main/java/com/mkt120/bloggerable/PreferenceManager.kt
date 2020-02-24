package com.mkt120.bloggerable

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferenceManager {

    private lateinit var prefs: SharedPreferences

    private const val KEY_ACCESS_EXPIRES_MILLIS = "KEY_ACCESS_EXPIRES_MILLIS"
    private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    private const val KEY_REFRESH_TOKEN = "KEY_REFRESH_TOKEN"
    private const val KEY_LABEL_SET = "KEY_LABEL_SET"
    private const val KEY_GOOGLE_DISPLAY_NAME = "KEY_GOOGLE_DISPLAY_NAME"
    private const val KEY_GOOGLE_PHOTO_URL = "KEY_GOOGLE_PHOTO_URL"


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

    fun isExpiredDateMillis() :Boolean {
        return tokenExpiredDateMillis <= System.currentTimeMillis()
    }

    var refreshToken: String
        set(refreshToken) = prefs.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
        get() = prefs.getString(KEY_REFRESH_TOKEN, "")!!

    var displayName: String
        set(name) = prefs.edit().putString(KEY_GOOGLE_DISPLAY_NAME, name).apply()
        get() = prefs.getString(KEY_GOOGLE_DISPLAY_NAME, "")!!

    var photoUrl: String
        set(url) = prefs.edit().putString(KEY_GOOGLE_PHOTO_URL, url).apply()
        get() = prefs.getString(KEY_GOOGLE_PHOTO_URL, "")!!

    var labelList: MutableList<String>
        set(list) {
            val text = Gson().toJson(list)
            prefs.edit().putString(KEY_LABEL_SET, text).apply()
        }
        get() {
            val text = prefs.getString(KEY_LABEL_SET, null)
            return Gson().fromJson(text, object : TypeToken<MutableList<String>>() {
            }.type)
        }
}