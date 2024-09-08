package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.Destination
import com.zhangke.krouter.KRouterModule
import com.zhangke.krouter.KRouterParams
import com.zhangke.krouter.internal.KRouterUri
import com.zhangke.krouter.common.ReflectionContract
import com.zhangke.krouter.common.utils.findAnnotationValue
import com.zhangke.krouter.common.utils.requireAnnotation

class KRouterModuleGenerator(private val environment: SymbolProcessorEnvironment) {

    fun generateModule(destinations: List<KSClassDeclaration>) {
        if (destinations.isEmpty()) return
        generateModelFile(destinations)
    }

    private fun generateModelFile(destinations: List<KSClassDeclaration>) {
        val className = ReflectionContract.generateCollectionFileName()
        val moduleClass = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addSuperinterface(KRouterModule::class)
            .addFunction(buildRouteFunction(destinations))
            .addFunction(buildMissingParamErrorFunction())
            .build()
        val fileSpec = FileSpec.builder(
            packageName = ReflectionContract.KROUTER_GENERATED_PACKAGE_NAME,
            fileName = className,
        ).addType(moduleClass)
            .build()
        fileSpec.writeTo(
            codeGenerator = environment.codeGenerator,
            dependencies = Dependencies(true),
        )
    }

    private fun buildRouteFunction(destinations: List<KSClassDeclaration>): FunSpec {
        val funSpecBuilder = FunSpec.builder("route")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("uri", String::class)
            .returns(Any::class.asTypeName().copy(true))
        if (destinations.isEmpty()) {
            funSpecBuilder.addStatement("return null")
        } else {
            funSpecBuilder.addStatement("val routerUri = ${KRouterUri::class.qualifiedName}.create(uri)")
                .addStatement("return when(routerUri.baseUrl){")
            destinations.forEach {
                funSpecBuilder.addStatement(buildRoutingStatement(it))
            }
            funSpecBuilder.addStatement("else -> null")
            funSpecBuilder.addStatement("}")
        }
        return funSpecBuilder.build()
    }

    private fun buildRoutingStatement(destination: KSClassDeclaration): String {
        val routerAnnotation = destination.requireAnnotation<Destination>()
        val route = routerAnnotation.findAnnotationValue("route")
        if (route.isNullOrEmpty()) {
            val errorMessage = "${destination.qualifiedName!!.asString()}: route is required in @Destination annotation"
            environment.logger.error(errorMessage)
            throw IllegalArgumentException(errorMessage)
        }
        val classFullName = destination.qualifiedName?.asString()!!
        val statementBuilder = StringBuilder()
        statementBuilder.append("\"$route\" -> {\n")
        val primaryConstructor = destination.primaryConstructor
        val parameters = primaryConstructor?.parameters
        if (primaryConstructor == null || parameters?.isEmpty() == true) {
            statementBuilder.append("$classFullName()")
        } else {

            statementBuilder.append("$classFullName(\n")
            with(statementBuilder) {
                buildConstructorParameters(classFullName, parameters!!)
            }
            statementBuilder.append("\n)")
        }
        statementBuilder.append("\n}")
        return statementBuilder.toString()
    }

    private fun StringBuilder.buildConstructorParameters(classFullName: String, parameters: List<KSValueParameter>) {
        parameters.forEach { parameter ->
            val paramsRouteName = parameter.routerParamName()
            if (paramsRouteName.isNullOrEmpty()) {
                if (!parameter.hasDefault) {
                    val errorMessage = "$classFullName#${parameter.name?.asString()} must set a default value!"
                    environment.logger.error(errorMessage)
                    throw IllegalArgumentException(errorMessage)
                }
            } else {
                val type = parameter.type.resolve().declaration.qualifiedName?.asString()!!
                val paramName = parameter.name!!.asString()
                this.append("$paramName = routerUri.queries[\"$paramsRouteName\"]")
                when (type) {
                    "kotlin.Boolean" -> this.append("?.toBoolean()")
                    "kotlin.Short" -> this.append("?.toShort()")
                    "kotlin.Int" -> this.append("?.toInt()")
                    "kotlin.Long" -> this.append("?.toLong()")
                    "kotlin.Float" -> this.append("?.toFloat()")
                    "kotlin.Double" -> this.append("?.toDouble()")
                    "kotlin.String" -> {}
                    else -> {
                        val errorMessage = "Unsupported type: $type"
                        environment.logger.error(errorMessage)
                        throw IllegalArgumentException(errorMessage)
                    }
                }
                if (parameter.type.resolve().nullability == Nullability.NOT_NULL) {
                    this.append(" ?: throw missingParamError(\"$paramName\", uri)")
                }
                this.append(',')
                this.appendLine()
            }
        }
    }

    private fun KSValueParameter.routerParamName(): String? {
        val routerParamsName = KRouterParams::class.simpleName
        return this.annotations
            .firstOrNull { it.shortName.asString() == routerParamsName }
            ?.arguments
            ?.first { it.name?.asString() == "name" }
            ?.value as? String
    }

    private fun buildMissingParamErrorFunction(): FunSpec {
        return FunSpec.builder("missingParamError")
            .addParameter("paramName", String::class)
            .addParameter("uri", String::class)
            .returns(Exception::class)
            .addModifiers(KModifier.PRIVATE)
            .addStatement("return IllegalArgumentException(\"Missing parameter \$paramName in \$uri\")")
            .build()
    }
}
