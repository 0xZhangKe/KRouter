package com.zhangke.kouter.sample.app

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.sample.core.Screen

fun main() {

    val homeScreen = KRouter.route<Screen>("scree/home")

    homeScreen.let {  }
}
