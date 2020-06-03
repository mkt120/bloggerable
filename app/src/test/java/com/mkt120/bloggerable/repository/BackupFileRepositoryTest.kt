package com.mkt120.bloggerable.repository

import com.mkt120.bloggerable.usecase.ReadBackupFile
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BackupFileRepositoryTest {

    companion object {
        private const val STUB_FILENAME = "stubFileName.drf"
        private const val STUB_TITLE = "stubTitle"
        private const val STUB_CONTENT = "stubContent"
    }

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    @Test
    fun getFile() {
        val backupFileRepository = BackupFileRepository(temporaryFolder.newFolder())
        val file = backupFileRepository.getFile(STUB_FILENAME)
        Assert.assertEquals(file.exists(), false)
    }

    @Test
    fun createFile() {
        val backupFileRepository = BackupFileRepository(temporaryFolder.newFolder())
        backupFileRepository.createFile(STUB_FILENAME, STUB_TITLE, STUB_CONTENT)
        val created = backupFileRepository.getFile(STUB_FILENAME)
        Assert.assertEquals(created.exists(), true)
        val reader = created.bufferedReader()
        val read: String = reader.use {
            it.readText()
        }
        val title = read.split(ReadBackupFile.NEW_LINE_PATTERN)[0]
        Assert.assertEquals(STUB_TITLE, title)

        val html: String = if (read.split(ReadBackupFile.NEW_LINE_PATTERN).size >= 3) {
            read.split(ReadBackupFile.NEW_LINE_PATTERN, 3)[2]
        } else {
            ""
        }
        Assert.assertEquals(STUB_CONTENT, html)
    }

    @Test
    fun deleteFile() {
        val backupFileRepository = BackupFileRepository(temporaryFolder.newFolder())
        backupFileRepository.createFile(STUB_FILENAME, STUB_TITLE, STUB_CONTENT)
        var file = backupFileRepository.getFile(STUB_FILENAME)
        Assert.assertEquals(file.exists(), true)
        backupFileRepository.deleteFile(STUB_FILENAME)
        file = backupFileRepository.getFile(STUB_FILENAME)
        Assert.assertEquals(file.exists(), false)
    }
}