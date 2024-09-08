package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/landing")
class HomeLandingScreen(val router: String = ""): Screen {

    override fun content() {
        println("HomeLandingScreen: $router")
    }
}