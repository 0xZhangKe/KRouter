package com.zhangke.krouter.sample.home

import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/home", "krouter://sample.com/screen/home")
class HomeScreen(@Router val router: String = "") : Screen {

    override fun content() {
        println("HomeScreen route is $router")
    }
}
