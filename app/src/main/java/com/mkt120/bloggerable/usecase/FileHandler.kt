package com.mkt120.bloggerable.usecase

import android.util.Log
import java.io.File
import java.util.regex.Pattern

open class FileHandler(private val fileDir: File) {
    companion object {
        val NEW_LINE_PATTERN: Pattern = Pattern.compile("\\n")
        private const val FILE_EXTENSION: String = ".drf"
    }

    protected fun getFile(blogId: String, postId: String?): File {
        var fileName = blogId.plus("_")
        if (!postId.isNullOrEmpty()) {
            fileName = fileName.plus(postId)
        }
        return getFile(fileName.plus(FILE_EXTENSION))
    }

    protected fun getFile(fileName: String): File {
        Log.i("FileHandler", "execute dir=${fileDir.path}, fileName=$fileName")
        return File(fileDir, "$fileName.txt")
    }
}