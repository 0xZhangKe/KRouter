package com.zhangke.kouter.sample.app

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/test")
class DebugScreen : Screen {

    override fun content() {
        println("DebugScreen")
    }
}