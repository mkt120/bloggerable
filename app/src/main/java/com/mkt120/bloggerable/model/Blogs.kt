package com.mkt120.bloggerable.model

data class Blogs(
    var kind: String? = null,
    var id: String? = null,
    var name: String? = null,
    var description: String? = null,
    var published: String? = null,
    var updated: String? = null,
    var url: String? = null,
    var selfLink: String? = null,
    var posts: Posts? = null,
    var pages: Pages? = null,
    var locale: Locale? = null
) {

    data class Posts(
        var totalItems: String? = null,
        var selfLink: String? = null
    )

    data class Pages(
        var totalItems: String? = null,
        var selfLink: String? = null
    )

    data class Locale(
        var language: String? = null,
        var country: String? = null,
        var variant: String? = null
    )

}