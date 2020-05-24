package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Test

class UpdatePostsTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private val STUB_NOW = System.currentTimeMillis()

        private const val STUB_TITLE = "stubTitle"
        private const val STUB_HTML = "stubHtml"

        private const val STUB_LABEL_1 = "STUB_LABEL_1"
        private const val STUB_LABEL_2 = "STUB_LABEL_2"
    }

    private val mockGetAccessToken = mock<UseCase.IGetAccessToken> {
        on {
            execute(STUB_USER_ID, STUB_NOW)
        } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
    }

    @Test
    fun execute() {
        val stubPosts = Posts()
        val postsRepository = mock<Repository.IPostsRepository> {
            on {
                updatePosts(STUB_ACCESS_TOKEN, stubPosts)
            } doReturn (Completable.create { emitter -> emitter.onComplete() })
        }
        val updatePosts = UpdatePosts(mockGetAccessToken, postsRepository)
        updatePosts.execute(STUB_NOW, STUB_USER_ID, stubPosts, STUB_TITLE, STUB_HTML, null).test()
            .assertNoErrors().onComplete()
        verify(postsRepository).updatePosts(check {
            it == STUB_ACCESS_TOKEN
        }, check {
            assert(it.title == STUB_TITLE)
            assert(it.content == STUB_HTML)
            assert(it.labels!!.isEmpty())
        })
    }


    @Test
    fun execute2() {
        val stubPosts = Posts()
        val labels = mutableListOf<String>().apply {
            add(STUB_LABEL_1)
            add(STUB_LABEL_2)
        }.toTypedArray()

        val postsRepository = mock<Repository.IPostsRepository> {
            on {
                updatePosts(STUB_ACCESS_TOKEN, stubPosts)
            } doReturn (Completable.create { emitter -> emitter.onComplete() })
        }
        val updatePosts = UpdatePosts(mockGetAccessToken, postsRepository)
        updatePosts.execute(STUB_NOW, STUB_USER_ID, stubPosts, STUB_TITLE, STUB_HTML, labels)
            .test()
            .assertNoErrors().onComplete()
        verify(postsRepository).updatePosts(check {
            it == STUB_ACCESS_TOKEN
        }, check {
            assert(it.title == STUB_TITLE)
            assert(it.content == STUB_HTML)
            assert(it.labels!!.size == 2)
            assert(it.labels!![0] == STUB_LABEL_1)
            assert(it.labels!![1] == STUB_LABEL_2)
        })
    }
}