package com.zhangke.krouter

import kotlin.reflect.KClass

interface KRouterModule {

    fun route(uri: String): Any?

    fun getServices(service: KClass<*>): List<KClass<*>>
}
