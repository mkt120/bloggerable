package com.mkt120.bloggerable.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.SignInButton
import com.mkt120.bloggerable.BaseActivity
import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.create.ConfirmDialog
import com.mkt120.bloggerable.create.CreatePostsContract
import com.mkt120.bloggerable.datasource.BloggerApiDataSource
import com.mkt120.bloggerable.datasource.GoogleOauthApiDataSource
import com.mkt120.bloggerable.datasource.PreferenceDataSource
import com.mkt120.bloggerable.datasource.RealmDataSource
import com.mkt120.bloggerable.repository.AccountRepository
import com.mkt120.bloggerable.repository.BlogRepository
import com.mkt120.bloggerable.repository.GoogleAccountRepository
import com.mkt120.bloggerable.repository.TimeRepository
import com.mkt120.bloggerable.top.TopActivity
import com.mkt120.bloggerable.usecase.*
import kotlinx.android.synthetic.main.activity_login.*

/**
 * ログイン画面
 */
class LoginActivity : BaseActivity(), LoginContract.View, ConfirmDialog.OnClickListener {

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
    }

    private lateinit var presenter: LoginContract.Presenter
    private var dialogFragment: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // ログインボタン
        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener {
            presenter.onClickSignIn()
        }

        val bloggerApiDataSource = BloggerApiDataSource()
        val preferenceDataSource = PreferenceDataSource(applicationContext)
        val accountRepository =
            AccountRepository(bloggerApiDataSource, preferenceDataSource)
        val timeRepository = TimeRepository()
        val requestAccessToken = RequestAccessToken(timeRepository, accountRepository)
        val googleOauthApiDataSource = GoogleOauthApiDataSource(applicationContext)
        val googleAccountRepository =
            GoogleAccountRepository(preferenceDataSource, googleOauthApiDataSource)
        val authorizeGoogleAccount = AuthorizeGoogleAccount(googleAccountRepository)
        val realmDataSource = RealmDataSource(getRealm())
        val blogsRepository = BlogRepository(bloggerApiDataSource, realmDataSource)
        val getAccessToken = GetAccessToken(accountRepository, timeRepository)
        val getCurrentAccount = GetCurrentAccount(accountRepository)
        val getAllBlogs =
            GetAllBlog(getAccessToken, accountRepository, blogsRepository, timeRepository)
        presenter = LoginPresenter(
            this@LoginActivity,
            requestAccessToken,
            getCurrentAccount,
            authorizeGoogleAccount,
            getAllBlogs
        )
        presenter.initialize()
    }

    override fun showLoginButton() {
        sign_in_button.visibility = View.VISIBLE
    }

    override fun requestSignIn(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun showProgress() {
        progress_view.visibility = View.VISIBLE
    }

    override fun dismissProgress() {
        progress_view.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode=$requestCode, resultCode=$resultCode")
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    override fun showBlogListScreen() {
        Handler().postDelayed({
            val intent = TopActivity.createIntent(this@LoginActivity)
            startActivity(intent)
            finish()
        }, 500)
    }

    override fun showError(type: CreatePostsContract.TYPE) {
        if (dialogFragment != null && dialogFragment!!.dialog != null && dialogFragment!!.dialog!!.isShowing) {
            return
        }
        dialogFragment = ConfirmDialog.newInstance(type)
        dialogFragment!!.show(supportFragmentManager, null)
    }

    override fun onConfirmPositiveClick(type: CreatePostsContract.TYPE) {
        presenter.onConfirmPositiveClick(type)
    }

    override fun onConfirmNegativeClick(type: CreatePostsContract.TYPE) {
        finish()
    }

    override fun onConfirmNeutralClick(type: CreatePostsContract.TYPE) {
        // 呼ばれない
    }
}