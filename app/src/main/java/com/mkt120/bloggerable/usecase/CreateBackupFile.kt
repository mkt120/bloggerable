package com.mkt120.bloggerable.usecase

import android.util.Log
import java.io.File
import java.io.FileWriter

class CreateBackupFile(fileDir: File) : FileHandler(fileDir) {

    fun execute(blogId: String, postId: String? = null, title: String, content: String) {
        Log.i("CreateBackupFile", "execute blogId=$blogId, postId=$postId")
        val file = getFile(blogId, postId)
        write(file, title, content)
    }

    /**
     * ファイルに書き込みする
     */
    public fun write(file: File, title: String, content: String) {
        if (!file.exists()) {
            // ないので作る
            file.createNewFile()
        }
        val writer = FileWriter(file)
        val text = "$title\n\n$content"
        writer.write(text)
        writer.close()
    }
}