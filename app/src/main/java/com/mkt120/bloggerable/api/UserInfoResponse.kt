package com.mkt120.bloggerable.api

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("sub") val id: String,
    val name: String,
    val picture: String
)