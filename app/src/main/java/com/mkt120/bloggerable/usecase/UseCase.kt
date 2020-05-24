package com.mkt120.bloggerable.usecase

import io.reactivex.Single

interface UseCase {
    interface IGetAccessToken {
        fun execute(userId: String, now:Long): Single<String>
    }
}