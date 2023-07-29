package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.krouter.sample.core.Screen

@Destination(router = "scree/home/detail")
class HomeDetailScreen(@Router val router: String = "") : Screen {

    override fun content() {
        println("HomeDetailScreen router is $router")
    }
}
