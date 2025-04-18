package com.zhangke.krouter.internal

import com.zhangke.krouter.KRouterModule
import kotlin.reflect.KClass

class KRouterModuleManager {

    companion object {

        const val KROUTER_GENERATED_PACKAGE_NAME = "com.zhangke.krouter.generated"
        const val REDUCING_TARGET_CLASS_NAME = "AutoReducingModule"
        const val AUTO_REDUCE_MODULE_CLASS_NAME =
            "$KROUTER_GENERATED_PACKAGE_NAME.$REDUCING_TARGET_CLASS_NAME"
    }

    private val defaultModel: KRouterModule? by lazy {
        reflectDefaultModule()
    }
    private val dynamicRouteModules = mutableListOf<KRouterModule>()

    @Suppress("UNCHECKED_CAST")
    fun <T> route(route: String): T? {
        dynamicRouteModules.firstNotNullOfOrNull { it.route(route) }?.let { return it as T }
        defaultModel?.route(route)?.let { return it as T }
        return null
    }

    fun getServices(service: KClass<*>): List<Any> {
        val services = mutableListOf<Any>()
        for (module in dynamicRouteModules) {
            module.getServices(service)
        }
        services.addAll(dynamicRouteModules.flatMap { it.getServices(service) })
        defaultModel?.getServices(service)?.let { services.addAll(it) }
        return services
    }

    fun addRouterModule(module: KRouterModule) {
        dynamicRouteModules += module
    }

    private fun reflectDefaultModule(): KRouterModule? {
        val className = "${KROUTER_GENERATED_PACKAGE_NAME}.${REDUCING_TARGET_CLASS_NAME}"
        return KRouterReflection().createObjectFromName(className) as? KRouterModule
    }
}
