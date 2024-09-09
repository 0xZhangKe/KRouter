package com.zhangke.krouter.sample.setting

import com.zhangke.krouter.Destination
import com.zhangke.krouter.RouteParam
import com.zhangke.krouter.sample.core.Screen

@Destination("screen/setting/detail")
class SettingsDetailScreen(
    @RouteParam("index") val index: Int,
    @RouteParam("title") val title: String,
    @RouteParam("id") val settingId: Long,
    @RouteParam("showTitle") val showTitle: Boolean,
    @RouteParam("ratio") val ratio: Float = 1.0F,
    @RouteParam("size") val size: Double?,
    val extra: Any? = null,
) : Screen {

    override fun content() {
        println("SettingsDetailScreen index is $index, $title, $settingId, $showTitle, $ratio, $size, $extra")
    }
}
