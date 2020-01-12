package com.mkt120.bloggerable.api

data class OauthResponse(
    var access_token: String? = null,
    var token_type: String? = null,
    var expires_in: Int? = null,
    var refresh_token: String? = null,
    var scope: String? = null
) {

    override fun toString(): String {
        return "OauthResponse(access_token=$access_token, token_type=$token_type, expires_in=$expires_in, refresh_token=$refresh_token, scope=$scope)"
    }
}
