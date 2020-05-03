package com.mkt120.bloggerable.top.drawer

import com.mkt120.bloggerable.model.blogs.Blogs

interface DrawerContract {

    interface View {
        fun setName(name:String)
        fun setImage(url:String)
    }
    interface Presenter {
        fun initialize()
    }

    interface BlogItemView {
        fun setBlogName(name: String)
    }

    interface BlogItemPresenter {
        fun onBindData(blog: Blogs)
    }
}