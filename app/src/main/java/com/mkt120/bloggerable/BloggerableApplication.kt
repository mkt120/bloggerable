package com.mkt120.bloggerable

import android.app.Application

class BloggerableApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(this)
    }
}