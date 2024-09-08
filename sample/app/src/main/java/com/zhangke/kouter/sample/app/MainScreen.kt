package com.zhangke.kouter.sample.app

import com.zhangke.krouter.Destination
import com.zhangke.krouter.KRouterParams
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/main/")
class MainScreen(
    @KRouterParams("id") val id: String,
    @KRouterParams("name") val name: String,
    @KRouterParams("size") val size: Double,
): Screen {

    override fun content() {
        super.content()
    }
}