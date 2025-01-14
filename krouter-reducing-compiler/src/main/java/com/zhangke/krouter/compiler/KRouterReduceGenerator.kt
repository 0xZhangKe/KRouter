package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.KRouterModule
import com.zhangke.krouter.common.ReflectionContract
import kotlin.reflect.KClass

class KRouterReduceGenerator(private val environment: SymbolProcessorEnvironment) {

    fun generate(list: List<KSClassDeclaration>, thisModuleClassName: String?) {
        if (list.isEmpty()) return
        val className = ReflectionContract.REDUCING_TARGET_CLASS_NAME
        val moduleClass = TypeSpec.classBuilder(className)
            .primaryConstructor(FunSpec.constructorBuilder().build())
            .addSuperinterface(KRouterModule::class)
            .addProperty(buildModuleList(list, thisModuleClassName))
            .addFunction(buildRouteFunction())
            .addFunction(buildGetServiceFunction())
            .build()
        val fileSpec = FileSpec.builder(
            packageName = ReflectionContract.KROUTER_GENERATED_PACKAGE_NAME,
            fileName = className,
        ).addType(moduleClass)
            .build()
        fileSpec.writeTo(
            codeGenerator = environment.codeGenerator,
            dependencies = Dependencies.ALL_FILES,
        )
    }

    private fun buildModuleList(
        list: List<KSClassDeclaration>,
        thisModuleClassName: String?
    ): PropertySpec {
        val propertyBuilder = PropertySpec.builder(
            name = "moduleList",
            type = List::class.asTypeName().parameterizedBy(KRouterModule::class.asTypeName()),
            modifiers = setOf(KModifier.PRIVATE),
        )
        val moduleList = list.map { it.qualifiedName!!.asString() }.toMutableList()
        if (thisModuleClassName != null) {
            val thisModuleQualifiedName =
                ReflectionContract.KROUTER_GENERATED_PACKAGE_NAME + "." + thisModuleClassName
            moduleList += thisModuleQualifiedName
        }
        propertyBuilder.initializer(
            "listOf<KRouterModule>(\n${moduleList.joinToString(",\n") { "    $it()" }}\n)"
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

    private fun buildGetServiceFunction(): FunSpec {
        val funSpecBuilder = FunSpec.builder("getServices")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("service", KClass::class.asTypeName().parameterizedBy(STAR))
            .returns(
                List::class.asTypeName()
                    .parameterizedBy(KClass::class.asTypeName().parameterizedBy(STAR))
            )
        funSpecBuilder.addStatement(
            "return moduleList.flatMap { it.getServices(service) }"
        )
        return funSpecBuilder.build()
    }
}
