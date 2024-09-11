package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/profile/second")
class ProfileSecondScreen: Screen {

    override fun content() {
        println("ProfileSecondScreen content")
    }
}