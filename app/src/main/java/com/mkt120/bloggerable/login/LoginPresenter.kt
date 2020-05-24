package com.mkt120.bloggerable.login

import android.content.Intent
import android.util.Log
import com.mkt120.bloggerable.create.CreatePostsContract
import com.mkt120.bloggerable.usecase.AuthorizeGoogleAccount
import com.mkt120.bloggerable.usecase.GetAllBlog
import com.mkt120.bloggerable.usecase.GetCurrentAccount
import com.mkt120.bloggerable.usecase.RequestAccessToken

class LoginPresenter(
    private val view: LoginContract.View,
    private val requestAccessToken: RequestAccessToken,
    private val getCurrentAccount: GetCurrentAccount,
    private val authorizeGoogleAccount: AuthorizeGoogleAccount,
    private val getAllBlogs: GetAllBlog
) : LoginContract.Presenter {

    companion object {
        private val TAG: String = LoginPresenter::class.java.simpleName
        private const val REQUEST_SIGN_IN: Int = 100
    }

    override fun initialize() {
        Log.i(TAG, "initialize")
        val alreadyAuthorized = authorizeGoogleAccount.alreadyAuthorized()
        if (!alreadyAuthorized) {
            view.showLoginButton()
            return
        }
        val currentAccount = getCurrentAccount.execute()!!
        if (currentAccount.isExpiredBlogList(System.currentTimeMillis())) {
            requestAllBlogs()
        } else {
            view.showBlogListScreen()
        }
    }

    override fun onClickSignIn() {
        Log.i(TAG, "signInRequest")
        requestSignIn()
    }

    private fun requestSignIn() {
        val signInIntent = authorizeGoogleAccount.getSignInIntent()
        view.requestSignIn(signInIntent, REQUEST_SIGN_IN)
    }

    /**
     * サインイン処理完了後
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "onActivityResult")
        if (requestCode == REQUEST_SIGN_IN) {
            view.showProgress()
            requestAccessToken.execute(data, {
                Log.d(TAG, "requestAccessToken onComplete")
                requestAllBlogs()
            }, {
                view.dismissProgress()
                view.showError(CreatePostsContract.TYPE.RECEIVE_OBTAIN_ACCESS_TOKEN_ERROR)
            })
        }
    }

    override fun onConfirmPositiveClick(type: CreatePostsContract.TYPE) {
        if (type == CreatePostsContract.TYPE.RECEIVE_OBTAIN_ACCESS_TOKEN_ERROR) {
            requestSignIn()
        } else if (type == CreatePostsContract.TYPE.RECEIVE_OBTAIN_BLOG_ERROR) {
            requestAllBlogs()
        }
    }

    fun requestAllBlogs() {
        Log.i(TAG, "requestAllBlogs")
        view.showProgress()
        val currentAccount = getCurrentAccount.execute()!!
        getAllBlogs.execute(
            System.currentTimeMillis(),
            currentAccount
        ).subscribe({
            view.dismissProgress()
            view.showBlogListScreen()
        }, {
            view.dismissProgress()
            view.showError(CreatePostsContract.TYPE.RECEIVE_OBTAIN_BLOG_ERROR)
        })
    }
}
