package com.zhangke.krouter.internal

actual class KRouterReflection {

    actual fun createObjectFromName(name: String): Any? {
        return Class.forName(name).constructors.first().newInstance()
    }
}
