package com.mkt120.bloggerable.usecase

import android.util.Log
import com.mkt120.bloggerable.model.posts.Posts
import com.mkt120.bloggerable.repository.Repository
import java.io.File
import java.util.regex.Pattern

class ReadBackupFile(private val backupFileRepository: Repository.IBackupFileRepository) :UseCase.IReadBackupFile{

    companion object {
        val NEW_LINE_PATTERN: Pattern = Pattern.compile("\\n")
        private const val FILE_EXTENSION: String = ".drf"
    }

    override fun execute(blogId: String, postId: String?): Posts? {
        Log.i("ReadBackupFile", "execute blogId=$blogId, postId=$postId")
        var fileName = blogId.plus("_")
        if (!postId.isNullOrEmpty()) {
            fileName = fileName.plus(postId)
        }
        val file = backupFileRepository.getFile(fileName.plus(FILE_EXTENSION))
        return read(file)
    }

    /**
     * ファイルを読み込む
     */
    private fun read(file: File): Posts? {
        if (!file.exists()) {
            return null
        }
        val reader = file.bufferedReader()
        val read: String = reader.use {
            // 勝手にcloseしてくれるらしい
            it.readText()
        }
        val title = read.split(NEW_LINE_PATTERN)[0]

        var html: String = if (read.split(NEW_LINE_PATTERN).size >= 3) {
            read.split(NEW_LINE_PATTERN, 3)[2]
        } else {
            ""
        }
        return Posts().apply {
            this.title = title
            this.content = html
        }
    }
}