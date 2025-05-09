package com.zhangke.krouter.common

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.KRouterModule
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.NoImplementationService
import com.zhangke.krouter.annotation.RouteParam
import com.zhangke.krouter.annotation.RouteUri
import com.zhangke.krouter.annotation.Service
import com.zhangke.krouter.common.utils.findAnnotationValue
import com.zhangke.krouter.common.utils.getRouterParamsNameValue
import com.zhangke.krouter.common.utils.requireAnnotation
import com.zhangke.krouter.internal.KRouterUri
import kotlin.math.abs
import kotlin.reflect.KClass

class KRouterModuleGenerator(private val environment: SymbolProcessorEnvironment) {

    fun generateModule(
        destinations: List<KSClassDeclaration>,
        services: List<KSClassDeclaration>,
    ): String? {
        if (destinations.isEmpty() && services.isEmpty()) return null
        return generateModelFile(destinations, services)
    }

    private fun generateModelFile(
        destinations: List<KSClassDeclaration>,
        services: List<KSClassDeclaration>,
    ): String {
        val sortedNames = (destinations + services).map { it.qualifiedName!!.asString() }.sorted()
        val fileNameIdentity = sortedNames
            .hashCode()
            .let { abs(it) }
            .toString(16)
        val className = ReflectionContract.generateCollectionFileName(name = fileNameIdentity)
        val moduleClass = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addSuperinterface(KRouterModule::class)
            .addFunction(buildRouteFunction(destinations))
            .addFunction(buildServiceFunction(services))
            .build()
        val fileSpec = FileSpec.builder(
            packageName = ReflectionContract.KROUTER_GENERATED_PACKAGE_NAME,
            fileName = className,
        ).addType(moduleClass)
            .indent("    ")
            .build()
        fileSpec.writeTo(
            codeGenerator = environment.codeGenerator,
            dependencies = Dependencies.ALL_FILES,
        )
        return className
    }

    private fun buildRouteFunction(destinations: List<KSClassDeclaration>): FunSpec {
        val funSpecBuilder = FunSpec.builder("route")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("uri", String::class)
            .returns(Any::class.asTypeName().copy(true))
        if (destinations.isEmpty()) {
            funSpecBuilder.addStatement("return null")
        } else {
            val codeBlockBuilder = CodeBlock.builder()
            codeBlockBuilder.addStatement("val routerUri = ${KRouterUri::class.qualifiedName}.create(uri)")
            codeBlockBuilder.addStatement("return when (routerUri.baseUrl) {")
            codeBlockBuilder.indent()
            destinations.forEach {
                buildRoutingStatement(codeBlockBuilder, it)
            }
            codeBlockBuilder.addStatement("else -> null")
            codeBlockBuilder.unindent()
            codeBlockBuilder.addStatement("}")
            funSpecBuilder.addCode(codeBlockBuilder.build())
        }
        return funSpecBuilder.build()
    }

    private fun buildServiceFunction(services: List<KSClassDeclaration>): FunSpec {
        val kClassType = KClass::class.asTypeName().parameterizedBy(TypeVariableName("*"))
        val funSpecBuilder = FunSpec.builder("getServices")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("service", kClassType)
            .returns(List::class.asTypeName().parameterizedBy(Any::class.asTypeName()))
        funSpecBuilder.addStatement(
            "if (service == Any::class) throw IllegalArgumentException(\"service can not be Any\")"
        )
        if (services.isEmpty()) {
            funSpecBuilder.addStatement("return emptyList()")
        } else {
            val codeBlockBuilder = CodeBlock.builder()
            codeBlockBuilder.addStatement("return when (service) {")
            codeBlockBuilder.indent()
            services.groupBy { it.getServiceName() }
                .forEach { (serviceName, serviceList) ->
                    codeBlockBuilder.addStatement("${serviceName}::class -> listOf(")
                    codeBlockBuilder.indent()
                    serviceList.forEach { service ->
                        codeBlockBuilder.addStatement("${service.qualifiedName!!.asString()}(),")
                    }
                    codeBlockBuilder.unindent()
                    codeBlockBuilder.addStatement(")")
                    codeBlockBuilder.addStatement("\n")
                }
            codeBlockBuilder.addStatement("else -> emptyList<Any>()")
            codeBlockBuilder.unindent()
            codeBlockBuilder.addStatement("}")
            funSpecBuilder.addCode(codeBlockBuilder.build())
        }
        return funSpecBuilder.build()
    }

