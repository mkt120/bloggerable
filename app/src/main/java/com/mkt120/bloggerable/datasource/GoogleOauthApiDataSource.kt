package com.mkt120.bloggerable.datasource

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mkt120.bloggerable.BuildConfig
import net.openid.appauth.*

class GoogleOauthApiDataSource(context: Context) : DataSource.IGoogleOauthApiDataSource {
    companion object {
        private const val SCOPE_PROFILE = "profile"
        private const val SCOPE_BLOGGER = "https://www.googleapis.com/auth/blogger"
        private val URI_AUTH_END_POINT =
            Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */
        private val URI_TOKEN_END_POINT =
            Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        private val REDIRECT_URI = Uri.parse("com.mkt120.bloggerable:/oauth2callback")
    }

    private val authorizationService = AuthorizationService(context)

    override fun getAuthorizeIntent(): Intent {
        val serviceConfiguration =
            AuthorizationServiceConfiguration(URI_AUTH_END_POINT, URI_TOKEN_END_POINT)
        val clientId = BuildConfig.BLOGGERABLE_CLIENT_ID
        val builder = AuthorizationRequest.Builder(
            serviceConfiguration,
            clientId,
            ResponseTypeValues.CODE,
            REDIRECT_URI
        )
        val scopes = HashSet<String>().apply {
            add(SCOPE_BLOGGER)
            add(SCOPE_PROFILE)
        }
        builder.setScopes(scopes)

        val request = builder.build()
        return authorizationService.getAuthorizationRequestIntent(request)
    }

    override fun requestAccessToken(
        response: AuthorizationResponse,
        onResponse: (accessToken: String, refreshToken: String, expired: Long) -> Unit,
        onFailed: (exception: Throwable) -> Unit
    ) {
        authorizationService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, exception ->
            if (exception != null) {
                onFailed(exception)
            } else if (tokenResponse != null) {
                val authState = AuthState(response, null)
                authState.update(tokenResponse, null)
                onResponse(
                    tokenResponse.accessToken!!,
                    tokenResponse.refreshToken!!,
                    tokenResponse.accessTokenExpirationTime!!
                )
            }
        }
    }

    override fun refreshAccessToken(
        refreshToken: String,
        onResponse: (TokenResponse) -> Unit,
        onFailed: (Throwable) -> Unit
    ) {
        val clientId = BuildConfig.BLOGGERABLE_CLIENT_ID
        val serviceConfiguration =
            AuthorizationServiceConfiguration(URI_AUTH_END_POINT, URI_TOKEN_END_POINT)
        val request =
            TokenRequest.Builder(serviceConfiguration, clientId)
                .setGrantType(GrantTypeValues.REFRESH_TOKEN)
                .setRefreshToken(refreshToken)
                .setRedirectUri(REDIRECT_URI).build()
        authorizationService.performTokenRequest(request) { tokenResponse, exception ->
            if (exception != null) {
                onFailed(exception)
            } else if (tokenResponse != null) {
                onResponse(tokenResponse)
            }
        }
    }
}