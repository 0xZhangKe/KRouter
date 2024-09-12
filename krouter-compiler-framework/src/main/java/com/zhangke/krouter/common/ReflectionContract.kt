package com.zhangke.krouter.common

import com.zhangke.krouter.internal.KRouterModuleManager

object ReflectionContract {

    const val KROUTER_GENERATED_PACKAGE_NAME = KRouterModuleManager.KROUTER_GENERATED_PACKAGE_NAME
    const val REDUCING_TARGET_CLASS_NAME = KRouterModuleManager.REDUCING_TARGET_CLASS_NAME
    private const val COLLECTION_CLASS_NAME_PREFIX = "RouterCollection_"

    const val AUTO_REDUCE_MODULE_CLASS_NAME = KRouterModuleManager.AUTO_REDUCE_MODULE_CLASS_NAME

    fun generateCollectionFileName(): String {
        // waiting 1 ms to avoid the same file name
        runCatching { Thread.sleep(1) }
        return "$COLLECTION_CLASS_NAME_PREFIX${System.currentTimeMillis()}"
    }

    fun isCollectionClass(name: String): Boolean {
        return name.startsWith("$KROUTER_GENERATED_PACKAGE_NAME.$COLLECTION_CLASS_NAME_PREFIX")
    }
}