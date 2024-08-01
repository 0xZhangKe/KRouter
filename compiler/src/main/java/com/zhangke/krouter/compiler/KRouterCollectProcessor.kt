package com.zhangke.krouter.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import com.zhangke.krouter.annotation.Destination
import kotlin.math.absoluteValue
import kotlin.random.Random

/**
 * 专门用于收集路由信息的处理器
 */
open class KRouterCollectProcessor(
    protected val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    protected fun log(message: String, symbol: KSNode? = null) =
        environment.logger.warn(message, symbol)

    companion object {
        private val badTypeName = Unit::class.qualifiedName
        private val badSuperTypeName = Any::class.qualifiedName
        const val GENERATED_SHARED_PACKAGE = "com.zhangke.krouter.generated"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val destinations = resolver.getSymbolsWithAnnotation(Destination::class.qualifiedName!!)
            .map { it as KSClassDeclaration }
            .toList()

        val collectedMap = destinations.map { collect(it) }
            .groupBy({ it.first }, { it.second })

        writeToFile(environment.codeGenerator, collectedMap)

        return emptyList()
    }

    private fun collect(targetClass: KSClassDeclaration): Pair<String, String> {
        val className = targetClass.qualifiedName?.asString().orEmpty()
        val routerAnnotation = targetClass.requireAnnotation<Destination>()

        // 首先看注解是否有自定义的目标父类
        val targetSuperClassName = routerAnnotation.findArgumentTypeByName("type")

        // 若获取到自定义的目标父类，则检查该类是否被目标类继承
        if (targetSuperClassName != null && targetSuperClassName != badTypeName) {
            checkSuperClassExist(targetClass, targetSuperClassName)

            return targetSuperClassName to className
        }

        // 若没有自定义的目标父类，则判断目标类是否有且只有一个父类，若有则使用该父类作为目标父类
        val superTypeName = targetClass.superTypes
            .takeIf { it.isSingleElement() }
            ?.firstOrNull()
            ?.typeQualifiedName
            ?.takeIf(String::isNotBlank)
            ?.takeIf { it != badSuperTypeName }

        if (superTypeName == null) {
            throw IllegalArgumentException("${targetClass.qualifiedName} must have a super class")
        }

        return superTypeName to className
    }

    private fun checkSuperClassExist(
        targetClass: KSClassDeclaration,
        targetClassName: String
    ) {
        if (targetClassName == badTypeName || targetClassName == badSuperTypeName) {
            throw IllegalArgumentException("[checkSuperClassExist]: $targetClassName is not valid")
        }

        if (!targetClass.superTypes.iterator().asSequence()
                .any { it.typeQualifiedName == targetClassName }
        ) {
            throw IllegalArgumentException("${targetClass.qualifiedName} must inherit from $targetClassName")
        }
    }

    // TODO 待将collectedMap改成Map<KSNode,List<KSNode>>，方便传递Dependency完善增量编译逻辑
    private fun writeToFile(
        codeGenerator: CodeGenerator,
        collectedMap: Map<String, List<String>>
    ) {
        if (collectedMap.isEmpty()) return

        val className = "KRouterMap\$\$${Random.nextInt().absoluteValue}"
        val classSpec = TypeSpec.classBuilder(className)
            .apply { modifiers += KModifier.PRIVATE }
            .build()

        val fileSpec = FileSpec.builder(GENERATED_SHARED_PACKAGE, className)
            .addType(classSpec)
            .build()

        fileSpec.writeTo(
            codeGenerator = codeGenerator,
            dependencies = Dependencies.ALL_FILES
        )
    }
}