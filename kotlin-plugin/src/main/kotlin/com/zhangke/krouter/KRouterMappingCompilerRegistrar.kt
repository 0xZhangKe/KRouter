package com.zhangke.krouter

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
//@AutoService(CompilerPluginRegistrar::class)
class KRouterMappingCompilerRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean
        get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val logger = KRouterLogger(configuration)
        logger.i("-----------------")
        logger.i("KRouterMappingCompilerRegistrar")
        logger.i("-----------------")

        IrGenerationExtension.registerExtension(KRouterMappingIrGenerationExtension(logger))
    }
}

//class KRouterMappingFirCompilerRegistrar: FirExtensionRegistrar() {
//
//    override fun ExtensionRegistrarContext.configurePlugin() {
//        +::KRouterMappingFirCompilerCheckers
//    }
//}
//
//class KRouterMappingFirCompilerCheckers(session: FirSession) : FirAdditionalCheckersExtension(session) {
//    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
//        override val propertyCheckers: Set<FirPropertyChecker>
//            get() = setOf(AtomicfuPropertyChecker)
//    }
//}
