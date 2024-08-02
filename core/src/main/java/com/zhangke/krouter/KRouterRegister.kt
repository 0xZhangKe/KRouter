package com.zhangke.krouter

import kotlin.reflect.KClass

interface KRouterRegister {
    fun get(): Map<String, List<KClass<*>>>
}