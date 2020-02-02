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

        const val STRING_SCOPE_BLOGGER = "https://www.googleapis.com/auth/blogger"

        const val REQUEST_SIGN_IN: Int = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!PreferenceManager.isExpiredDateMillis()) {
            // 有効期限内トークン
            goBlogList()
            return
        }

        val refreshToken = PreferenceManager.refreshToken
        Log.i(TAG, "refreshToken=$refreshToken")
        if (refreshToken.isNotEmpty()) {
            // リフレッシュトークンがあるのでリフレッシュ
            refreshToken()
            return
        }

        setContentView(R.layout.activity_main)

        sign_in_button.setSize(SignInButton.SIZE_STANDARD)
        sign_in_button.setOnClickListener {
            // try to sign in
            signInRequest()
        }
    }

    private fun signInRequest() {
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(ApiManager.CLIENT_ID, true)
            .requestScopes(Scope(STRING_SCOPE_BLOGGER))
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, option)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode=$requestCode, resultCode=$resultCode")
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (requestCode == REQUEST_SIGN_IN) {
            handleSignInResult(task)
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
            Log.w(TAG, "signInResult:failed code=${e.statusCode}", e)
        }
    }

    private fun requestAccessToken(account: GoogleSignInAccount) {
        ApiManager.requestAccessToken(account.serverAuthCode!!, "", object : ApiManager.Listener {
            override fun onResponse() {
                goBlogList()
            }
        })
    }

    private fun refreshToken() {
        Log.i(TAG, "refreshToken")
        val refreshToken = PreferenceManager.refreshToken
        ApiManager.refreshToken("", refreshToken, object : ApiManager.Listener {
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