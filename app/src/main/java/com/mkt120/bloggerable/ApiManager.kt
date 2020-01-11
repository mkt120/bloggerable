package com.mkt120.bloggerable

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

    private const val GRANT_TYPE_AUTHORIZATION_CODE: String = "authorization_code"
    private const val GRANT_TYPE_REFRESH_TOKEN: String = "refresh_token"

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
            GRANT_TYPE_AUTHORIZATION_CODE,
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
                        response.body()?.apply {
                            access_token?.let {
                                PreferenceManager.accessToken = it
                            }
                            refresh_token?.let {
                                PreferenceManager.refreshToken = it
                            }
                            expires_in?.let {
                                PreferenceManager.tokenExpiredDateMillis =
                                    System.currentTimeMillis() + it * 1000L
                            }
                        }
                    }
                    listener.onResponse()
                }
            }

            override fun onFailure(call: Call<OauthResponse>?, t: Throwable?) {}
        })
    }

    fun refreshToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        refreshToken:String,
        listener: Listener) {

        apiService.refreshToken(clientId, clientSecret, redirectUri, refreshToken, GRANT_TYPE_REFRESH_TOKEN)
            .enqueue(object : Callback<OauthResponse> {
                override fun onResponse(
                call: Call<OauthResponse>?,
                response: Response<OauthResponse>?) {
                Log.d(TAG, "onResponse")
                response?.let {
                    if (response.isSuccessful) {
                        Log.d(TAG, "blogsResponse=$response")
                        Log.d(TAG, "oauthResponse=${response.body()}")
                        response.body()?.apply {
                            access_token?.let {
                                PreferenceManager.accessToken = it
                            }
                            refresh_token?.let {
                                PreferenceManager.refreshToken = it
                            }
                            expires_in?.let {
                                PreferenceManager.tokenExpiredDateMillis =
                                    System.currentTimeMillis() + it * 1000L
                            }
                        }
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
    fun getBlogs(listener: BlogListener) {
        val accessToken = PreferenceManager.accessToken
        apiService.listByUser("Bearer $accessToken", "self", BuildConfig.BLOGGERABLE_API_KEY)
            .enqueue(object : Callback<BlogsResponse> {
                override fun onResponse(
                    call: Call<BlogsResponse>,
                    response: Response<BlogsResponse>
                ) {
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
    fun getPosts(blogId: String, listener: PostsListener) {
        val accessToken = PreferenceManager.accessToken
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

    fun createPosts(blogId: String, title: String, content: String, listener: CompleteListener) {
        val accessToken = PreferenceManager.accessToken
        val posts = Posts.createPosts(title, content)
        apiService.createPosts(
            "Bearer $accessToken",
            blogId,
            BuildConfig.BLOGGERABLE_API_KEY,
            posts
        )
            .enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    listener.onComplete()
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d(TAG, "onFailure", t)
                    listener.onFailed(t)
                }
            })
    }

    fun deletePosts(blogId: String, postId: String, listener: CompleteListener) {
        val accessToken = PreferenceManager.accessToken
        apiService.deletePosts(
            "Bearer $accessToken",
            blogId,
            postId,
            BuildConfig.BLOGGERABLE_API_KEY
        )
            .enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
                ) {
                    listener.onComplete()
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d(TAG, "onFailure", t)
                    listener.onFailed(t)
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

    public interface CompleteListener {
        fun onComplete()
        fun onFailed(t: Throwable)
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