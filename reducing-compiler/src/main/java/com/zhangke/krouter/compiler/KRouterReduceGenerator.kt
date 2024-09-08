package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.KRouterModule
import com.zhangke.krouter.common.ReflectionContract

class KRouterReduceGenerator(private val environment: SymbolProcessorEnvironment) {

    fun generate(list: List<KSClassDeclaration>) {
        if (list.isEmpty()) return
        val className = ReflectionContract.REDUCING_TARGET_CLASS_NAME
        val moduleClass = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addSuperinterface(KRouterModule::class)
            .addProperty(buildModuleList(list))
            .addFunction(buildRouteFunction())
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

    private fun buildModuleList(list: List<KSClassDeclaration>): PropertySpec {
        val moduleListPropertyName = "moduleList"
        val propertyBuilder = PropertySpec.builder(
            name = moduleListPropertyName,
            type = List::class.asTypeName().parameterizedBy(KRouterModule::class.asTypeName()),
            modifiers = setOf(KModifier.PRIVATE),
        )
        propertyBuilder.initializer(
            "listOf<KRouterModule>(\n${list.joinToString(",\n") { "    ${it.qualifiedName!!.asString()}()" }}\n)"
        )
        return propertyBuilder.build()
    }

    private fun buildRouteFunction(): FunSpec {
        val funSpecBuilder = FunSpec.builder("route")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("uri", String::class)
            .returns(Any::class.asTypeName().copy(true))
        funSpecBuilder.addStatement(
            "return moduleList.firstNotNullOfOrNull { it.route(uri) }"
        )
        return funSpecBuilder.build()
    }
}
