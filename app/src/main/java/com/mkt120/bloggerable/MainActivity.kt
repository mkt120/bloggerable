package com.mkt120.bloggerable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG: String = MainActivity::class.java.simpleName

        // AuthorizationCode を使ってAccessTokenをもらう
        private const val CLIENT_ID = BuildConfig.BLOGGERABLE_CLIENT_ID

        // AuthorizationCode を使ってAccessTokenをもらう
        private const val CLIENT_SECRET = BuildConfig.BLOGGERABLE_CLIENT_SECRET

        const val STRING_SCOPE_BLOGGER = "https://www.googleapis.com/auth/blogger"

        const val REQUEST_SIGN_IN: Int = 100
        const val REQUEST_REFRESH_TOKEN: Int = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val expire = PreferenceManager.tokenExpiredDateMillis
        if (expire >= System.currentTimeMillis()) {
            // 有効期限内トークン
            goBlogList()
            return
        }

        val refreshToken = PreferenceManager.refreshToken
        if (refreshToken.isNotEmpty()) {
            // リフレッシュトークンがあるのでリフレッシュ
            signInRequest(REQUEST_REFRESH_TOKEN)
            return
        }

        setContentView(R.layout.activity_main)

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener {
            // try to sign in
            signInRequest(REQUEST_SIGN_IN)
        }
    }

    private fun signInRequest(requestCode: Int) {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(CLIENT_ID)
            .requestScopes(Scope(STRING_SCOPE_BLOGGER))
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, option)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode=$requestCode, resultCode=$resultCode")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (requestCode == REQUEST_SIGN_IN) {
            handleSignInResult(task)
        } else if (requestCode == REQUEST_REFRESH_TOKEN) {
            refreshToken(task.getResult(ApiException::class.java)!!)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            if (account != null) {
                Log.d(TAG, "account.id=${account.id}")
                Log.d(TAG, "account.displayName=${account.displayName}")
                Log.d(TAG, "account=${account.grantedScopes}")
                Log.d(TAG, "account.serverAuthCode=${account.serverAuthCode}")
                requestAccessToken(account)
            }
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=${e.statusCode}" , e)
        }
    }

    private fun requestAccessToken(account: GoogleSignInAccount) {
        ApiManager.requestAccessToken(account.serverAuthCode!!, CLIENT_ID, CLIENT_SECRET, "", object : ApiManager.Listener {
            override fun onResponse() {
                goBlogList()
            }
        })
    }

    private fun refreshToken(account: GoogleSignInAccount) {
        ApiManager.refreshToken(account.serverAuthCode!!, CLIENT_ID, CLIENT_SECRET, "", object :ApiManager.Listener{
            override fun onResponse() {
                goBlogList()
            }
        })
    }

    private fun goBlogList() {
        val intent = Intent(this@MainActivity, BlogListActivity::class.java)
        startActivity(intent)
        finish()
    }

}