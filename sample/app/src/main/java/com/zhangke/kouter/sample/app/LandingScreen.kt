package com.zhangke.kouter.sample.app

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/landing")
class LandingScreen(
    @RouteParam("url") val url: String,
    @RouteParam("showTitle") val showTitle: Boolean,
    @RouteParam("index") val index: Short,
    @RouteParam("title") val title: String?,
) : Screen {

    override fun content() {
        println("LandingScreen content: url=$url, showTitle=$showTitle, index=$index, title=$title")
    }
}