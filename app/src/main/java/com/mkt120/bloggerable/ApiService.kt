package com.mkt120.bloggerable

import com.mkt120.bloggerable.model.BlogsResponse
import com.mkt120.bloggerable.model.PostsResponse
import retrofit2.Call
import retrofit2.http.*


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

    @FormUrlEncoded
    @POST("oauth2/v4/token")
    fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String
    ): Call<ApiManager.OauthResponse>


    @GET("blogger/v3/users/{userId}/blogs")
    fun listByUser(@Header("Authorization") accessToken: String?, @Path("userId") userId: String, @Query("key") apiKey: String): Call<BlogsResponse>

    @GET("blogger/v3/blogs/{blogId}/posts")
    fun getPosts(@Header("Authorization") accessToken: String?, @Path("blogId") blogId: String, @Query("key") apiKey: String): Call<PostsResponse>

    @POST("blogger/v3/blogs/{blogId}/posts")
    fun createPosts(@Header("Authorization") accessToken: String?, @Path("blogId") blogId: String, @Query("key") apiKey: String,@Body post: HashMap<String, String>): Call<Any>

    @DELETE("blogger/v3/blogs/{blogId}/posts/{postId}")
    fun deletePosts(@Header("Authorization") accessToken: String?, @Path("blogId") blogId: String, @Path("postId") postId: String, @Query("key") apiKey: String): Call<Any>

}