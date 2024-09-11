package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/profile")
class ProfileScreen : Screen {

    override fun content() {
        println("ProfileScreen route is null")
    }
}
