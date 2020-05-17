package com.mkt120.bloggerable

import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.OauthResponse
import com.mkt120.bloggerable.model.posts.Posts
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    /**
     * アクセストークン取得する
     */
    fun requestAccessToken(
        authorizationCode: String,
        redirectUri: String
    ): Single<OauthResponse> = apiService.postAccessToken(
        authorizationCode,
        CLIENT_ID,
        CLIENT_SECRET,
        redirectUri,
        GRANT_TYPE_AUTHORIZATION_CODE,
        ACCESS_TYPE
    ).subscribeOn(Schedulers.io())

    /**
     * アクセストークンを再取得する
     */
    fun refreshToken(
        redirectUri: String,
        refreshToken: String
    ): Single<OauthResponse> = apiService.refreshToken(
        CLIENT_ID,
        CLIENT_SECRET,
        redirectUri,
        refreshToken,
        GRANT_TYPE_REFRESH_TOKEN
    )
        .observeOn(AndroidSchedulers.mainThread())


    /**
     * ブログリストを取得する
     */
    fun getBlogs(accessToken: String): Single<BlogsResponse> =
        apiService.listByUser("Bearer $accessToken", "self", BuildConfig.BLOGGERABLE_API_KEY)
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * 下書き一覧を取得する
     */
    fun getDraftPosts(
        accessToken: String,
        blogId: String
    ): Single<Pair<List<Posts>?, Boolean>> = getPosts(accessToken, blogId, "draft")
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 記事一覧を取得する
     */
    fun getPosts(
        accessToken: String,
        blogId: String,
        status: String = "live"
    ): Single<Pair<List<Posts>?, Boolean>> = apiService.getPosts(
        "Bearer $accessToken",
        blogId,
        BuildConfig.BLOGGERABLE_API_KEY,
        status
    ).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { response ->
            Pair(response.items, status != ("live"))
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
        isDraft: Boolean
    ): Completable {
        val posts = Posts.createPosts(title, content, labels)
        return apiService.createPosts(
            "Bearer $accessToken",
            blogId,
            BuildConfig.BLOGGERABLE_API_KEY,
            posts,
            isDraft
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 投稿を更新する
     */
    fun updatePosts(accessToken: String, old: Posts): Completable {
        val posts = Posts.createPosts(
            old.title!!,
            old.content!!,
            old.labels!!.toTypedArray()
        )
        return apiService.updatePosts(
            "Bearer $accessToken",
            old.blog!!.id!!,
            old.id!!,
            BuildConfig.BLOGGERABLE_API_KEY,
            posts
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 下書きを公開(投稿)する
     */
    fun publishPosts(
        accessToken: String,
        blogId: String,
        postsId: String
    ): Completable = apiService.publishPosts(
        "Bearer $accessToken",
        blogId,
        postsId,
        BuildConfig.BLOGGERABLE_API_KEY
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 投稿を下書きに戻す
     */
    fun revertPosts(
        accessToken: String,
        blogId: String, postId: String
    ): Completable = apiService.revertPosts(
        "Bearer $accessToken",
        blogId,
        postId,
        BuildConfig.BLOGGERABLE_API_KEY
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 投稿を削除する
     */
    fun deletePosts(
        accessToken: String,
        blogId: String,
        postId: String
    ): Completable = apiService.deletePosts(
        "Bearer $accessToken",
        blogId,
        postId,
        BuildConfig.BLOGGERABLE_API_KEY
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}