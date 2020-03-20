package com.mkt120.bloggerable

import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm
import java.lang.IllegalStateException

open class BaseActivity : AppCompatActivity() {
    protected fun getRealm() : Realm {

        if (application is BloggerableApplication) {
            return (application as BloggerableApplication).getRealm()
        }
        throw IllegalStateException("application is invalid!!!")
    }

}