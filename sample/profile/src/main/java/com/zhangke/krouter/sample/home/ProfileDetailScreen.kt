package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.RouteParam
import com.zhangke.krouter.sample.core.Screen

@Destination("demo://router/screen/profile/detail")
data class ProfileDetailScreen(
    @RouteParam("userId") private val _userId: String? = null,
    @RouteParam("userName") private val _userName: String? = null,
    private val userId: String = _userId!!,
    private val userName: String? = _userName!!,
): Screen{

    override fun content() {
        println("ProfileDetailScreen userId:$userId, userName:$userName")
    }
}
