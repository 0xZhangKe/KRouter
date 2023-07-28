package com.zhangke.krouter

import java.net.URI
import java.util.*

object KRouter {

    val cache = mutableMapOf<String, List<Any>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> route(router: String): T? {
        val key = T::class.qualifiedName!!
        val serviceList = cache.getOrPut(key) {
            ServiceLoaderUtils.findServices<T>() as List<Any>
        } as List<T>
        return findServiceByRouter(serviceList, router)
    }

    inline fun <reified T> findServiceByRouter(
        serviceList: List<T>,
        router: String,
    ): T? {
        val routerUri = URI.create(router).baseUri
        val service = serviceList.firstOrNull {
            val serviceRouter = getRouterFromAnnotation(it)
            if (serviceRouter.isNullOrEmpty().not()) {
                val serviceUri = URI.create(serviceRouter!!).baseUri
                serviceUri == routerUri
            }else{
                false
            }
        }
        return service
    }

    inline fun <reified T> getRouterFromAnnotation(target: T): String? {
        if (target == null) return null
        val routerAnnotation = target!!::class.annotations.firstOrNull {
            it is Router
        } as? Router ?: return null
        return routerAnnotation.router
    }

    fun flush() {
        cache.clear()
    }
}
