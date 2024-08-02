package com.zhangke.krouter

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object KRouter {
    private val findServiceByRouter = ZZZKRouterInternalUtil::findServiceByRouter
    private val getFilledRouterService = ZZZKRouterInternalUtil::getFilledRouterService
    private val servicesMap: Map<String, List<KClass<*>>> by lazy { getServicesClasses() }

    fun init() {
        println(servicesMap.size)
    }

    private fun getServicesClasses(): Map<String, List<KClass<*>>> {
        val clazz = Class.forName("com.zhangke.krouter.generated.KRouterInjectMap")
        val register = clazz.declaredConstructors.firstOrNull()
            ?.also { it.isAccessible = true }
            ?.newInstance() as? KRouterRegister

        return register?.get() ?: emptyMap()
    }

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
        val serviceList = servicesMap[className] ?: emptyList()
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
