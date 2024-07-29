package com.zhangke.krouter

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Param
import com.zhangke.krouter.annotation.Router
import com.zhangke.krouter.utils.baseUri
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

/**
 * Just for KRouter internal usage.
 * Public for inline function.
 */
internal object ZZZKRouterInternalUtil {

    private val intKType = Int::class.createType()
    private val longKType = Long::class.createType()
    private val floatKType = Float::class.createType()
    private val doubleKType = Double::class.createType()
    private val booleanKType = Boolean::class.createType()
    private val stringKType = String::class.createType()

    fun findServiceByRouter(
        serviceClassList: List<KClass<*>>,
        router: String,
    ): KClass<*>? {
        val routerUri = URI.create(router).baseUri
        val service = serviceClassList.firstOrNull {
            getRouterFromClassAnnotation(it)
                .map { route -> URI.create(route).baseUri }
                .contains(routerUri)
        }
        return service
    }

    fun getFilledRouterService(
        routeRequest: RouteRequest,
        serviceClass: KClass<*>,
    ): Any {
        // new instance and fill with routeRequests
        val service = fillServiceByConstructor(routeRequest, serviceClass)

        // fill instance properties with routeRequests
        fillServiceProperty(service, routeRequest, serviceClass)

        // fill router property while router is not null
        // TODO remove it when @Router is removed
        routeRequest.stringParams?.get("router")?.let {
            fillRouterByProperty(
                router = it,
                service = service,
                serviceClass = serviceClass
            )
        }

        // return instance
        return service
    }

    private fun fillServiceByConstructor(
        routeRequest: RouteRequest,
        serviceClass: KClass<*>,
    ): Any {
        val primaryConstructor = serviceClass.primaryConstructor
            ?: throw IllegalArgumentException("KRouter Destination class must have a Primary-Constructor!")

        val map = primaryConstructor.parameters.mapNotNull { parameter ->
            val param = parameter.findAnnotation<Param>()
            val name = param?.name?.takeIf(String::isNotBlank) ?: parameter.name

            val value = routeRequest.getValueForParameter(
                parameter = parameter,
                getName = { name }
            )

            // check parameter is required
            if (param?.required == true && value == null) {
                throw IllegalArgumentException("@Router parameter(${name}: ${parameter.type}) is required!")
            }

            // skip optional parameter when value is null
            if (parameter.isOptional && value == null) return@mapNotNull null

            // check parameter is not null
            if (!parameter.type.isMarkedNullable && value == null)
                throw IllegalArgumentException("@Router parameter(${name}: ${parameter.type}) must not be null!")

            parameter to value
        }.toMap()

        // create new instance
        return primaryConstructor.callBy(map)
    }

    private fun fillServiceProperty(
        service: Any,
        routeRequest: RouteRequest,
        serviceClass: KClass<*>,
    ) {
        serviceClass.findRouterProperties().forEach { (property, param) ->
            if (property !is KMutableProperty<*>) throw IllegalArgumentException("@Router property must be non-final!")
            if (property.visibility != KVisibility.PUBLIC) throw IllegalArgumentException("@Router property must be public!")

            val setter = property.setter
            val propertyType = setter.parameters[1]
            val name = param.name.takeIf(String::isNotBlank) ?: property.name

            // get value from RouteRequest
            val value = routeRequest.getValueForParameter(
                parameter = propertyType,
                getName = { name }
            )

            // check parameter is required, if it is required while value is null, throw exception
            if (param.required && value == null) {
                throw IllegalArgumentException("@Router property($name: ${propertyType.type}) is required!")
            }

            property.setter.call(service, value)
        }
    }

    private fun RouteRequest.getValueForParameter(
        parameter: KParameter,
        getName: KParameter.() -> String? = { name }
    ): Any? {
        val name = parameter.getName()
            ?: throw IllegalArgumentException("No available name for parameter $parameter")

        return when (parameter.type) {
            intKType -> intParams?.get(name)
            longKType -> longParams?.get(name)
            floatKType -> floatParams?.get(name)
            doubleKType -> doubleParams?.get(name)
            booleanKType -> booleanParams?.get(name)
            stringKType -> stringParams?.get(name)
            else -> objParams?.get(name)
        }
    }

    private fun getRouterFromClassAnnotation(targetClass: KClass<*>): Array<String> {
        val routerAnnotation = targetClass.findAnnotation<Destination>() ?: return emptyArray()
        return arrayOf(*routerAnnotation.router)
    }

    private fun KClass<*>.findRouterProperties(): List<Pair<KProperty<*>, Param>> {
        return declaredMemberProperties.mapNotNull {
            it to (it.findAnnotation<Param>() ?: return@mapNotNull null)
        }
    }

    @Deprecated("Not useful")
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

    @Deprecated("Not useful")
    private fun KClass<*>.findRouterProperty(): KProperty<*>? {
        return declaredMemberProperties.firstOrNull { property ->
            val isRouterProperty = property.findAnnotation<Router>() != null
            isRouterProperty
        }
    }

    @Deprecated("Not useful")
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

    @Deprecated("Not useful")
    private fun errorRouterParameterType(parameter: KParameter) {
        throw IllegalArgumentException(
            "@Router property(${parameter.name}: ${parameter.type}) type must be String!"
        )
    }
}
