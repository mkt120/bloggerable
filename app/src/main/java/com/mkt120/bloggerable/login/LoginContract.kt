package com.mkt120.bloggerable.login

import android.content.Intent
import com.mkt120.bloggerable.api.BlogsResponse

interface LoginContract {

    interface View {
        fun showLoginButton()
        fun showBlogListScreen(blogsResponse: BlogsResponse?)
        fun requestSignIn(intent: Intent, requestCode: Int)
    }
    interface Presenter {
        fun onCreate()
        fun onClickSignIn()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

}