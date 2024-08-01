package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class KRouterProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val processorType = environment.options["kRouterType"]

        if (processorType == null) {
            environment.logger.warn("kRouterType is null, please set kRouterType.")
        }

        return when (processorType) {
            "inject" -> KRouterInjectProcessor(environment)
            "collect" -> KRouterCollectProcessor(environment)
            else -> KRouterCollectProcessor(environment)
        }
    }
}