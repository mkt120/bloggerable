package com.mkt120.bloggerable.top.drawer

import com.mkt120.bloggerable.model.Blogs

interface DrawerContract {

    interface BlogsItemView {
        fun setBlogName(name: String)
    }

    interface BlogsItemPresenter {
        fun onBindData(blogs: Blogs)
    }
}