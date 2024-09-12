package com.zhangke.kouter.sample.app

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.sample.core.Screen

fun main() {

    KRouter.route<Screen>("screen/main?name=zhangke&id=123&size=12")?.content()
    KRouter.route<Screen>("screen/home/landing?url=mocked_url&showTitle=false&index=3")?.content()

    KRouter.route<Screen>("screen/home?name=zhangke&showTitle=false")?.content()
    KRouter.route<Screen>("screen/home/detail?id=123")?.content()
    KRouter.route<Screen>("screen/home/second")?.content()
    KRouter.route<Screen>("screen/home/landing")?.content()
    KRouter.route<Screen>("krouter://sample.com/screen/home?name=zhangke")?.content()
    KRouter.route<Screen>("screen/profile?name=zhangke")?.content()
    KRouter.route<Screen>("demo://router/screen/profile/detail?userId=1&userName=ke")?.content()
    KRouter.route<Screen>("screen/profile/second")?.content()
    KRouter.route<Screen>("screen/setting")?.content()
    KRouter.route<Screen>("screen/setting/detail?index=1&title=title&id=33&showTitle=false&ratio=1")?.content()
}
