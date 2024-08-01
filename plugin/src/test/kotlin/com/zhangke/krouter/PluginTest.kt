package com.zhangke.krouter

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar

import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class PluginTest {

    @Test
    fun hello() {
        println("hello world")
    }

    @Test
    fun testCompile() {
        val value = "\$router"
        val code = """
            class TestScreen(val router: String = "test") {
                fun content() {
                    println("TestScreen content: $value")
                }
            }
        """.trimIndent()
        val sourceFile = SourceFile.kotlin(
            name = "TestScreen.kt",
            contents = code,
            trimIndent = false,
        )

        val result = compile(sourceFile)

        val ktClazz = result.classLoader.loadClass("TestScreen")
        val method = ktClazz.declaredMethods.find { it.name == "content" }

        val testScreen = ktClazz.newInstance()
        method?.invoke(testScreen)
    }
}

@OptIn(ExperimentalCompilerApi::class)
fun compile(
    sourceFiles: List<SourceFile>,
    plugin: CompilerPluginRegistrar? = null,
): JvmCompilationResult {
    return KotlinCompilation().apply {
        sources = sourceFiles
        compilerPluginRegistrars = plugin?.let { listOf(it) } ?: emptyList()
        inheritClassPath = false
    }.compile()
}

@OptIn(ExperimentalCompilerApi::class)
fun compile(
    sourceFile: SourceFile,
    plugin: CompilerPluginRegistrar? = null,
): JvmCompilationResult {
    return compile(listOf(sourceFile), plugin)
}