package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Service
import com.zhangke.krouter.common.KRouterModuleGenerator

class CollectingProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CollectingProcessor(environment)
    }
}

class CollectingProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val moduleGenerator = KRouterModuleGenerator(environment)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val destinations = resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .toList()

        val services = resolver.getSymbolsWithAnnotation(Service::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        moduleGenerator.generateModule(destinations, services)
        return emptyList()
    }
}
