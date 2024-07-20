package com.zhangke.kouter.sample.app

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.sample.core.Screen
import kotlin.system.measureTimeMillis

fun main() {
    repeat(10) {
        val costTime = measureTimeMillis {
            KRouter.route<Screen>("screen/home") {
                with("name", "10")
                with("list", listOf("1", "2", "3", "${System.currentTimeMillis()}"))
            }?.content()
        }
        println("[$it]costTime: ${costTime}")
    }

    repeat(10) {
        val costTime = measureTimeMillis {
            KRouter.route<Screen>("screen/home/detail")?.content()
        }
        println("[$it]costTime: ${costTime}")
    }

    repeat(10) {
        val costTime = measureTimeMillis {
            KRouter.route<Screen>("screen/home/second")?.content()
        }
        println("[$it]costTime: ${costTime}")
    }

    KRouter.route<Screen>("screen/home/landing")?.content()
    KRouter.route<Screen>("krouter://sample.com/screen/home?name=zhangke")?.content()
    KRouter.route<Screen>("screen/home/detail?name=zhangke")?.content()
    KRouter.route<Screen>("screen/profile?name=zhangke")?.content()
    KRouter.route<Screen>("screen/profile/detail?name=zhangke")?.content()
    KRouter.route<Screen>("screen/setting")?.content()
    KRouter.route<Screen>("screen/setting/detail")?.content()
}
