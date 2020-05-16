package com.mkt120.bloggerable.login

import android.content.Intent
import android.util.Log
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.usecase.AuthorizeGoogleAccount
import com.mkt120.bloggerable.usecase.RequestAccessToken
import com.mkt120.bloggerable.usecase.RequestAllBlogs
import com.mkt120.bloggerable.usecase.SaveAllBlogs

class LoginPresenter(
    private val view: LoginContract.View,
    private val requestAccessToken: RequestAccessToken,
    private val saveAllBlogs: SaveAllBlogs,
    private val authorizeGoogleAccount: AuthorizeGoogleAccount,
    private val requestAllBlogs: RequestAllBlogs
) : LoginContract.Presenter {

    companion object {
        private val TAG: String = LoginPresenter::class.java.simpleName
        private const val REQUEST_SIGN_IN: Int = 100
    }

    override fun initialize() {
        Log.i(TAG, "initialize")
        val accessToken = authorizeGoogleAccount.getAccessToken()
        if (accessToken.isEmpty()) {
            view.showLoginButton()
            return
        }
        requestAllBlogs()
    }

    override fun onClickSignIn() {
        Log.i(TAG, "signInRequest")
        val signInIntent = authorizeGoogleAccount.getSignInIntent()
        view.requestSignIn(signInIntent, REQUEST_SIGN_IN)
    }

    /**
     * サインイン処理完了後
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "onActivityResult")
        if (requestCode == REQUEST_SIGN_IN) {
            authorizeGoogleAccount.saveAccountInfo(data)
            requestAccessToken.execute(data, object : RequestAccessToken.OnCompleteListener {
                override fun onComplete() {
                    requestAllBlogs()
                }

                override fun onErrorResponse(code: Int, message: String) {
                    view.showError()
                }

                override fun onFailed(t: Throwable?) {
                    view.showError()
                }
            })
        }
    }
    fun requestAllBlogs() {
        requestAllBlogs.execute(object :ApiManager.BlogListener {
            override fun onResponse(blogList: List<Blogs>?) {
                //todo: empty
                saveAllBlogs.execute(blogList)
                view.showBlogListScreen(blogList!![0].id!!)
            }
        })
    }
}
