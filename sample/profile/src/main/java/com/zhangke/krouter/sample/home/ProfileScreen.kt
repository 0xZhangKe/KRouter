package com.zhangke.krouter.sample.home

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Param
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/profile")
class ProfileScreen : Screen {

    @Param(KRouter.PRESET_ROUTER)
    lateinit var router: String

    override fun content() {
        println("ProfileScreen route is $router")
    }
}
