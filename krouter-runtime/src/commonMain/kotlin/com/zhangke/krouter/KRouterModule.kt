package com.zhangke.krouter

interface KRouterModule {

    fun route(uri: String): Any?
}
