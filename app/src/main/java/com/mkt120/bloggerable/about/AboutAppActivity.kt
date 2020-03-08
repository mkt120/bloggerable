package com.mkt120.bloggerable.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mkt120.bloggerable.BuildConfig
import com.mkt120.bloggerable.R
import kotlinx.android.synthetic.main.activity_about_app.*

/**
 * このアプリについて画面
 */
class AboutAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        tool_bar.setTitle(R.string.drawer_menu_about_this_app)
        app_version.text = BuildConfig.VERSION_NAME
    }
}