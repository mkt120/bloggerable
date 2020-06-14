package com.mkt120.bloggerable.about

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.mkt120.bloggerable.BuildConfig
import com.mkt120.bloggerable.R
import kotlinx.android.synthetic.main.activity_about_app.*

/**
 * このアプリについて画面
 */
class AboutAppActivity : AppCompatActivity() , View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        tool_bar.setTitle(R.string.drawer_menu_about_this_app)
        app_version.text = BuildConfig.VERSION_NAME

        license_view.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == license_view) {
            val intent = Intent(this, OssLicensesMenuActivity::class.java)
            intent.putExtra("title", "Open Source License")
            startActivity(intent)
        }
    }
}