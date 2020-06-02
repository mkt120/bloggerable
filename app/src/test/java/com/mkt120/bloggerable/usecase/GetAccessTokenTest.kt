package com.mkt120.bloggerable.usecase

import com.mkt120.bloggerable.presenter.TopPresenterTest
import com.mkt120.bloggerable.repository.Repository
import com.mkt120.bloggerable.repository.TimeRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Test

class GetAccessTokenTest {
    companion object {
        private const val STUB_USER_ID = "stubUserId"
        private const val STUB_PREF_ACCESS_TOKEN = "stubPrefAccessToken"
        private const val STUB_REFRESH_TOKEN = "stubRefreshToken"
        private const val STUB_ACCESS_TOKEN = "stubAccessToken"
        private val STUB_NOW = System.currentTimeMillis()
    }

    @Test
    fun confirm_pref_access_token_1() {
        val mockRepository = mock<Repository.IAccountRepository> {
            // Preferenceから取得
            on {
                getAccessToken(
                    STUB_USER_ID,
                    STUB_NOW
                )
            } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_PREF_ACCESS_TOKEN) })
            // リフレッシュトークン
            on {
                getRefreshToken(STUB_USER_ID)
            } doReturn (STUB_REFRESH_TOKEN)
            // リフレッシュ要求
            on {
                requestRefresh(STUB_USER_ID, STUB_REFRESH_TOKEN, STUB_NOW)
            } doReturn (Single.create {})
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }
        val getAccessToken = GetAccessToken(mockRepository, timeRepository)
        val single = getAccessToken.execute(STUB_USER_ID).test()
        single.assertValue(STUB_PREF_ACCESS_TOKEN)
    }

    @Test
    fun confirm_request_access_token() {
        val mockRepository = mock<Repository.IAccountRepository> {
            // Preferenceから取得
            on {
                getAccessToken(
                    STUB_USER_ID,
                    STUB_NOW
                )
                // トークンが有効期限切れであれば、onErrorが発生する
            } doReturn (Single.create { emitter -> emitter.onError(Exception()) })
            // リフレッシュトークン
            on {
                getRefreshToken(STUB_USER_ID)
            } doReturn (STUB_REFRESH_TOKEN)
            // リフレッシュ要求
            on {
                requestRefresh(STUB_USER_ID, STUB_REFRESH_TOKEN, STUB_NOW)
            } doReturn (Single.create { emitter -> emitter.onSuccess(STUB_ACCESS_TOKEN) })
        }
        val timeRepository = mock<Repository.ITimeRepository> {
            on {
                getCurrentTime()
            } doReturn (STUB_NOW)
        }
        val getAccessToken = GetAccessToken(mockRepository, timeRepository)
        val single = getAccessToken.execute(STUB_USER_ID).test()
        single.assertValue(STUB_ACCESS_TOKEN)
    }

}