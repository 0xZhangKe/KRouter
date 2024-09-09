package com.zhangke.krouter.compiler

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.zhangke.krouter.Destination
import com.zhangke.krouter.common.KRouterModuleGenerator
import com.zhangke.krouter.common.ReflectionContract


class ReducingProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ReducingProcessor(environment)
    }
}

class ReducingProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val reduceGenerator = KRouterReduceGenerator(environment)
    private val moduleGenerator = KRouterModuleGenerator(environment)

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val autoModelClass = ReflectionContract.AUTO_REDUCE_MODULE_CLASS_NAME
        if (resolver.getClassDeclarationByName(autoModelClass) != null) return emptyList()
        val moduleList = resolver.getDeclarationsFromPackage(ReflectionContract.KROUTER_GENERATED_PACKAGE_NAME)
            .mapNotNull { it as? KSClassDeclaration }
            .filter { ReflectionContract.isCollectionClass(it.qualifiedName?.asString().orEmpty()) }
            .toList()

        val destinations = resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .toList()

        val thisModuleClassName = moduleGenerator.generateModule(destinations)

        reduceGenerator.generate(moduleList, thisModuleClassName)
        return emptyList()
    }
}
