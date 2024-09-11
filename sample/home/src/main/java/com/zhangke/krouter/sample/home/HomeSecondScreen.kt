package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/second")
class HomeSecondScreen(val router: String = "default home screen rout"): Screen {

    override fun content() {
        println("HomeSecondScreen: $router")
    }
}