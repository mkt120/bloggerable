package com.mkt120.bloggerable.login

import android.content.Intent
import com.mkt120.bloggerable.create.CreatePostsContract

interface LoginContract {

    interface View {
        fun showLoginButton()
        fun showBlogListScreen()
        fun showError(type: CreatePostsContract.TYPE)
        fun requestSignIn(intent: Intent, requestCode: Int)
        fun showProgress()
        fun dismissProgress()
    }

    interface Presenter {
        fun initialize()
        fun onClickSignIn()
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun onConfirmPositiveClick(type: CreatePostsContract.TYPE)
    }

}