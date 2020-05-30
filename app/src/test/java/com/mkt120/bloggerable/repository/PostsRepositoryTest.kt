package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.datasource.DataSource
import com.mkt120.bloggerable.model.posts.Posts
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.realm.RealmList
import org.junit.After
import org.junit.Before
import org.junit.Test

class PostsRepositoryTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POST_ID = "stubPostId"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_TITLE = "stubTitle"
        private const val STUB_HTML = "stubHtml"

        private const val STUB_LABEL_1 = "STUB_LABEL_1"
        private const val STUB_LABEL_2 = "STUB_LABEL_2"
    }

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    @Test
    fun requestLivePosts() {
        val list = mutableListOf<Posts>()
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                requestPostsList(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(list.toList())
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.requestLivePosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID).test().await()
            .assertValue(list.toList())
    }

    @Test
    fun requestDraftPosts() {
        val list = mutableListOf<Posts>()
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                requestDraftPostsList(STUB_ACCESS_TOKEN, STUB_BLOG_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(list.toList())
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.requestDraftPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID).test().await()
            .assertValue(list.toList())
    }

    @Test
    fun createPosts() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                createPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_TITLE, STUB_HTML, null, false)
            } doReturn (Completable.create { emitter ->
                emitter.onComplete()
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.createPosts(
            STUB_ACCESS_TOKEN,
            STUB_BLOG_ID,
            STUB_TITLE,
            STUB_HTML,
            null,
            false
        ).test().await().assertComplete()
    }

    @Test
    fun savePosts() {
        val posts = mutableListOf<Posts>()
        posts.add(Posts())
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
        }
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.savePosts(posts, true)
        verify(mockRealmDataSource, times(1)).savePosts(posts, true)
    }

    @Test
    fun findAllPosts() {
        val ret = mutableListOf<Posts>()
        ret.add(Posts())
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
            on {
                findAllPost(STUB_BLOG_ID, true)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.findAllPosts(STUB_BLOG_ID, true).test().assertValue(ret)
    }

    @Test
    fun findPosts() {
        val ret = Posts()
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
            on {
                findPosts(STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Single.create { emitter ->
                emitter.onSuccess(ret)
            })
        }
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
        }
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.findPosts(STUB_BLOG_ID, STUB_POST_ID).test().assertValue(ret)
    }

    @Test
    fun revertPosts() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                revertPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Completable.create { emitter ->
                emitter.onComplete()
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.revertPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POST_ID).test()
            .assertComplete()
    }

    @Test
    fun updatePosts() {
        val posts = Posts()
        posts.title = STUB_TITLE
        posts.content = STUB_HTML
        posts.labels = RealmList()
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                updatePosts(STUB_ACCESS_TOKEN, posts)
            } doReturn (Completable.create { emitter ->
                emitter.onComplete()
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.updatePosts(STUB_ACCESS_TOKEN, posts).test().assertComplete()
    }

    @Test
    fun publishPosts() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                publishPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Completable.create { emitter ->
                emitter.onComplete()
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {}
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.publishPosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POST_ID).test()
            .assertComplete()
    }

    @Test
    fun deletePosts() {
        val mockBloggerApiDataSource = mock<DataSource.IBloggerApiDataSource> {
            on {
                deletePosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Completable.create { emitter ->
                emitter.onComplete()
            })
        }
        val mockRealmDataSource = mock<DataSource.IRealmDataSource> {
            on {
                deletePosts(STUB_BLOG_ID, STUB_POST_ID)
            } doReturn (Completable.create { emitter ->
                emitter.onComplete()
            })
        }
        val postsRepository = PostsRepository(mockBloggerApiDataSource, mockRealmDataSource)
        postsRepository.deletePosts(STUB_ACCESS_TOKEN, STUB_BLOG_ID, STUB_POST_ID).test()
            .assertComplete()

        postsRepository.deletePosts(STUB_BLOG_ID, STUB_POST_ID).test().assertComplete()
    }
}