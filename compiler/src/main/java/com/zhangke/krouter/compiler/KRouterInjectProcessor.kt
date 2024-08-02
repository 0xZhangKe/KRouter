package com.zhangke.krouter.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated

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

        // TODO 待完善将收集到的路由信息注入的逻辑
        val generatedItems = resolver.getDeclarationsFromPackage(GENERATED_SHARED_PACKAGE)
        generatedItems.forEachIndexed { index, item ->
            val clazz = kotlin.runCatching { item.asClassDeclaration() }.getOrNull()
            log("[$index]generatedItem: $item $clazz ${clazz?.classKind}")

            clazz?.getDeclaredProperties()?.forEach { property ->
                val propertyName = property.simpleName.asString()
                val propertyClazz = property.type.resolve().declaration.asClassDeclaration()

                log("[$index]propertyName: $propertyName")
            }
        }

        return resultList
    }
}