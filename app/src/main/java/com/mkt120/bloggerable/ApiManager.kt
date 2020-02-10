package com.mkt120.bloggerable

import android.util.Log
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.api.PostsResponse
import com.mkt120.bloggerable.model.Posts
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

    // AuthorizationCode を使ってAccessTokenをもらう
    const val CLIENT_ID = BuildConfig.BLOGGERABLE_CLIENT_ID

    // AuthorizationCode を使ってAccessTokenをもらう
    private const val CLIENT_SECRET = BuildConfig.BLOGGERABLE_CLIENT_SECRET


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
        redirectUri: String,
        listener: Listener
    ) {
        apiService.postAccessToken(
            authorizationCode,
            CLIENT_ID,
            CLIENT_SECRET,
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

    /**
     * アクセストークンを再取得する
     */
    fun refreshToken(
        redirectUri: String,
        refreshToken: String,
        listener: Listener
    ) {

        apiService.refreshToken(
            CLIENT_ID,
            CLIENT_SECRET,
            redirectUri,
            refreshToken,
            GRANT_TYPE_REFRESH_TOKEN
        )
            .enqueue(object : Callback<OauthResponse> {
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

    /**
     * ブログリストを取得する
     */
    fun getBlogs(listener: BlogListener) {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    getBlogs(listener)
                }
            })
            return
        }

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
     * 下書き一覧を取得する
     */
    fun getDraftPosts(blogId: String, listener: PostsListener) {
        getPosts(blogId, listener, "draft")
    }

    /**
     * 記事一覧を取得する
     */
    fun getPosts(blogId: String, listener: PostsListener, status: String = "live") {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    getPosts(blogId, listener, status)
                }
            })
            return
        }

        val accessToken = PreferenceManager.accessToken
        apiService.getPosts("Bearer $accessToken", blogId, BuildConfig.BLOGGERABLE_API_KEY, status)
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

    /**
     * 投稿する
     */
    fun createPosts(
        blogId: String,
        title: String,
        content: String,
        labels: Array<String>? = null,
        isDraft: Boolean,
        listener: CompleteListener
    ) {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    createPosts(blogId, title, content, labels, isDraft, listener)
                }
            })
            return
        }

        val accessToken = PreferenceManager.accessToken
        val posts = Posts.createPosts(title, content, labels)
        apiService.createPosts(
            "Bearer $accessToken",
            blogId,
            BuildConfig.BLOGGERABLE_API_KEY,
            posts,
            isDraft
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

    /**
     * 投稿を更新する
     */
    fun updatePosts(old: Posts, listener: CompleteListener) {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    updatePosts(old, listener)
                }
            })
            return
        }

        val accessToken = PreferenceManager.accessToken
        val posts = Posts.createPosts(
            old.title!!,
            old.content!!,
            old.labels
        )
        apiService.updatePosts(
            "Bearer $accessToken",
            old.blog!!.id!!,
            old.id!!,
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

    /**
     * 下書きを公開(投稿)する
     */
    fun publishPosts(blogId: String, postsId: String, listener: CompleteListener) {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    publishPosts(blogId, postsId, listener)
                }
            })
            return
        }

        val accessToken = PreferenceManager.accessToken
        apiService.publishPosts(
            "Bearer $accessToken",
            blogId,
            postsId,
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

    /**
     * 投稿を下書きに戻す
     */
    fun revertPosts(blogId: String, postId: String, listener: CompleteListener) {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    revertPosts(blogId, postId, listener)
                }
            })
            return
        }

        val accessToken = PreferenceManager.accessToken
        apiService.revertPosts(
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

    /**
     * 投稿を削除する
     */
    fun deletePosts(blogId: String, postId: String, listener: CompleteListener) {
        if (PreferenceManager.isExpiredDateMillis()) {
            val refreshToken = PreferenceManager.refreshToken
            refreshToken("", refreshToken, object : Listener {
                override fun onResponse() {
                    deletePosts(blogId, postId, listener)
                }
            })
            return
        }

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
}