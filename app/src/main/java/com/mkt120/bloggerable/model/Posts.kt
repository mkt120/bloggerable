package com.mkt120.bloggerable.model

data class Posts(
    var kind: String? = null,
    var id: String? = null,
    var url: String? = null,
    var selfLink: String? = null,
    var title:String? = null,
    var content:String? = null)