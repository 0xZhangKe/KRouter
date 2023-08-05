package com.zhangke.kouter.sample.app

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.sample.core.Screen

fun main() {
    KRouter.route<Screen>("screen/home?name=zhangke")?.content()
    KRouter.route<Screen>("krouter://sample.com/screen/home?name=zhangke")?.content()
    KRouter.route<Screen>("screen/home/detail?name=zhangke")?.content()
    KRouter.route<Screen>("screen/profile?name=zhangke")?.content()
    KRouter.route<Screen>("screen/profile/detail?name=zhangke")?.content()
    KRouter.route<Screen>("screen/setting")?.content()
    KRouter.route<Screen>("screen/setting/detail")?.content()
}
