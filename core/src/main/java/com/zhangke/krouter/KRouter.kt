package com.zhangke.krouter

object KRouter {

//    fun <T> route(route: String): T?{
//        // find class by RouteMapping
//        // parse params from route
//        // parse params from destination class
//        // check params legal
//        // construct object with params
//        return null
//    }

    inline fun <reified T : Any> route(router: String): T? {
        val serviceList = ServiceLoaderUtils.findServices<T>()
        val targetService = ZZZKRouterInternalUtil.findServiceByRouter(serviceList, router) ?: return null
        return ZZZKRouterInternalUtil.getFilledRouterService(router, targetService) as T
    }
}
