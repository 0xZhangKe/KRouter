package com.zhangke.krouter.sample.setting

import com.zhangke.krouter.KRouter
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Param
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/setting")
object SettingScreen : Screen {

    @Param(name = KRouter.PRESET_ROUTER)
    var router: String = "SettingsScreen"

    @Param(name = KRouter.PRESET_PARAMS)
    var title: Map<String, Any?> = emptyMap()

    override fun content() {
        println("Settings: router: $router")

        title.forEach {
            println("Settings: [${it.key}]: ${it.value}")
        }
    }
}
