package com.zhangke.krouter

import com.zhangke.krouter.utils.ServiceLoaderUtils

@Suppress("UNCHECKED_CAST")
object KRouter {
    private val getServicesClasses = ServiceLoaderUtils::getServicesClasses
    private val findServiceByRouter = ZZZKRouterInternalUtil::findServiceByRouter
    private val getFilledRouterService = ZZZKRouterInternalUtil::getFilledRouterService

    inline fun <reified T : Any> route(
        router: String,
        noinline request: RouteRequest.() -> Unit = { }
    ): T? = route(
        router = router,
        className = T::class.java.name,
        request = request
    )

    fun <T : Any> route(
        router: String,
        className: String,
        request: RouteRequest.() -> Unit = { }
    ): T? {
        // get all services that match T class
        val serviceList = getServicesClasses(className)
        val routeRequest = RouteRequest()
            .apply(request)
            .also { it.with("router", router) } // TODO remove it when @Router is removed

        // get target service by router
        val targetService = findServiceByRouter(serviceList, router)
            ?: return null

        // fill service with request obj
        return getFilledRouterService(routeRequest, targetService) as T
    }
}
