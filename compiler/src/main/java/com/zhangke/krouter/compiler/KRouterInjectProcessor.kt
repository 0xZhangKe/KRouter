package com.zhangke.krouter.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.compiler.code.buildGetRouterMapFunc
import com.zhangke.krouter.compiler.code.buildHandleParamsFunction
import com.zhangke.krouter.compiler.code.buildParamStateClass
import com.zhangke.krouter.compiler.ext.asClassDeclaration

/**
 * 真正实现路由注入的处理器，继承自KRouterCollectProcessor
 * 收集完所在模块后才会执行注入操作
 */
class KRouterInjectProcessor(
    environment: SymbolProcessorEnvironment
) : KRouterCollectProcessor(environment) {

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val resultList = super.process(resolver)

        // 若存在生成的文件，则说明收集到了路由信息，还会触发一次process，此时可跳过注入操作
        if (environment.codeGenerator.generatedFile.isNotEmpty()) {
            return resultList
        }

        val generatedItems = resolver.getDeclarationsFromPackage(GENERATED_SHARED_PACKAGE)
        val propertiesItems = generatedItems
            .mapNotNull { (it as? KSClassDeclaration)?.getDeclaredProperties() }
            .flatten()

        val collectedMap = propertiesItems
            .map { it.type.resolve().declaration.asClassDeclaration() }
            .toList()

        writeToFile(environment.codeGenerator, collectedMap)

        return resultList
    }

    private fun writeToFile(
        codeGenerator: CodeGenerator,
        collectedMap: List<KSClassDeclaration>
    ) {
        if (collectedMap.isEmpty()) return

        val className = "KRouterInjectMap"
        val classSpec = TypeSpec.objectBuilder(className)
            .addKdoc(CLASS_KDOC)
            .addFunction(buildGetRouterMapFunc(collectedMap))
            .addType(buildParamStateClass())
            .addFunction(buildHandleParamsFunction())
            .build()

        val fileSpec = FileSpec.builder(GENERATED_SHARED_PACKAGE, className)
            .addType(classSpec)
            .indent("    ")
            .build()

        // 将涉及到的类所涉及的文件作为依赖传入，方便增量编译
        val dependencies = collectedMap
            .mapNotNull { it.containingFile }
            .distinct()
            .toTypedArray()

        kotlin.runCatching {
            fileSpec.writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies(aggregating = true, *dependencies)
            )
        }
    }
}