package com.mkt120.bloggerable.repository

import java.io.File
import java.io.FileWriter

class BackupFileRepository(private val fileDir: File) : Repository.IBackupFileRepository {

    override fun createFile(fileName: String, title: String, content: String) {
        val file = getFile(fileName)
        if (!file.exists()) {
            // ないので作る
            file.createNewFile()
        }
        val writer = FileWriter(file)
        val text = "$title\n\n$content"
        writer.write(text)
        writer.close()
    }

    override fun deleteFile(fileName: String) {
        val file = getFile(fileName)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun getFile(fileName: String): File {
        return File(fileDir, fileName)
    }
}