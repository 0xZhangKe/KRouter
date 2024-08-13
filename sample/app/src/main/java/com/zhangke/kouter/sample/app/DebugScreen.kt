package com.zhangke.kouter.sample.app

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination(router = ["screen/test"])
class DebugScreen(
    val name: String = "debugScreen",
    val title: String?,
) : Screen {

    override fun content() {
        println("[DebugScreen]: name -> $name, title -> $title")
    }
}