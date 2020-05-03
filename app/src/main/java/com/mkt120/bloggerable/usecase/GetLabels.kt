package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.repository.BlogRepository

class GetLabels(
    private val blogsRepository: BlogRepository
) {
    fun execute(blogId: String): ArrayList<String> = blogsRepository.findAllLabels(blogId)
}