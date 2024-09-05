package com.zhangke.krouter

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
//@AutoService(CommandLineProcessor::class)
class KRouterMappingCommandLineProcessor: CommandLineProcessor {

    override val pluginId: String
        get() = "krouter"

    override val pluginOptions: Collection<AbstractCliOption>
        get() = listOf(
//            CliOption(
//                optionName = "RouterMapping",
//                valueDescription = "make route to class mapping",
//                "make route to class mapping",
//            ),
        )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        super.processOption(option, value, configuration)
    }
}
