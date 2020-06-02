package com.mkt120.bloggerable.presenter

import com.mkt120.bloggerable.R
import com.mkt120.bloggerable.model.Account
import com.mkt120.bloggerable.model.blogs.Blogs
import com.mkt120.bloggerable.model.posts.Blog
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.top.TopContract
import com.mkt120.bloggerable.top.TopPresenter
import com.mkt120.bloggerable.usecase.UseCase
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class TopPresenterTest {
    companion object {
        private val STUB_NOW = System.currentTimeMillis()
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"

        private const val STUB_BLOG_NAME = "stubBlogName"
        private const val STUB_BLOG_URL = "stubBlogUrl"

        private const val STUB_USER_NAME = "stubUserName"
        private const val STUB_USER_URL = "stubUserUrl"
        private const val STUB_TOKEN_EXPIRED = 1000L
        private const val STUB_TOKEN_REFRESH_1 = "stubTokenRefresh1"
        private const val STUB_LAST_REQUEST_BLOG_LIST = 1000L
        private const val STUB_CURRENT_BLOG_ID = "stubCurrentBlogId"
        private const val STUB_CURRENT_BLOG_ID_2 = "stubCurrentBlogId2"

        private const val URL_BLOGGER = "https://www.blogger.com/"
        private const val STUB_BLOG_ID = "stubBlogId";
    }

    var topView = mock<TopContract.TopView> {}
    var getCurrentAccount = mock<UseCase.IGetCurrentAccount> {}
    var saveCurrentAccount = mock<UseCase.ISaveCurrentAccount> {}
    var findAllBlog = mock<UseCase.IFindAllBlog> {}
    var getAllPosts = mock<UseCase.IGetAllPosts> {}
    var getLabels = mock<UseCase.IGetLabels> {}

    @Before
    fun setUp() {
        topView = mock<TopContract.TopView> {}
        getCurrentAccount = mock<UseCase.IGetCurrentAccount> {}
        saveCurrentAccount = mock<UseCase.ISaveCurrentAccount> {}
        findAllBlog = mock<UseCase.IFindAllBlog> {}
        getAllPosts = mock<UseCase.IGetAllPosts> {}
        getLabels = mock<UseCase.IGetLabels> {}
    }

    @Test
    fun initialize() {
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
        verify(topView).showLoginScreen()
    }

    @Test
    fun initialize2() {
        val account = Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH_1,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_CURRENT_BLOG_ID
        )
        getCurrentAccount = mock<UseCase.IGetCurrentAccount> {
            on {
                execute()
            } doReturn (account)
        }
        findAllBlog = mock<UseCase.IFindAllBlog> {
            on {
                execute(STUB_USER_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(mutableListOf<Blogs>())
            })
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
        verify(topView).showEmptyBlogScreen()
    }

    @Test
    fun initialize3() {
        val account = Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH_1,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_CURRENT_BLOG_ID
        )
        val blogs = Blogs().apply {
            id = STUB_CURRENT_BLOG_ID
            name = STUB_BLOG_NAME
        }

        val ret = mutableListOf<Blogs>()
        ret.add(blogs)
        findAllBlog = mock<UseCase.IFindAllBlog> {
            on {
                execute(STUB_USER_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        getCurrentAccount = mock<UseCase.IGetCurrentAccount> {
            on {
                execute()
            } doReturn (account)
        }
        getAllPosts = mock<UseCase.IGetAllPosts> {
            on {
                execute(account, blogs)
            } doReturn (Observable.create { emitter ->
                val lives = mutableListOf<Posts>()
                val drafts = mutableListOf<Posts>()
                emitter.onNext(Pair(lives, drafts))
            })
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
        verify(topView, times(0)).showLoginScreen()
        verify(topView, times(1)).onBindDrawer(ret)
        verify(topView, times(0)).showEmptyBlogScreen()
        verify(topView, times(1)).setItemMenu(R.menu.posts_list_menu)
        verify(topView, times(1)).initDrawerLayout()
        verify(topView, times(1)).showProgress()
    }

    @Test
    fun onClickFab() {
        val account = Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH_1,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_CURRENT_BLOG_ID
        )
        val blogs = Blogs().apply {
            id = STUB_CURRENT_BLOG_ID
            name = STUB_BLOG_NAME
        }

        val ret = mutableListOf<Blogs>()
        ret.add(blogs)
        findAllBlog = mock<UseCase.IFindAllBlog> {
            on {
                execute(STUB_USER_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        getCurrentAccount = mock<UseCase.IGetCurrentAccount> {
            on {
                execute()
            } doReturn (account)
        }
        getAllPosts = mock<UseCase.IGetAllPosts> {
            on {
                execute(account, blogs)
            } doReturn (Observable.create { emitter ->
                val lives = mutableListOf<Posts>()
                val drafts = mutableListOf<Posts>()
                emitter.onNext(Pair(lives, drafts))
            })
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
        presenter.onClickFab()
        verify(topView).showCreateScreen(STUB_CURRENT_BLOG_ID, mutableListOf())
    }

    @Test
    fun onClickBlog() {
        topView = mock<TopContract.TopView> {
            on {
                isDrawerOpen()
            } doReturn (true)
        }
        val account = Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH_1,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_CURRENT_BLOG_ID
        )
        val blogs = Blogs().apply {
            id = STUB_CURRENT_BLOG_ID
            name = STUB_BLOG_NAME
        }

        val ret = mutableListOf<Blogs>()
        ret.add(blogs)
        findAllBlog = mock<UseCase.IFindAllBlog> {
            on {
                execute(STUB_USER_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        getCurrentAccount = mock<UseCase.IGetCurrentAccount> {
            on {
                execute()
            } doReturn (account)
        }
        val newBlog = Blogs().apply {
            id = STUB_CURRENT_BLOG_ID_2
            name = STUB_BLOG_NAME
        }
        getAllPosts = mock<UseCase.IGetAllPosts> {
            on {
                execute(account, newBlog)
            } doReturn (Observable.create { emitter ->
                val lives = mutableListOf<Posts>()
                val drafts = mutableListOf<Posts>()
                emitter.onNext(Pair(lives, drafts))
            })
            on {
                execute(account, blogs)
            } doReturn (Observable.create { emitter ->
                val lives = mutableListOf<Posts>()
                val drafts = mutableListOf<Posts>()
                emitter.onNext(Pair(lives, drafts))
            })
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
        presenter.onClickBlog(newBlog)
        verify(topView).closeDrawer()
        verify(saveCurrentAccount, times(2)).execute(account)
        verify(topView, times(2)).showProgress()
    }

    @Test
    fun onClickDrawerItem() {
    }

    @Test
    fun onBackPressed() {
        topView = mock<TopContract.TopView> {
            on {
                isDrawerOpen()
            } doReturn (true)
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        val ret = presenter.onBackPressed()
        Assert.assertEquals(true, ret)
        verify(topView).closeDrawer()
    }

    @Test
    fun onBackPressed2() {
        topView = mock<TopContract.TopView> {
            on {
                isDrawerOpen()
            } doReturn (false)
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        val ret = presenter.onBackPressed()
        Assert.assertEquals(false, ret)
    }

    @Test
    fun onActivityResult() {
        // todo:
    }

    @Test
    fun onClickCreateBlogButton() {
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.onClickCreateBlogButton()
        verify(topView).openBrowser(URL_BLOGGER)
    }

    @Test
    fun onClickRefreshButton() {
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.onClickRefreshButton()
        verify(topView).showLoginScreen()
    }

    @Test
    fun onMenuItemClick() {
        val account = Account(
            STUB_USER_ID,
            STUB_USER_NAME,
            STUB_USER_URL,
            STUB_ACCESS_TOKEN,
            STUB_TOKEN_EXPIRED,
            STUB_TOKEN_REFRESH_1,
            STUB_LAST_REQUEST_BLOG_LIST,
            STUB_CURRENT_BLOG_ID
        )
        val blogs = Blogs().apply {
            id = STUB_CURRENT_BLOG_ID
            name = STUB_BLOG_NAME
            url = STUB_BLOG_URL
        }

        val ret = mutableListOf<Blogs>()
        ret.add(blogs)
        findAllBlog = mock<UseCase.IFindAllBlog> {
            on {
                execute(STUB_USER_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        getCurrentAccount = mock<UseCase.IGetCurrentAccount> {
            on {
                execute()
            } doReturn (account)
        }
        getAllPosts = mock<UseCase.IGetAllPosts> {
            on {
                execute(account, blogs)
            } doReturn (Observable.create { emitter ->
                val lives = mutableListOf<Posts>()
                val drafts = mutableListOf<Posts>()
                emitter.onNext(Pair(lives, drafts))
            })
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.initialize()
        presenter.onMenuItemClick(R.id.open_in_browser)
        verify(topView).openBrowser(STUB_BLOG_URL)

        presenter.onMenuItemClick(R.id.about_this_blog)
        verify(topView).showAboutDialog(blogs)
    }

    @Test
    fun onClickPosts() {
        val posts = Posts()
        posts.blog = Blog()
        posts.blog!!.id = STUB_BLOG_ID
        getLabels = mock<UseCase.IGetLabels> {
            on {
                execute(STUB_BLOG_ID)
            } doReturn (mutableListOf<String>())
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.onClickPosts(posts, TopContract.TYPE.DRAFT)
        verify(topView).showEditScreen(posts, mutableListOf(), true)
    }

    @Test
    fun onClickPosts2() {
        val posts = Posts()
        posts.blog = Blog()
        posts.blog!!.id = STUB_BLOG_ID
        getLabels = mock<UseCase.IGetLabels> {
            on {
                execute(STUB_BLOG_ID)
            } doReturn (mutableListOf<String>())
        }
        val presenter = TopPresenter(
            topView,
            getCurrentAccount,
            saveCurrentAccount,
            findAllBlog,
            getAllPosts,
            getLabels
        )
        presenter.onClickPosts(posts, TopContract.TYPE.POST)
        verify(topView).showEditScreen(posts, mutableListOf(), false)
    }

    @Test
    fun onClickConfirmPositiveClick() {
        // todo:
    }
}