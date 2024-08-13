package com.zhangke.kouter.sample.app

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.generated.KRouterInjectMap
import com.zhangke.krouter.sample.core.Screen
import kotlin.system.measureTimeMillis

fun main() {
    val initCost = measureTimeMillis { KRouter.init(KRouterInjectMap::getMap) }
    println("init cost: $initCost")

    val map = mapOf("router" to "zhangke")
    KRouter.route<Screen>("screen/home/detail?router=detail")?.content()
    KRouter.route<Screen>("screen/home/second?router=second", map)?.content()

    KRouter.route<Screen>("screen/home/landing", map)?.content()
    // TODO URI类不能在jvm之外的平台上使用，所以需要单独针对各个平台做处理
    // KRouter.route<Screen>("krouter://sample.com/screen/home?name=zhangke", map)?.content()
    KRouter.route<Screen>("screen/home/detail?name=zhangke", map)?.content()
    KRouter.route<Screen>("screen/profile?name=zhangke", map)?.content()
    KRouter.route<Screen>("screen/profile/detail?name=zhangke", map)?.content()
    KRouter.route<Screen>("screen/setting?router=name&name=test", map)?.content()
    KRouter.route<Screen>("screen/setting/detail", map)?.content()
}
