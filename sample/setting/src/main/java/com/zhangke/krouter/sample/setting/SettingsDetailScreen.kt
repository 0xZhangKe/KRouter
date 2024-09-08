package com.zhangke.krouter.sample.setting

import com.zhangke.krouter.Destination
import com.zhangke.krouter.KRouterParams
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/setting/detail")
class SettingsDetailScreen(
    @KRouterParams("index") val index: Int,
    @KRouterParams("title") val title: String,
    @KRouterParams("id") val settingId: Long,
    @KRouterParams("showTitle") val showTitle: Boolean,
    @KRouterParams("ratio") val ratio: Float = 1.0F,
    @KRouterParams("size") val size: Double?,
    val extra: Any? = null,
) : Screen {

    override fun content() {
        println("SettingsDetailScreen index is $index, $title, $settingId, $showTitle, $ratio, $size, $extra")
    }
}
