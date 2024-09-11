package com.zhangke.krouter.internal

expect class KRouterReflection() {

    fun createObjectFromName(name: String): Any?
}
