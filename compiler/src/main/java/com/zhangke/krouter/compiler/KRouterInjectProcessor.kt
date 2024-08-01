package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode

/**
 * 真正实现路由注入的处理器
 */
class KRouterInjectProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private fun log(message: String, symbol: KSNode? = null) =
        environment.logger.warn(message, symbol)

    override fun process(resolver: Resolver): List<KSAnnotated> {

        return emptyList()
    }
}