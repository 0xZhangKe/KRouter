package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

private const val TestScreen = """
package com.zhangke.krouter.test

import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Param

interface Screen
interface TabScreen

@Destination("screen/test", "screen/test2")
data class TestScreen(
    val title: String,
    val name: String = ""
) : Screen

@Destination("screen/main")
class MainScreen(
    val number: Int = 0
) : Screen

@Destination("screen/settings")
class SettingsScreen(
    @Param(required = true)
    val index: Int? = 0,
    val count: Int = 10,
    val price: Int
) : TabScreen
"""

@OptIn(ExperimentalCompilerApi::class)
class KRouterInjectProcessorTest {

    @Test
    fun test() {
        val kotlinSource = listOf(
            SourceFile.kotlin("Screens.kt", TestScreen),
        )

        val result = compile(
            sourceFiles = kotlinSource,
            kspProcessors = listOf(KRouterProcessorProvider())
        )
    }

    @OptIn(ExperimentalCompilerApi::class)
    fun compile(
        sourceFiles: List<SourceFile>,
        kspProcessors: List<SymbolProcessorProvider>
    ): JvmCompilationResult {
        return KotlinCompilation().apply {
            sources = sourceFiles
            inheritClassPath = true

            configureKsp(true) {
                processorOptions["kRouterType"] = "inject"

                symbolProcessorProviders.apply {
                    clear()
                    addAll(kspProcessors)
                }
            }
        }.compile()
    }
}

