package com.mkt120.bloggerable.usecase

import android.util.Log
import com.mkt120.bloggerable.repository.Repository

class CreateBackupFile(private val backupFileRepository: Repository.IBackupFileRepository) :
    UseCase.ICreateBackupFile {

    companion object {
        private const val FILE_EXTENSION: String = ".drf"
    }

    override fun execute(blogId: String, postId: String?, title: String, content: String) {
        Log.i("CreateBackupFile", "execute blogId=$blogId, postId=$postId")
        var fileName = blogId.plus("_")
        if (!postId.isNullOrEmpty()) {
            fileName = fileName.plus(postId)
        }
        backupFileRepository.createFile(fileName.plus(FILE_EXTENSION), title, content)
    }
}