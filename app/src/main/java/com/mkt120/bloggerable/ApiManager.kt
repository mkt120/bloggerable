package com.mkt120.bloggerable

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {

    private const val TAG: String = "ApiManager"

    private const val BASE_URL: String = "https://www.googleapis.com/"

    private const val GRANT_TYPE: String = "authorization_code"

    private const val ACCESS_TYPE: String = "offline"

    private val oauthService: ApiService

    init {
        val httpClient = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(loggingInterceptor)

        val client = httpClient.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        oauthService = retrofit.create(ApiService::class.java)
    }

    fun requestAccessToken(
        context: Context,
        authorizationCode: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String
    ) {
        oauthService.postAccessToken(
            authorizationCode,
            clientId,
            clientSecret,
            redirectUri,
            GRANT_TYPE,
            ACCESS_TYPE
        ).enqueue(object : Callback<OauthResponse> {
            override fun onResponse(
                call: Call<OauthResponse>?,
                response: Response<OauthResponse>?
            ) {
                Log.d(TAG, "onResponse")
                response?.let {
                    if (response.isSuccessful) {
                        Log.d(TAG, "response=$response")
                        Log.d(TAG, "oauthResponse=${response.body()}")
                        val access = response.body()?.access_token
                        val refresh = response.body()?.refresh_token
                        context.getSharedPreferences(
                            "com.mkt120.bloggerable.pref",
                            Context.MODE_PRIVATE
                        ).edit().putString("KEY_ACCESS_TOKEN", access).apply()
                        context.getSharedPreferences(
                            "com.mkt120.bloggerable.pref",
                            Context.MODE_PRIVATE
                        ).edit().putString("KEY_ACCESS_REFRESH", refresh).apply()
                    }
                }
            }

            override fun onFailure(call: Call<OauthResponse>?, t: Throwable?) {}
        })
    }

    data class OauthResponse(
        val access_token: String?,
        var token_type: String?,
        var expires_in: Int?,
        var refresh_token: String?,
        var scope: String?
    ) {
        constructor() : this(null, null, null, null, null)

        override fun toString(): String {
            return "OauthResponse(access_token=$access_token, token_type=$token_type, expires_in=$expires_in, refresh_token=$refresh_token, scope=$scope)"
        }

    }
}