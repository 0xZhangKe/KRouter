package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Router
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/detail")
class HomeDetailScreen(@Router val router: String = "") : Screen {

    override fun content() {
        println("HomeDetailScreen router is $router")
    }
}
