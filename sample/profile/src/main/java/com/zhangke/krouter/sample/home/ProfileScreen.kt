package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/profile")
class ProfileScreen : Screen {

    @Router
    lateinit var router: String

    override fun content() {
        println("ProfileScreen route is $router")
    }
}
