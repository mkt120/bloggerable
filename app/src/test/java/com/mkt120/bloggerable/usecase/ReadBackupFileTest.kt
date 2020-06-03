package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.FileWriter

class ReadBackupFileTest {

    companion object {
        private const val STUB_BLOG_ID = "stubBlogId"
        private const val STUB_POST_ID = "stubPostId"
        private const val STUB_TITLE = "stubTitle"
        private const val STUB_CONTENT = "stubContent"
        private const val FILE_EXTENSION = ".drf"
    }

    private lateinit var backupFileRepository: Repository.IBackupFileRepository

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    @Test
    fun execute() {
        val file = File(temporaryFolder.newFolder(), STUB_BLOG_ID.plus("_").plus(STUB_POST_ID).plus(FILE_EXTENSION))
        if (!file.exists()) {
            // ないので作る
            file.createNewFile()
        }
        val writer = FileWriter(file)
        val text = "$STUB_TITLE\n\n$STUB_CONTENT"
        writer.write(text)
        writer.close()

        backupFileRepository = mock<Repository.IBackupFileRepository>() {
            on {
                getFile(STUB_BLOG_ID.plus("_").plus(STUB_POST_ID).plus(FILE_EXTENSION))
            } doReturn (file)
        }
        val read = ReadBackupFile(backupFileRepository);
        val post = read.execute(STUB_BLOG_ID, STUB_POST_ID)
        Assert.assertNotNull(post)
        Assert.assertEquals(post!!.title, STUB_TITLE)
        Assert.assertEquals(post.content, STUB_CONTENT)
    }
    @Test
    fun execute2() {
        val file = File(temporaryFolder.newFolder(), STUB_BLOG_ID.plus("_").plus(STUB_POST_ID).plus(FILE_EXTENSION))
        backupFileRepository = mock<Repository.IBackupFileRepository>() {
            on {
                getFile(STUB_BLOG_ID.plus("_").plus(STUB_POST_ID).plus(FILE_EXTENSION))
            } doReturn (file)
        }
        val read = ReadBackupFile(backupFileRepository);
        val post = read.execute(STUB_BLOG_ID, STUB_POST_ID)
        Assert.assertNull(post)
    }
}