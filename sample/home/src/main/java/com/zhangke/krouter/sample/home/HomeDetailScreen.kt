package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.KRouterParams
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/detail")
class HomeDetailScreen(
    @KRouterParams("id") val id: String,
    val name: String = "",
    val desc: String? = null,
) : Screen {

    override fun content() {
        println("HomeDetailScreen id is $id")
    }
}
