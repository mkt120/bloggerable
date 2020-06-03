package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.Repository

class GetLabels(
    private val blogsRepository: Repository.IBlogRepository
) : UseCase.IGetLabels {
    override fun execute(blogId: String): List<String> = blogsRepository.findAllLabels(blogId)
}