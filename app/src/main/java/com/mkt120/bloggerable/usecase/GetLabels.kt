package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.BlogRepository

class GetLabels(
    private val blogsRepository: BlogRepository
) : UseCase.IGetLabels {
    override fun execute(blogId: String): List<String> = blogsRepository.findAllLabels(blogId)
}