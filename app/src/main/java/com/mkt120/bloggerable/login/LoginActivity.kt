package com.mkt120.bloggerable.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.common.SignInButton
import com.mkt120.bloggerable.BaseActivity
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.util.RealmManager
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.top.TopActivity
import kotlinx.android.synthetic.main.activity_login.*

/**
 * ログイン画面
 */
class LoginActivity : BaseActivity(), LoginContract.View {

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName

        const val REQUEST_SIGN_IN: Int = 100
    }

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // ログインボタン
        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener {
            presenter.onClickSignIn()
        }

        presenter = LoginPresenter(
            this@LoginActivity,
            RealmManager(getRealm()),
            LoginStaticWrapper(this@LoginActivity)
        )
        presenter.onCreate()
    }

    override fun showLoginButton() {
        sign_in_button.visibility = View.VISIBLE
    }

    override fun requestSignIn(intent: Intent, requestCode: Int) {
        startActivityForResult(
            intent,
            requestCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode=$requestCode, resultCode=$resultCode")
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun showBlogListScreen(blogsResponse: BlogsResponse?) {
        val intent = TopActivity.createIntent(
            this@LoginActivity,
            blogsResponse!!
        )
        startActivity(intent)
        finish()
    }

}