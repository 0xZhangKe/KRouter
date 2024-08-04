package com.zhangke.krouter.compiler.code

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.zhangke.krouter.annotation.Destination
import com.zhangke.krouter.annotation.Param
import com.zhangke.krouter.compiler.ext.combinations
import com.zhangke.krouter.compiler.ext.requestAnnotation
import com.zhangke.krouter.compiler.ext.requireAnnotation

fun buildGetRouterMapFunc(
    collectedMap: List<KSClassDeclaration>,
): FunSpec {
    val mapType = LambdaTypeName.get(
        receiver = null,
        returnType = Any::class.asTypeName(),
        parameters = arrayOf(
            Map::class.asClassName()
                .parameterizedBy(
                    String::class.asTypeName(),
                    Any::class.asTypeName()
                        .copy(nullable = true)
                )
        )
    )

    val codeBlock = CodeBlock.builder()
        .beginControlFlow("return when (baseRoute)")
        .apply {
            buildRouterCondition(
                collectedMap = collectedMap,
                block = { clazz ->
                    buildRouterConstructorInject(clazz)
                }
            )
        }
        .addStatement(
            "else -> throw IllegalArgumentException(%P)",
            "Route [\$baseRoute] Not Found."
        )
        .endControlFlow()
        .build()

    return FunSpec.builder("getMap")
        .addParameter("baseRoute", type = String::class)
        .returns(mapType)
        .addCode(codeBlock)
        .build()
}


fun CodeBlock.Builder.buildRouterCondition(
    collectedMap: List<KSClassDeclaration>,
    block: CodeBlock.Builder.(KSClassDeclaration) -> Unit
) {
    collectedMap.forEach { clazz ->
        val annotation = clazz.requireAnnotation<Destination>()
        val routers = annotation.arguments
            .firstOrNull { it.name?.asString() == "router" }
            ?.let { (it.value as? ArrayList<*>)?.filterIsInstance<String>() }
            ?: return@forEach

        val baseRouterCondition = routers
            .joinToString(separator = ", ") { "\"$it\"" }

        this.beginControlFlow("$baseRouterCondition -> { params ->")
            .apply { block(clazz) }
            .endControlFlow()
    }
}

fun CodeBlock.Builder.buildRouterConstructorInject(clazz: KSClassDeclaration) {
    val parameters = clazz.primaryConstructor?.parameters
        ?: emptyList()

    // 初始化参数
    parameters.forEach { parameter ->
        val parameterName = parameter.routeParamName // 参数的映射名称
        val parameterType = parameter.type.resolve() // 参数的类型
        val targetInjectType = parameterType.requireParameterizedClassName()
        val targetInjectName = parameter.name?.asString() ?: ""

        addStatement(
            "val %L = params.handleParams<%T>(%S)",
            "${targetInjectName}_$parameterName",
            targetInjectType,
            parameterName,
        )
    }

    // 检测校验参数
    parameters.forEach { parameter ->
        val paramAnnotation = parameter.annotations.firstOrNull()
        val parameterType = parameter.type.resolve() // 参数的类型
        val parameterName = parameter.routeParamName // 参数的映射名称
        val targetInjectName = parameter.name?.asString() ?: ""

        // 是否为可空类型
        val isNullable = parameterType.nullability != Nullability.NOT_NULL

        // 是否必须填写的参数，其次若没有默认值，则为必填
        val isRequired = paramAnnotation?.arguments
            ?.firstOrNull { it.name?.asString() == "required" }
            ?.value == true || !parameter.hasDefault

        val flags = mutableListOf<String>()
        flags.add("ParamState.CHECK_TYPE_FLAG")
        if (isRequired) flags.add("ParamState.CHECK_PROVIDED_FLAG")
        if (!isNullable) flags.add("ParamState.CHECK_IS_NOT_NULL_FLAG")
        val flagsCode = flags.joinToString(separator = " or ")

        addStatement("${targetInjectName}_${parameterName}.checkSelf(%L)", flagsCode)
    }

    // 获取所有必须提供值的Parameter
    val paramsMustBeProvided = parameters
        .filter { !it.hasDefault }

    // 获取所有可选参数的组合
    val combinations = parameters
        .filter { it.hasDefault }
        .combinations()
        .sortedByDescending { it.size }

    // 生成覆盖所有情况的when条件判断
    beginControlFlow("when")
    for (conditionParams in combinations) {
        val targetInjectParams = paramsMustBeProvided + conditionParams

        if (targetInjectParams.isEmpty()) {
            beginControlFlow("else ->")
            when (clazz.classKind) {
                ClassKind.CLASS -> addStatement("%T()", clazz.toClassName())
                ClassKind.OBJECT -> addStatement("%T", clazz.toClassName())
                else -> addStatement(
                    "throw IllegalArgumentException(%S)",
                    "Unsupported class kind: ${clazz.classKind}"
                )
            }
            endControlFlow()
            continue
        }

        val condition = conditionParams.takeIf { it.isNotEmpty() }?.run {
            joinToString(separator = " && ") {
                val parameterName = it.routeParamName
                val targetInjectName = it.name?.asString() ?: ""

                "${targetInjectName}_${parameterName} is ParamState.Provided<*>"
            }
        } ?: "else"

        beginControlFlow("$condition ->")
        val parameterCodeResult = targetInjectParams.joinToCode(separator = ",\n") {
            val parameterType = it.type.resolve()
            val parameterName = it.routeParamName
            val isNullable = parameterType.nullability != Nullability.NOT_NULL
            val targetInjectType = parameterType.requireParameterizedClassName()
            val targetInjectName = it.name?.asString() ?: ""

            var sentence = when {
                it in conditionParams -> "${it.name?.asString()} = ${targetInjectName}_${parameterName}.value as %T"
                else -> "${it.name?.asString()} = (${targetInjectName}_${parameterName} as ParamState.Provided<*>).value as %T"
            }
            if (isNullable) {
                sentence = sentence.replace(".value", "?.value")
                    .replace(" as ", " as? ")
            }

            buildCodeBlock { add(sentence, targetInjectType) }
        }
        addStatement("%T(%L)", clazz.toClassName(), parameterCodeResult)
        endControlFlow()
    }
    endControlFlow()

    buildRouterPropertiesInject(clazz)
}

