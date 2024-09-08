package com.zhangke.kouter.sample.app

import com.zhangke.krouter.Destination
import com.zhangke.krouter.KRouterParams
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home/landing")
class LandingScreen(
    @KRouterParams("url") val url: String,
    @KRouterParams("showTitle") val showTitle: Boolean,
    @KRouterParams("index") val index: Short,
    @KRouterParams("title") val title: String?,
) : Screen {

    override fun content() {
        println("LandingScreen content: url=$url, showTitle=$showTitle, index=$index, title=$title")
    }
}