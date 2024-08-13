package com.zhangke.krouter.compiler.code

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName

fun buildHandleParamsFunction(): FunSpec = FunSpec.builder("handleParams")
    .addModifiers(KModifier.PRIVATE, KModifier.INLINE)
    .addTypeVariable(
        TypeVariableName("T", Any::class.asTypeName())
            .copy(reified = true)
    )
    .receiver(
        Map::class.asClassName()
            .parameterizedBy(
                String::class.asTypeName(),
                Any::class.asTypeName()
                    .copy(nullable = true)
            )
    )
    .addParameter("name", String::class)
    .returns(ClassName("", "ParamState"))
    .addStatement("if (!this.containsKey(name)) return ParamState.NotProvided(name)")
    .addStatement("val value = this[name] ?: return ParamState.ProvidedButNull(name)")
    .beginControlFlow("if (!T::class.isInstance(value))")
    .addStatement(
        "return ParamState.ProvidedButWrongType(name, %P)",
        "Parameter [\$name] expects a [\${T::class.qualifiedName}], but a [\${value::class.qualifiedName}] is provided."
    )
    .endControlFlow()
    .addStatement("return ParamState.Provided(name, value as T)")
    .build()

