package com.zhangke.krouter

import java.net.URI
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * Just for KRouter internal usage.
 * Public for inline function.
 */
object ZZZKRouterInternalUtil {

    private val stringKType = String::class.createType()

    fun findServiceByRouter(
        serviceClassList: List<Any>,
        router: String,
    ): Any? {
        val routerUri = URI.create(router).baseUri
        val service = serviceClassList.firstOrNull {
            val serviceRouter = getRouterFromClassAnnotation(it::class)
            if (serviceRouter.isNullOrEmpty().not()) {
                val serviceUri = URI.create(serviceRouter!!).baseUri
                serviceUri == routerUri
            } else {
                false
            }
        }
        return service
    }

    private fun getRouterFromClassAnnotation(targetClass: KClass<*>): String? {
        val routerAnnotation = targetClass.findAnnotation<Destination>() ?: return null
        return routerAnnotation.router
    }

    fun getFilledRouterService(router: String, service: Any): Any {
        val serviceClass = service::class
        fillRouterByConstructor(router, serviceClass)?.let { return it }
        fillRouterByProperty(router, service, serviceClass)?.let { return it }
        return service
    }

    private fun fillRouterByConstructor(router: String, serviceClass: KClass<*>): Any? {
        val primaryConstructor = serviceClass.primaryConstructor
            ?: throw IllegalArgumentException("KRouter Destination class must have a Primary-Constructor!")
        val routerParameter = primaryConstructor.parameters.firstOrNull { parameter ->
            parameter.findAnnotation<Router>() != null
        } ?: return null
        if (routerParameter.type != stringKType) errorRouterParameterType(routerParameter)
        return primaryConstructor.callBy(mapOf(routerParameter to router))
    }

    private fun fillRouterByProperty(
        router: String,
        service: Any,
        serviceClass: KClass<*>,
    ): Any? {
        val routerProperty = serviceClass.findRouterProperty() ?: return null
        fillRouterToServiceProperty(
            router = router,
            service = service,
            property = routerProperty,
        )
        return service
    }

    private fun KClass<*>.findRouterProperty(): KProperty<*>? {
        return declaredMemberProperties.firstOrNull { property ->
            val isRouterProperty = property.findAnnotation<Router>() != null
            isRouterProperty
        }
    }

    private fun fillRouterToServiceProperty(
        router: String,
        service: Any,
        property: KProperty<*>,
    ) {
        if (property !is KMutableProperty<*>) throw IllegalArgumentException("@Router property must be non-final!")
        if (property.visibility != KVisibility.PUBLIC) throw IllegalArgumentException("@Router property must be public!")
        val setter = property.setter
        val propertyType = setter.parameters[1]
        if (propertyType.type != stringKType) errorRouterParameterType(propertyType)
        property.setter.call(service, router)
    }

    private fun errorRouterParameterType(parameter: KParameter) {
        throw IllegalArgumentException(
            "@Router property(${parameter.name}: ${parameter.type}) type must be String!"
        )
    }
}
