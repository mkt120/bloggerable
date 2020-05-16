package com.mkt120.bloggerable.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.SignInButton
import com.mkt120.bloggerable.BaseActivity
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.GoogleOauthApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.repository.AccessTokenRepository
import com.mkt120.bloggerable.repository.BlogsRepository
import com.mkt120.bloggerable.repository.GoogleAccountRepository
import com.mkt120.bloggerable.top.TopActivity
import com.mkt120.bloggerable.usecase.*
import com.mkt120.bloggerable.util.RealmManager
import kotlinx.android.synthetic.main.activity_login.*

/**
 * ログイン画面
 */
class LoginActivity : BaseActivity(), LoginContract.View {

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
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

        val bloggerApiDataSource = BloggerApiDataSource()
        val preferenceDataSource = PreferenceDataSource()
        val accessTokenRepository =
            AccessTokenRepository(bloggerApiDataSource, preferenceDataSource)
        val requestAccessToken = RequestAccessToken(accessTokenRepository)
        val googleOauthApiDataSource = GoogleOauthApiDataSource(applicationContext)
        val googleAccountRepository =
            GoogleAccountRepository(preferenceDataSource, googleOauthApiDataSource)
        val authorizeGoogleAccount = AuthorizeGoogleAccount(googleAccountRepository)
        val realmDataSource = RealmDataSource(RealmManager(getRealm()))
        val blogsRepository = BlogsRepository(bloggerApiDataSource, realmDataSource)
        val getAccessToken = GetAccessToken(accessTokenRepository)
        val saveAllBlogs = SaveAllBlogs(blogsRepository)
        val requestAllBlogs = RequestAllBlogs(getAccessToken, blogsRepository)
        presenter = LoginPresenter(
            this@LoginActivity,
            requestAccessToken,
            saveAllBlogs,
            authorizeGoogleAccount,
            requestAllBlogs
        )
        presenter.initialize()
    }

    override fun showLoginButton() {
        sign_in_button.visibility = View.VISIBLE
    }

    override fun requestSignIn(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode=$requestCode, resultCode=$resultCode")
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun showBlogListScreen(blogId: String) {
        val intent = TopActivity.createIntent(this@LoginActivity, blogId)
        startActivity(intent)
        finish()
    }

    override fun showError() {
        Toast.makeText(this@LoginActivity, "情報取得に失敗しました", Toast.LENGTH_SHORT).show()
    }

}