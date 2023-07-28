package com.zhangke.krouter

import java.util.*

object ServiceLoaderUtils {

    inline fun <reified T> findServices(): List<T> {
        val clazz = T::class.java
        return ServiceLoader.load(clazz, clazz.classLoader).iterator().asSequence().toList()
    }
}