package com.mkt120.bloggerable

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    @FormUrlEncoded
    @POST("oauth2/v4/token")
    fun postAccessToken(
        @Field("code") authorizationCode: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String,
        @Field("access_type") accessType: String
    ): Call<ApiManager.OauthResponse>

}