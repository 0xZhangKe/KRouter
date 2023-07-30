package com.zhangke.krouter

object KRouter {

    inline fun <reified T : Any> route(router: String): T? {
        val serviceList = ServiceLoaderUtils.findServices<T>()
        val targetService = ZZZKRouterInternalUtil.findServiceByRouter(serviceList, router) ?: return null
        return ZZZKRouterInternalUtil.getFilledRouterService(router, targetService) as T
    }
}
