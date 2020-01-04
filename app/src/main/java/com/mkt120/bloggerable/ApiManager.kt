package com.mkt120.bloggerable

import android.content.Context
import android.util.Log
import com.mkt120.bloggerable.model.BlogsResponse
import com.mkt120.bloggerable.model.Posts
import com.mkt120.bloggerable.model.PostsResponse
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

    private val apiService: ApiService

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
        apiService = retrofit.create(ApiService::class.java)
    }

    /**
     * アクセストークン取得する
     */
    fun requestAccessToken(
        context: Context,
        authorizationCode: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        listener: Listener
    ) {
        apiService.postAccessToken(
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
                        Log.d(TAG, "blogsResponse=$response")
                        Log.d(TAG, "oauthResponse=${response.body()}")
                        val access = response.body()?.access_token
                        val refresh = response.body()?.refresh_token
                        val expiresMillis = response.body()!!.getExpiredDateMillis()

                        context.getSharedPreferences(
                            "com.mkt120.bloggerable.pref",
                            Context.MODE_PRIVATE
                        ).edit().putString("KEY_ACCESS_TOKEN", access).apply()
                        context.getSharedPreferences(
                            "com.mkt120.bloggerable.pref",
                            Context.MODE_PRIVATE
                        ).edit().putString("KEY_ACCESS_REFRESH", refresh).apply()
                        context.getSharedPreferences(
                            "com.mkt120.bloggerable.pref",
                            Context.MODE_PRIVATE
                        ).edit().putLong("KEY_ACCESS_EXPIRES_MILLIS", expiresMillis).apply()
                    }
                    listener.onResponse()
                }
            }

            override fun onFailure(call: Call<OauthResponse>?, t: Throwable?) {}
        })
    }

    /**
     * ブログリストを取得する
     */
    fun getBlogs(context: Context, listener: BlogListener) {
        val accessToken = context.getSharedPreferences(
            "com.mkt120.bloggerable.pref",
            Context.MODE_PRIVATE).getString("KEY_ACCESS_TOKEN", null)
        apiService.listByUser("Bearer $accessToken", "self", BuildConfig.BLOGGERABLE_API_KEY)
            .enqueue(object : Callback<BlogsResponse> {
                override fun onResponse(call: Call<BlogsResponse>, response: Response<BlogsResponse>) {
                    val list = response.body()
                    listener.onResponse(list)
                }
                override fun onFailure(call: Call<BlogsResponse>, t: Throwable) {
                    Log.d(TAG, "onFailure", t)
                }
            })
    }

    /**
     * 記事一覧を取得する
     */
    fun getPosts(context: Context, blogId: String, listener: PostsListener) {
        val accessToken = context.getSharedPreferences(
            "com.mkt120.bloggerable.pref",
            Context.MODE_PRIVATE).getString("KEY_ACCESS_TOKEN", null)
        apiService.getPosts("Bearer $accessToken", blogId, BuildConfig.BLOGGERABLE_API_KEY)
            .enqueue(object : Callback<PostsResponse> {
                override fun onResponse(
                    call: Call<PostsResponse>,
                    response: Response<PostsResponse>
                ) {
                    val list = response.body()
                    listener.onResponse(list)
                }
                override fun onFailure(call: Call<PostsResponse>, t: Throwable) {
                }

            })
    }

    public interface Listener {
        fun onResponse()
    }

    public interface BlogListener {
        fun onResponse(blogList: BlogsResponse?)
    }
    public interface PostsListener {
        fun onResponse(post: PostsResponse?)
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

        fun getExpiredDateMillis() :Long {
            val expiresMillis = expires_in!! * 1000L
            return System.currentTimeMillis() + expiresMillis
        }
    }
}