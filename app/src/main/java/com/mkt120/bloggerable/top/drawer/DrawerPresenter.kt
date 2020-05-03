package com.mkt120.bloggerable.top.drawer

import com.mkt120.bloggerable.usecase.GetAllAccount
import com.mkt120.bloggerable.usecase.GetCurrentAccount

class DrawerPresenter(
    private val view: DrawerContract.View,
    private val getAllAccount: GetAllAccount,
    private val getCurrentAccount: GetCurrentAccount
) : DrawerContract.Presenter {

    override fun initialize() {
        val account = getCurrentAccount.execute()!!
        view.setName(account.getName())
        view.setImage(account.getPhotoUrl())

        // todo: マルチアカウント対応
        val accounts = getAllAccount.execute()
    }
}