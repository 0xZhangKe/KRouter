package com.zhangke.kouter.sample.app

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/main/")
class MainScreen(
    @RouteParam("id") val id: String,
    @RouteParam("name") val name: String,
    @RouteParam("size") val size: Double,
): Screen {

    override fun content() {
        println("MainScreen content: id=$id, name=$name, size=$size")
    }
}