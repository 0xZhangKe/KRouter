package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.zhangke.krouter.annotation.Destination

/**
 * 专门用于收集路由信息的处理器
 */
class KRouterCollectProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private fun log(message: String, symbol: KSNode? = null) =
        environment.logger.warn(message, symbol)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .toList()

        return emptyList()
    }
}