fun CodeBlock.Builder.buildRouterPropertiesInject(clazz: KSClassDeclaration) {
    // 只处理当前类中可见的参数，不处理从父类继承来的
    val properties = clazz.getDeclaredProperties()
        .mapNotNull { property ->
            property.takeIf { it.isMutable } // 需要确保属性是可变的
                ?.requestAnnotation<Param>()
                ?.let { property to it }
        }
        .toList()

    // 若没有需要注入的参数，则直接返回
    if (properties.isEmpty()) return

    // 处理Property相关数据注入逻辑
    beginControlFlow(".apply {")

    // 初始化参数
    properties.forEach { (property, _) ->
        val parameterName = property.routeParamName // 参数的映射名称
        val parameterType = property.type.resolve() // 参数的类型
        val targetInjectName = property.simpleName.asString()
        val targetInjectType = parameterType.requireParameterizedClassName()

        addStatement(
            "val %L = params.handleParams<%T>(%S)",
            "${targetInjectName}_$parameterName",
            targetInjectType,
            parameterName,
        )
    }

    // 检查校验参数
    properties.forEach { (property, param) ->
        val parameterName = property.routeParamName // 参数的映射名称
        val parameterType = property.type.resolve() // 参数的类型
        val targetInjectName = property.simpleName.asString()

        val isNullable = parameterType.nullability != Nullability.NOT_NULL

        // 是否必须填写的参数，若此property标记为lateinit则说明必须提供值
        val isRequired = param.arguments
            .firstOrNull { it.name?.asString() == "required" }
            ?.value == true || property.modifiers.contains(Modifier.LATEINIT)

        val flags = mutableListOf<String>()
        flags.add("ParamState.CHECK_TYPE_FLAG")
        if (isRequired) flags.add("ParamState.CHECK_PROVIDED_FLAG")
        if (!isNullable) flags.add("ParamState.CHECK_IS_NOT_NULL_FLAG")
        val flagsCode = flags.joinToString(separator = " or ")

        addStatement("${targetInjectName}_${parameterName}.checkSelf(%L)", flagsCode)
    }

    // 注入参数
    properties.forEach { (property, _) ->
        val parameterName = property.routeParamName // 参数的映射名称
        val parameterType = property.type.resolve() // 参数的类型
        val targetInjectType = parameterType.requireParameterizedClassName()
        val targetInjectName = property.simpleName.asString()

        val isNullable = parameterType.nullability != Nullability.NOT_NULL
        var sentence =
            "this.${property.simpleName.asString()} = (${targetInjectName}_${parameterName} as ParamState.Provided<*>).value as %T"

        if (isNullable) {
            sentence = sentence.replace(".value", "?.value")
                .replace(" as ", " as? ")
        }

        addStatement(sentence, targetInjectType)
    }
    endControlFlow()
}

private val routeParamsNameCache = mutableMapOf<KSNode, String?>()
val KSValueParameter.routeParamName: String?
    get() = routeParamsNameCache.getOrPut(this) {
        val paramAnnotation = this.requestAnnotation<Param>()

        return paramAnnotation?.arguments
            ?.firstOrNull { it.name?.asString() == "name" }
            ?.let { it.value as? String }
            ?.takeIf(String::isNotBlank)
            ?: name?.asString()
    }

val KSPropertyDeclaration.routeParamName: String?
    get() = routeParamsNameCache.getOrPut(this) {
        val paramAnnotation = this.requestAnnotation<Param>()

        return paramAnnotation?.arguments
            ?.firstOrNull { it.name?.asString() == "name" }
            ?.let { it.value as? String }
            ?.takeIf(String::isNotBlank)
            ?: simpleName.asString()
    }

fun KSType.requireParameterizedClassName() = toClassName().let { typeName ->
    when {
        arguments.isNotEmpty() -> {
            typeName.parameterizedBy(
                arguments.mapNotNull { it.type?.resolve()?.toTypeName() }
            )
        }

        else -> typeName
    }
}