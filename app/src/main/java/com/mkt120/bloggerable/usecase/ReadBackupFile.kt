package com.mkt120.bloggerable.usecase

import android.util.Log
import com.mkt120.bloggerable.model.posts.Posts
import java.io.File

class ReadBackupFile(fileDir: File) : FileHandler(fileDir) {

    fun execute(blogId: String, postId: String? = null): Posts? {
        Log.i("ReadBackupFile", "execute blogId=$blogId, postId=$postId")
        return read(getFile(blogId, postId))
    }

    /**
     * ファイルを読み込む
     */
    public fun read(file: File): Posts? {
        if (!file.exists()) {
            return null
        }
        val reader = file.bufferedReader()
        val read: String = reader.use {
            // 勝手にcloseしてくれるらしい
            it.readText()
        }
        val title = read.split(NEW_LINE_PATTERN)[0]

        var html: String = if (read.split(NEW_LINE_PATTERN).size > 3) {
            read.split(NEW_LINE_PATTERN)[2]
        } else {
            ""
        }
        return Posts().apply {
            this.title = title
            this.content = html
        }
    }
}