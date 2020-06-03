package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class CreateBackupFileTest {

    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POST_ID = "stubPostId"
        private const val STUB_TITLE = "stubTitle"
        private const val STUB_CONTENT = "stubContent"
        private const val FILE_EXTENSION = ".drf"
    }

    private lateinit var backupFileRepository: Repository.IBackupFileRepository

    @Before
    fun setUp() {
        backupFileRepository = mock<Repository.IBackupFileRepository>()
    }

    @Test
    fun execute1() {
        val create = CreateBackupFile(backupFileRepository)
        create.execute(STUB_BLOG_ID, STUB_POST_ID, STUB_TITLE, STUB_CONTENT)
        verify(backupFileRepository).createFile(
            STUB_BLOG_ID.plus("_").plus(STUB_POST_ID).plus(FILE_EXTENSION),
            STUB_TITLE,
            STUB_CONTENT
        )
    }
    @Test
    fun execute2() {
        val create = CreateBackupFile(backupFileRepository)
        create.execute(STUB_BLOG_ID, null, STUB_TITLE, STUB_CONTENT)
        verify(backupFileRepository).createFile(
            STUB_BLOG_ID.plus("_").plus(FILE_EXTENSION),
            STUB_TITLE,
            STUB_CONTENT
        )
    }
}