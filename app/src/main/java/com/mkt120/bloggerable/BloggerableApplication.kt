package com.mkt120.bloggerable

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class BloggerableApplication : Application() {

    private lateinit var realm: Realm

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(this)


        //Realmの初期化
        Realm.init(this)
        val config = RealmConfiguration.Builder().build()
        Realm.setDefaultConfiguration(config)
        realm = Realm.getDefaultInstance()
    }

    public fun getRealm() :Realm {
        return realm
    }
}