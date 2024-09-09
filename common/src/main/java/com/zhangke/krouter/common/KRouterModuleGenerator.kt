package com.zhangke.krouter.common

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.Destination
import com.zhangke.krouter.KRouterModule
import com.zhangke.krouter.RouteParam
import com.zhangke.krouter.common.utils.findAnnotationValue
import com.zhangke.krouter.common.utils.getRouterParamsNameValue
import com.zhangke.krouter.common.utils.requireAnnotation
import com.zhangke.krouter.internal.KRouterUri

class KRouterModuleGenerator(private val environment: SymbolProcessorEnvironment) {

    fun generateModule(destinations: List<KSClassDeclaration>): String? {
        if (destinations.isEmpty()) return null
        return generateModelFile(destinations)
    }

    private fun generateModelFile(destinations: List<KSClassDeclaration>): String {
        val className = ReflectionContract.generateCollectionFileName()
        val moduleClass = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addSuperinterface(KRouterModule::class)
            .addFunction(buildRouteFunction(destinations))
            .build()
        val fileSpec = FileSpec.builder(
            packageName = ReflectionContract.KROUTER_GENERATED_PACKAGE_NAME,
            fileName = className,
        ).addType(moduleClass)
            .indent("    ")
            .build()
        fileSpec.writeTo(
            codeGenerator = environment.codeGenerator,
            dependencies = Dependencies(true),
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

    private fun buildRoutingStatement(codeBlockBuilder: CodeBlock.Builder, destination: KSClassDeclaration) {
        val route = destination.requireAnnotation<Destination>()
            .findAnnotationValue("route")
            ?.removeSuffix("/")
        if (route.isNullOrEmpty()) {
            val errorMessage = "${destination.qualifiedName!!.asString()}: route is required in @Destination annotation"
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
                    buildAssignmentStatement(
                        paramName = property.simpleName.asString(),
                        type = property.type,
                        routerParamsName = paramName,
                        appendComma = false,
                    )
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
            if (paramsRouteName.isNullOrEmpty()) {
                if (!parameter.hasDefault) {
                    val errorMessage = "$classFullName#${parameter.name?.asString()} must set a default value!"
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
}
