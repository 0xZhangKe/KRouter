package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/detail")
class HomeDetailScreen(
    @RouteParam("id") val id: String,
    val name: String = "",
    val desc: String? = null,
) : Screen {

    override fun content() {
        println("HomeDetailScreen id is $id")
    }
}
