package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Param
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home")
class HomeScreen(
    val name: String,
) : Screen {

    @Param(required = true)
    var list: List<String> = emptyList()

    override fun content() {
        println(
            "HomeScreen route is name: $name ${list.joinToString(", ") { it }}"
        )
    }
}
