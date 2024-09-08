package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home")
class HomeScreen() : Screen {

    override fun content() {
        println("HomeScreen route is ")
    }
}
