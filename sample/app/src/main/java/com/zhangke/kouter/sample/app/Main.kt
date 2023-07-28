package com.zhangke.kouter.sample.app

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.sample.core.Screen

fun main() {
    val screens = KRouter.findImplements<Screen>()
    println(screens.joinToString(",") { it::class.simpleName.toString() })
}
