package com.zhangke.krouter

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object KRouterMapping {

    private val routeMapping: Map<String, KClass<*>> = buildRouteToClassMapping()
    private val dynamicRouteMapping = ConcurrentHashMap<String, KClass<*>>()

    fun findClass(route: String): KClass<*>? {
        routeMapping[route]?.let { return it }
        dynamicRouteMapping[route]?.let { return it }
        return null
    }

    fun registerRoute(route: String, clazz: KClass<*>) {
        dynamicRouteMapping[route] = clazz
    }

    fun unregisterRoute(route: String): KClass<*>? {
        return dynamicRouteMapping.remove(route)
    }

    private fun buildRouteToClassMapping(): Map<String, KClass<*>> {
        val routeToClassMapping = mutableMapOf<String, KClass<*>>()
        // pending inject by compiler
        return routeToClassMapping
    }
}