    private fun buildRoutingStatement(
        codeBlockBuilder: CodeBlock.Builder,
        destination: KSClassDeclaration
    ) {
        val route = destination.requireAnnotation<Destination>()
            .findAnnotationValue("route")
            ?.removeSuffix("/")
        if (route.isNullOrEmpty()) {
            val errorMessage =
                "${destination.qualifiedName!!.asString()}: route is required in @Destination annotation"
            environment.logger.error(errorMessage)
            throw IllegalArgumentException(errorMessage)
        }
        val classFullName = destination.qualifiedName?.asString()!!
        codeBlockBuilder.add("\"$route\" -> {\n")
        val primaryConstructor = destination.primaryConstructor
        val parameters = primaryConstructor?.parameters
        if (primaryConstructor == null || parameters.isNullOrEmpty()) {
            codeBlockBuilder.indent()
            codeBlockBuilder.add("$classFullName()")
            codeBlockBuilder.unindent()
        } else {
            codeBlockBuilder.indent()
            codeBlockBuilder.add("$classFullName(\n")
            with(codeBlockBuilder) {
                buildConstructorParameters(classFullName, parameters)
            }
            codeBlockBuilder.add(")")
            codeBlockBuilder.unindent()
        }
        val processingProperties = destination.getAllProperties()
            .filter { it.hasBackingField }
            .mapNotNull {
                val paramName = it.getRouterParamsNameValue()
                if (paramName.isNullOrEmpty()) {
                    null
                } else {
                    paramName to it
                }
            }.toList()
        if (processingProperties.isNotEmpty()) {
            codeBlockBuilder.add(".apply {\n")
            codeBlockBuilder.indent()
            processingProperties.forEach { (paramName, property) ->
                codeBlockBuilder.indent()
                with(codeBlockBuilder) {
                    if (property.isRouterUriField()) {
                        addStatement("\n${property.simpleName.asString()} = uri")
                    } else {
                        buildAssignmentStatement(
                            paramName = property.simpleName.asString(),
                            type = property.type,
                            routerParamsName = paramName,
                            appendComma = false,
                        )
                    }
                }
                codeBlockBuilder.unindent()
            }
            codeBlockBuilder.add("}")
            codeBlockBuilder.unindent()
        }
        codeBlockBuilder.add("\n}\n\n")
    }

    private fun CodeBlock.Builder.buildConstructorParameters(
        classFullName: String,
        parameters: List<KSValueParameter>
    ) {
        this.indent()
        parameters.forEach { parameter ->
            val paramsRouteName = parameter.routerParamName()
            if (parameter.isRouterUriField()) {
                addStatement("\n${parameter.name!!.asString()} = uri,")
            } else if (paramsRouteName.isNullOrEmpty()) {
                if (!parameter.hasDefault) {
                    val errorMessage =
                        "$classFullName#${parameter.name?.asString()} must set a default value!"
                    environment.logger.error(errorMessage)
                    throw IllegalArgumentException(errorMessage)
                }
            } else {
                buildAssignmentStatement(
                    paramName = parameter.name!!.asString(),
                    type = parameter.type,
                    routerParamsName = paramsRouteName,
                    appendComma = true,
                )
            }
        }
        this.unindent()
    }

    private fun CodeBlock.Builder.buildAssignmentStatement(
        paramName: String,
        type: KSTypeReference,
        routerParamsName: String,
        appendComma: Boolean,
    ) {
        val ksType = type.resolve()
        val suffix = if (appendComma) "," else ""
        if (ksType.nullability == Nullability.NOT_NULL) {
            addStatement("$paramName = routerUri.requireQuery(\"$routerParamsName\")$suffix")
        } else {
            addStatement("$paramName = routerUri.getQuery(\"$routerParamsName\")$suffix")
        }
    }

    private fun KSValueParameter.routerParamName(): String? {
        val routerParamsName = RouteParam::class.simpleName
        return this.annotations
            .firstOrNull { it.shortName.asString() == routerParamsName }
            ?.arguments
            ?.first { it.name?.asString() == "name" }
            ?.value as? String
    }

    private fun KSValueParameter.isRouterUriField(): Boolean {
        val routerUriName = RouteUri::class.simpleName
        return this.annotations.any { it.shortName.asString() == routerUriName }
    }

    private fun KSPropertyDeclaration.isRouterUriField(): Boolean {
        val routerUriName = RouteUri::class.simpleName
        return this.annotations.any { it.shortName.asString() == routerUriName }
    }

    private fun KSClassDeclaration.getServiceName(): String {
        val serviceName = Service::class.simpleName
        val serviceInArgument = this.annotations.first { it.shortName.asString() == serviceName }
            .arguments.firstOrNull { it.name?.asString() == "service" }
            ?.value as? KSType
        if (serviceInArgument == Any::class) {
            throw IllegalArgumentException("service($serviceName) can not be Any")
        }
        val invalidName = NoImplementationService::class.qualifiedName
        val serviceInArgumentName = serviceInArgument?.toClassName()?.canonicalName
        if (serviceInArgument != null && invalidName != serviceInArgumentName) {
            return serviceInArgument.toClassName().canonicalName
        }
        val superTypeList = this.superTypes.toList()
        if (superTypeList.size != 1) {
            throw IllegalArgumentException("service($serviceName) super type not found")
        }
        return superTypeList.first().resolve().toClassName().canonicalName
    }
}
