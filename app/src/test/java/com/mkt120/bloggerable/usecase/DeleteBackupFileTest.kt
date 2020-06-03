package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class DeleteBackupFileTest {

    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POST_ID = "stubPostId"
        private const val FILE_EXTENSION = ".drf"
    }

    private lateinit var backupFileRepository: Repository.IBackupFileRepository

    @Before
    fun setUp() {
        backupFileRepository = mock<Repository.IBackupFileRepository>()
    }

    @Test
    fun execute1() {
        val delete = DeleteBackupFile(backupFileRepository)
        delete.execute(STUB_BLOG_ID, STUB_POST_ID)
        verify(backupFileRepository).deleteFile(
            STUB_BLOG_ID.plus("_").plus(STUB_POST_ID).plus(FILE_EXTENSION)
        )
    }
    @Test
    fun execute2() {
        val delete = DeleteBackupFile(backupFileRepository)
        delete.execute(STUB_BLOG_ID, null)
        verify(backupFileRepository).deleteFile(
            STUB_BLOG_ID.plus("_").plus(FILE_EXTENSION)
        )
    }
}