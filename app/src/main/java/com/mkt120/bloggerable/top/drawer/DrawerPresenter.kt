package com.mkt120.bloggerable.top.drawer

import com.mkt120.bloggerable.usecase.GetGoogleAccount

class DrawerPresenter(
    private val view: DrawerContract.View,
    private val getGoogleAccount: GetGoogleAccount
) : DrawerContract.Presenter {

    override fun initialize() {
        view.setName(getGoogleAccount.getDisplayName())
        view.setImage(getGoogleAccount.getPhotoUrl())
    }
}