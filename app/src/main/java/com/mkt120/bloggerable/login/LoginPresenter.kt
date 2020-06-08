package com.mkt120.bloggerable.login

import android.content.Intent
import android.util.Log
import com.mkt120.bloggerable.create.CreatePostsContract
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.usecase.AuthorizeGoogleAccount
import com.mkt120.bloggerable.usecase.GetAllBlog
import com.mkt120.bloggerable.usecase.RequestUserInfo
import com.mkt120.bloggerable.usecase.UseCase

class LoginPresenter(
    private val view: LoginContract.View,
    private val requestUserInfo: RequestUserInfo,
    private val saveCurrentAccount: UseCase.ISaveCurrentAccount,
    private val getCurrentAccount: UseCase.IGetCurrentAccount,
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
            requestAllBlogs(currentAccount)
        } else {
            view.showBlogListScreen()
        }
    }

    override fun onClickSignIn() {
        Log.i(TAG, "signInRequest")
        requestSignIn()
    }

    private fun requestSignIn() {
        val intent = authorizeGoogleAccount.getAuthorizeIntent()
        view.startActivityForResult(intent, REQUEST_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_IN && data != null) {
            requestUserInfo.execute(data,
                { account ->
                    saveCurrentAccount.execute(account)
                    Log.d(TAG, "requestAccessToken onComplete")
                    requestAllBlogs(account)
                }, {
                    view.dismissProgress()
                    view.showError(CreatePostsContract.TYPE.GET_USER_INFO_FAILED)
                })
        }
    }

    override fun onConfirmPositiveClick(type: CreatePostsContract.TYPE) {
        if (type == CreatePostsContract.TYPE.GET_BLOG_INFO_FAILED) {
            val account = getCurrentAccount.execute()
            if (account != null) {
                requestAllBlogs(account)
                // アカウントがないはずないけど
                return
            }
        }
        requestSignIn()
    }

    private fun requestAllBlogs(currentAccount: Account) {
        Log.i(TAG, "requestAllBlogs")
        view.showProgress()
        getAllBlogs.execute(
            currentAccount
        ).subscribe({
            view.dismissProgress()
            view.showBlogListScreen()
        }, {
            view.dismissProgress()
            view.showError(CreatePostsContract.TYPE.GET_BLOG_INFO_FAILED)
        })
    }
}
