package com.zhangke.krouter.utils

import java.io.IOException
import java.net.URL
import java.util.Enumeration
import kotlin.reflect.KClass

internal object ServiceLoaderUtils {

    private val clazzMap: MutableMap<String, List<KClass<*>>> = mutableMapOf()
    private val availableClassesMap = mutableMapOf<String, List<String>?>()

    internal fun getServicesClasses(className: String): List<KClass<*>> {
        val targetList = getAvailableServices(className)
        return clazzMap.getOrPut(className) {
            targetList.mapNotNull {
                val item = runCatching {
                    Class.forName(it).let { UnsafeAllocator.INSTANCE.newInstance(it) }
                }.getOrElse {
                    it.printStackTrace()
                    null
                } ?: return@mapNotNull null

                item::class
            }
        }
    }

    private fun getAvailableServices(className: String): List<String> {
        return availableClassesMap.getOrPut(className) {
            load(className)?.toList()
                ?.map {
                    it.readText()
                        .split('\n')
                        .filter(String::isNotBlank)
                        .map { it.trim() }
                }?.flatten()
                ?.distinct()
        } ?: emptyList()
    }


    /**
     * load service from classpath
     */
    private fun load(className: String): Enumeration<URL>? = try {
        val target = "META-INF/services/$className"

        when (val loader = Thread.currentThread().contextClassLoader) {
            null -> ClassLoader.getSystemResources(target)
            else -> loader.getResources(target)
        }
    } catch (e: IOException) {
        null
    }
}
