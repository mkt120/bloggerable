package com.mkt120.bloggerable.login

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.mkt120.bloggerable.ApiManager
import com.mkt120.bloggerable.PreferenceManager
import com.mkt120.bloggerable.RealmManager
import com.mkt120.bloggerable.api.BlogsResponse

class LoginPresenter(
    private val view: LoginContract.View,
    private val realmManager: RealmManager,
    private val wrapper: LoginStaticWrapper
) : LoginContract.Presenter {
    companion object {
        private val TAG: String = LoginPresenter::class.java.simpleName
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        if (!wrapper.isExpiredDateMillis()) {
            // 有効期限内トークン
            requestBlogList()
            return
        }
        // 有効期限切れでもリフレッシュトークンがあれば
        val refreshToken = wrapper.refreshToken()
        Log.i(TAG, "refreshToken=$refreshToken")
        if (refreshToken.isNotEmpty()) {
            // リフレッシュトークンがあるのでリフレッシュ
            refreshToken()
            return
        }
        view.showLoginButton()
    }

    override fun onClickSignIn() {
        Log.i(TAG, "onClickSignIn")
        // try to sign in
        signInRequest()
    }

    private fun signInRequest() {
        Log.i(TAG, "signInRequest")
        val signInIntent = wrapper.getClient()
        view.requestSignIn(signInIntent, LoginActivity.REQUEST_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "onActivityResult")
        if (requestCode == LoginActivity.REQUEST_SIGN_IN) {
            val task = wrapper.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun refreshToken() {
        Log.i(TAG, "refreshToken")
        val refreshToken = PreferenceManager.refreshToken
        ApiManager.refreshToken(
            "",
            refreshToken,
            object : ApiManager.Listener {
                override fun onResponse() {
                    requestBlogList()
                }
            })
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.i(TAG, "handleSignInResult")
        try {
            val account: GoogleSignInAccount? =
                completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            if (account != null) {
                Log.d(TAG, "account.id=${account.id}")
                Log.d(TAG, "account.displayName=${account.displayName}")
                Log.d(TAG, "account=${account.grantedScopes}")
                Log.d(TAG, "account.serverAuthCode=${account.serverAuthCode}")
                Log.d(TAG, "account.url=${account.photoUrl}")
                PreferenceManager.photoUrl = account.photoUrl.toString()
                PreferenceManager.displayName = account.displayName.toString()
                requestAccessToken(account)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=${e.statusCode}", e)
        }
    }

    private fun requestAccessToken(account: GoogleSignInAccount) {
        Log.i(TAG, "requestAccessToken")
        ApiManager.requestAccessToken(
            account.serverAuthCode!!,
            "",
            object : ApiManager.Listener {
                override fun onResponse() {
                    requestBlogList()
                }
            })
    }

    private fun requestBlogList() {
        Log.i(TAG, "requestBlogList")
        ApiManager.getBlogs(object :
            ApiManager.BlogListener {
            override fun onResponse(blogsResponse: BlogsResponse?) {
                realmManager.addAllBlogs(blogsResponse!!.items!!)
                view.showBlogListScreen(blogsResponse)
            }
        })
    }
}
