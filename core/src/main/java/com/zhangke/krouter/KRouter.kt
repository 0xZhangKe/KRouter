package com.zhangke.krouter

import com.zhangke.krouter.internal.KRouterModuleManager

object KRouter {

    private val moduleManager = KRouterModuleManager()

    fun <T> route(router: String): T? {
        return moduleManager.route(router)
    }

    fun addRouterModule(module: KRouterModule) {
        moduleManager.addRouterModule(module)
    }
}
