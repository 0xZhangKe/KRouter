package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.RouteParam
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home")
class HomeScreen() : Screen {

    @RouteParam("name")
    lateinit var name: String

    @RouteParam("title")
    var title: String? = null

    @RouteParam("showTitle")
    var showTitle: Boolean = false

    @RouteParam("desc")
    var desc: String
        set(value) {

        }
        get() = ""

    override fun content() {
        println("HomeScreen $name, $title, $showTitle ")
    }
}
