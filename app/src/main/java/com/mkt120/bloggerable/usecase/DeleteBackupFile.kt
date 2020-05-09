package com.mkt120.bloggerable.usecase

import java.io.File

class DeleteBackupFile(fileDir: File) : FileHandler(fileDir) {

    fun execute(blogId: String, postId: String?) {
        val file = getFile(blogId, postId)
        if (file.exists()) {
            file.delete()
        }
    }
}