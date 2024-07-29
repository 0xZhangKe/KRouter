package com.zhangke.krouter

import com.zhangke.krouter.utils.ServiceLoaderUtils

object KRouter {
    val getServicesClasses = ServiceLoaderUtils::getServicesClasses
    val findServiceByRouter = ZZZKRouterInternalUtil::findServiceByRouter
    val getFilledRouterService = ZZZKRouterInternalUtil::getFilledRouterService

    inline fun <reified T : Any> route(
        router: String,
        request: RouteRequest.() -> Unit = { }
    ): T? {
        // get all services that match T class
        val serviceList = getServicesClasses(T::class.java.name)
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
