package com.mkt120.bloggerable

import com.mkt120.bloggerable.api.BlogsResponse
import com.mkt120.bloggerable.api.PostResponse
import com.mkt120.bloggerable.api.UserInfoResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*


interface ApiService {

    @GET("oauth2/v3/userinfo")
    fun userInfo(@Header("Authorization") accessToken: String?): Single<UserInfoResponse>

    @GET("blogger/v3/users/{userId}/blogs")
    fun listByUser(
        @Header("Authorization") accessToken: String?,
        @Path("userId") userId: String,
        @Query("key") apiKey: String
    ): Single<BlogsResponse>

    @GET("blogger/v3/blogs/{blogId}/posts")
    fun getPosts(
        @Header("Authorization") accessToken: String?,
        @Path("blogId") blogId: String,
        @Query("key") apiKey: String,
        @Query("status") status: String? = null
    ): Single<PostResponse>

    @POST("blogger/v3/blogs/{blogId}/posts")
    fun createPosts(
        @Header("Authorization") accessToken: String?,
        @Path("blogId") blogId: String,
        @Query("key") apiKey: String,
        @Body post: HashMap<String, Any>,
        @Query("isDraft") isDraft: Boolean = false
    ): Completable

    @PUT("blogger/v3/blogs/{blogId}/posts/{postId}")
    fun updatePosts(
        @Header("Authorization") accessToken: String?,
        @Path("blogId") blogId: String,
        @Path("postId") postId: String,
        @Query("key") apiKey: String,
        @Body post: HashMap<String, Any>
    ): Completable

    @POST("blogger/v3/blogs/{blogId}/posts/{postId}/publish")
    fun publishPosts(
        @Header("Authorization") accessToken: String?,
        @Path("blogId") blogId: String,
        @Path("postId") postId: String,
        @Query("key") apiKey: String
    ): Completable

    @POST("blogger/v3/blogs/{blogId}/posts/{postId}/revert")
    fun revertPosts(
        @Header("Authorization") accessToken: String?,
        @Path("blogId") blogId: String,
        @Path("postId") postId: String,
        @Query("key") apiKey: String
    ): Completable

    @DELETE("blogger/v3/blogs/{blogId}/posts/{postId}")
    fun deletePosts(
        @Header("Authorization") accessToken: String?,
        @Path("blogId") blogId: String,
        @Path("postId") postId: String,
        @Query("key") apiKey: String
    ): Completable

}