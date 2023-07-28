package com.zhangke.krouter

import java.util.*

object ServiceLoaderUtils {

    inline fun <reified T> findImplement(): T {
        val implements = findImplements<T>()
        return implements.first()
    }

    inline fun <reified T> findImplements(): List<T> {
        val clazz = T::class.java
        return ServiceLoader.load(clazz, clazz.classLoader).iterator().asSequence().toList()
    }
}