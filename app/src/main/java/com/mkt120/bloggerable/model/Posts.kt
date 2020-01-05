package com.mkt120.bloggerable.model

data class Posts(
    var kind: String? = null,
    var id: String? = null,
    var url: String? = null,
    var selfLink: String? = null,
    var title:String? = null,
    var content:String? = null) {

    companion object {
        public fun createPosts(title:String, content:String) : HashMap<String, String> =
            HashMap<String, String>().apply {
                this["title"] = title
                this["content"] = content
            }
    }
}