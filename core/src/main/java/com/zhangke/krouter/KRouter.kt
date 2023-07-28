package com.zhangke.krouter

import java.util.*

object KRouter {

    inline fun <reified T> find(route: String): T? {
        val target = findImplement<T>() ?: return null
        target::class.annotations.find {
            it is Route
        }?.let {
            println((it as Route).route)
        }
        return null
    }

    inline fun <reified T> findImplement(): T {
        val implements = findImplements<T>()
        return implements.first()
    }

    inline fun <reified T> findImplements(): List<T> {
        val clazz = T::class.java
        return ServiceLoader.load(clazz, clazz.classLoader).iterator().asSequence().toList()
    }
}
