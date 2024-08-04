package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/landing")
class HomeLandingScreen(val router: String = "") : Screen {

    override fun content() {
        println("HomeLandingScreen: $router")
    }
}