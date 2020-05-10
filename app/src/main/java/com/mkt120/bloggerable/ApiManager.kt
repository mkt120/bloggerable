package com.mkt120.bloggerable

import android.util.Log
import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.api.PostResponse
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Posts
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
        listener: OauthListener
    ) {
        apiService.postAccessToken(
            authorizationCode,
            CLIENT_ID,
            CLIENT_SECRET,
            redirectUri,
            GRANT_TYPE_AUTHORIZATION_CODE,
            ACCESS_TYPE
        ).enqueue(object : Callback<OauthResponse> {
            override fun onResponse(call: Call<OauthResponse>, response: Response<OauthResponse>) {
                Log.d(TAG, "onResponse")
                if (response.isSuccessful) {
                    listener.onResponse(response.body()!!)
                } else {
                    listener.onErrorResponse(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<OauthResponse>?, t: Throwable) {
                listener.onFailed(t)
            }
        })
    }

    /**
     * アクセストークンを再取得する
     */
    fun refreshToken(
        redirectUri: String,
        refreshToken: String,
        listener: OauthListener
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
                    call: Call<OauthResponse>,
                    response: Response<OauthResponse>
                ) {
                    Log.d(TAG, "onResponse")
                    if (response.isSuccessful) {
                        listener.onResponse(response.body()!!)
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
                }

                override fun onFailure(call: Call<OauthResponse>, t: Throwable) {
                    listener.onFailed(t)
                }
            })
    }


    /**
     * ブログリストを取得する
     */
    fun getBlogs(accessToken: String, listener: BlogListener) {
        apiService.listByUser("Bearer $accessToken", "self", BuildConfig.BLOGGERABLE_API_KEY)
            .enqueue(object : Callback<BlogsResponse> {
                override fun onResponse(
                    call: Call<BlogsResponse>,
                    response: Response<BlogsResponse>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        listener.onResponse(list!!.items)
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
                }

                override fun onFailure(call: Call<BlogsResponse>, t: Throwable) {
                    Log.d(TAG, "onFailure", t)
                    listener.onFailed(t)
                }
            })
    }

    /**
     * 下書き一覧を取得する
     */
    fun getDraftPosts(accessToken: String, blogId: String, listener: PostsListener) {
        getPosts(accessToken, blogId, listener, "draft")
    }

    /**
     * 記事一覧を取得する
     */
    fun getPosts(
        accessToken: String,
        blogId: String,
        listener: PostsListener,
        status: String = "live"
    ) {
        apiService.getPosts("Bearer $accessToken", blogId, BuildConfig.BLOGGERABLE_API_KEY, status)
            .enqueue(object : Callback<PostResponse> {
                override fun onResponse(
                    call: Call<PostResponse>,
                    response: Response<PostResponse>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        listener.onResponse(list?.items)
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
                }

                override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                    listener.onFailed(t)
                }

            })
    }

    /**
     * 投稿する
     */
    fun createPosts(
        accessToken: String,
        blogId: String,
        title: String,
        content: String,
        labels: Array<String>? = null,
        isDraft: Boolean,
        listener: CompleteListener
    ) {
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
                    if (response.isSuccessful) {
                        listener.onComplete()
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
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
    fun updatePosts(accessToken: String, old: Posts, listener: CompleteListener) {
        val posts = Posts.createPosts(
            old.title!!,
            old.content!!,
            old.labels!!.toTypedArray()
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
                    if (response.isSuccessful) {
                        listener.onComplete()
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
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
    fun publishPosts(
        accessToken: String,
        blogId: String,
        postsId: String,
        listener: CompleteListener
    ) {
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
                    if (response.isSuccessful) {
                        listener.onComplete()
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
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
    fun revertPosts(
        accessToken: String,
        blogId: String, postId: String, listener: CompleteListener
    ) {
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
                    if (response.isSuccessful) {
                        listener.onComplete()
                    } else {
                        listener.onErrorResponse(response.code(), response.message())
                    }
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
    fun deletePosts(
        accessToken: String,
        blogId: String,
        postId: String,
        listener: CompleteListener
    ) {
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

    interface OauthListener {
        fun onResponse(response: OauthResponse)
        fun onErrorResponse(code: Int, message: String)
        fun onFailed(t: Throwable)
    }

    interface BlogListener {
        fun onResponse(blogList: List<Blogs>?)
        fun onErrorResponse(code: Int, message: String)
        fun onFailed(t: Throwable)
    }

    interface PostsListener {
        fun onResponse(posts: List<Posts>?)
        fun onErrorResponse(code: Int, message: String)
        fun onFailed(t: Throwable)
    }

    interface CompleteListener {
        fun onComplete()
        fun onErrorResponse(code: Int, message: String)
        fun onFailed(t: Throwable)
    }
}