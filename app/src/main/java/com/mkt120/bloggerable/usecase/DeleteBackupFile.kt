package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository

class DeleteBackupFile(private val backupFileRepository: Repository.IBackupFileRepository) :
    UseCase.IDeleteBackupFile {

    companion object {
        private const val FILE_EXTENSION: String = ".drf"
    }

    override fun execute(blogId: String, postId: String?) {
        var fileName = blogId.plus("_")
        if (!postId.isNullOrEmpty()) {
            fileName = fileName.plus(postId)
        }
        backupFileRepository.deleteFile(fileName.plus(FILE_EXTENSION))
    }
}