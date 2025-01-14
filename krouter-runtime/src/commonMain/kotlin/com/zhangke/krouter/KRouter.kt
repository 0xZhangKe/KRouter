package com.zhangke.krouter

import com.zhangke.krouter.internal.KRouterModuleManager

object KRouter {

    val moduleManager = KRouterModuleManager()

    fun <T> route(router: String): T? {
        return moduleManager.route(router)
    }

    inline fun <reified T> getServices(): List<T> {
        @Suppress("UNCHECKED_CAST")
        return moduleManager.getServices(T::class) as List<T>
    }

    fun addRouterModule(module: KRouterModule) {
        moduleManager.addRouterModule(module)
    }
}
