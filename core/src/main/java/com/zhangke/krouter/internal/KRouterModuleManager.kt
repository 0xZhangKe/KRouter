package com.zhangke.krouter.internal

import com.zhangke.krouter.KRouterModule
import java.util.concurrent.CopyOnWriteArrayList

class KRouterModuleManager {

    companion object {

        const val KROUTER_GENERATED_PACKAGE_NAME = "com.zhangke.krouter.generated"
        const val REDUCING_TARGET_CLASS_NAME = "AutoReducingModule"
        const val AUTO_REDUCE_MODULE_CLASS_NAME = "$KROUTER_GENERATED_PACKAGE_NAME.$REDUCING_TARGET_CLASS_NAME"
    }

    private val defaultModel: KRouterModule by lazy {
        reflectDefaultModule()
    }
    private val dynamicRouteModules = CopyOnWriteArrayList<KRouterModule>()

    @Suppress("UNCHECKED_CAST")
    fun <T> route(route: String): T? {
        dynamicRouteModules.firstNotNullOfOrNull { it.route(route) }?.let { return it as T }
        defaultModel.route(route)?.let { return it as T }
        return null
    }

    fun addRouterModule(module: KRouterModule) {
        dynamicRouteModules += module
    }

    private fun reflectDefaultModule(): KRouterModule {
        val className = "${KROUTER_GENERATED_PACKAGE_NAME}.${REDUCING_TARGET_CLASS_NAME}"
        return Class.forName(className).constructors.first().newInstance() as KRouterModule
    }
}
