package com.mkt120.bloggerable.login

import android.content.Intent

interface LoginContract {

    interface View {
        fun showLoginButton()
        fun showEmptyBlogScreen()
        fun showBlogListScreen(blogId: String)
        fun showError(message: String)
        fun requestSignIn(intent: Intent, requestCode: Int)
    }

    interface Presenter {
        fun initialize()
        fun onClickSignIn()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

}