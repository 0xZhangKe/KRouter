package com.zhangke.krouter

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration

class KRouterLogger(private val configuration: CompilerConfiguration) {

    private val logger = configuration.get(
        CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        MessageCollector.NONE,
    )

    fun i(message: String) {
        logger.report(CompilerMessageSeverity.INFO, message)
        logger.report(CompilerMessageSeverity.ERROR, message)
        logger.report(CompilerMessageSeverity.EXCEPTION, message)
        logger.report(CompilerMessageSeverity.ERROR, message)
    }

    fun w(message: String) {
        logger.report(CompilerMessageSeverity.WARNING, message)
        logger.report(CompilerMessageSeverity.ERROR, message)
        logger.report(CompilerMessageSeverity.EXCEPTION, message)
        logger.report(CompilerMessageSeverity.ERROR, message)
    }
}