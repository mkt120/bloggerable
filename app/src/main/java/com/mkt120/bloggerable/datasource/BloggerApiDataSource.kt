package com.mkt120.bloggerable.datasource

import com.mkt120.bloggerable.ApiService
import com.mkt120.bloggerable.BuildConfig
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

class BloggerApiDataSource : DataSource.IBloggerApiDataSource {

    companion object {
        private const val TAG: String = "ApiManager"

        private const val BASE_URL: String = "https://www.googleapis.com/"

        private const val GRANT_TYPE_AUTHORIZATION_CODE: String = "authorization_code"
        private const val GRANT_TYPE_REFRESH_TOKEN: String = "refresh_token"

        private const val ACCESS_TYPE: String = "offline"

        // AuthorizationCode を使ってAccessTokenをもらう
        const val CLIENT_ID = BuildConfig.BLOGGERABLE_CLIENT_ID

        // AuthorizationCode を使ってAccessTokenをもらう
        private const val CLIENT_SECRET = BuildConfig.BLOGGERABLE_CLIENT_SECRET
    }

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
    override fun requestAccessToken(
        authorizationCode: String
    ): Single<OauthResponse> = apiService.postAccessToken(
        authorizationCode,
        CLIENT_ID,
        CLIENT_SECRET,
        "",
        GRANT_TYPE_AUTHORIZATION_CODE,
        ACCESS_TYPE
    ).subscribeOn(Schedulers.io())

    /**
     * トークンのリフレッシュ要求
     */
    override fun refreshAccessToken(
        refreshToken: String
    ): Single<OauthResponse> = apiService.refreshToken(
        CLIENT_ID,
        CLIENT_SECRET,
        "",
        refreshToken,
        GRANT_TYPE_REFRESH_TOKEN
    ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    override fun requestPostsList(
        accessToken: String,
        blogId: String
    ): Single<List<Posts>> = getPosts(accessToken, blogId, "live")

    override fun requestDraftPostsList(
        accessToken: String,
        blogId: String
    ): Single<List<Posts>> = getPosts(accessToken, blogId, "draft")

    private fun getPosts(
        accessToken: String,
        blogId: String,
        status: String = "live"
    ): Single<List<Posts>> = apiService.getPosts(
        "Bearer $accessToken",
        blogId,
        BuildConfig.BLOGGERABLE_API_KEY,
        status
    ).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { response -> response.items ?: ArrayList<Posts>()  }

    override fun updatePosts(
        accessToken: String,
        old: Posts
    ): Completable {
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

    override fun deletePosts(
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


    /**
     * 下書きを公開(投稿)する
     */
    override fun publishPosts(
        accessToken: String,
        blogId: String,
        postId: String
    ): Completable = apiService.publishPosts(
        "Bearer $accessToken",
        blogId,
        postId,
        BuildConfig.BLOGGERABLE_API_KEY
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 投稿を下書きに戻す
     */
    override fun revertPosts(
        accessToken: String,
        blogId: String,
        postId: String
    ): Completable = apiService.revertPosts(
        "Bearer $accessToken",
        blogId,
        postId,
        BuildConfig.BLOGGERABLE_API_KEY
    )
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * 投稿する
     */
    override fun createPosts(
        accessToken: String,
        blogId: String,
        title: String,
        html: String,
        labels: Array<String>?,
        isDraft: Boolean
    ): Completable {
        val posts = Posts.createPosts(title, html, labels)
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

    override fun getBlogs(accessToken: String): Single<BlogsResponse> =
        apiService.listByUser("Bearer $accessToken", "self", BuildConfig.BLOGGERABLE_API_KEY)
            .observeOn(AndroidSchedulers.mainThread())
}