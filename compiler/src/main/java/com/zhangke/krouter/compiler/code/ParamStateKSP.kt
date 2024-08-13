package com.zhangke.krouter.compiler.code

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName

fun buildParamStateClass(): TypeSpec {
    fun createCompanionObject(): TypeSpec = TypeSpec.companionObjectBuilder()
        .addProperty(
            PropertySpec.builder("CHECK_PROVIDED_FLAG", Int::class)
                .initializer("1")
                .build()
        )
        .addProperty(
            PropertySpec.builder("CHECK_IS_NOT_NULL_FLAG", Int::class)
                .initializer("CHECK_PROVIDED_FLAG shl 1")
                .build()
        )
        .addProperty(
            PropertySpec.builder("CHECK_TYPE_FLAG", Int::class)
                .initializer("CHECK_IS_NOT_NULL_FLAG shl 1")
                .build()
        )
        .addProperty(
            PropertySpec.builder("DEFAULT_CHECK_ACTION_FLAG", Int::class)
                .initializer("CHECK_TYPE_FLAG or CHECK_PROVIDED_FLAG")
                .build()
        )
        .build()

    fun createCheckSelfFunction(): FunSpec = FunSpec.builder("checkSelf")
        .addModifiers(KModifier.OPEN)
        .addParameter(
            ParameterSpec.builder("checkFlag", Int::class)
                .defaultValue("DEFAULT_CHECK_ACTION_FLAG")
                .build()
        )
        .build()

    fun createProvidedClass(): TypeSpec = TypeSpec.classBuilder("Provided")
        .addModifiers(KModifier.DATA)
        .addTypeVariable(TypeVariableName("T"))
        .superclass(ClassName("", "ParamState"))
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .addParameter("value", TypeVariableName("T"))
                .build()
        )
        .addProperty(
            PropertySpec.builder("name", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("name")
                .build()
        )
        .addProperty(
            PropertySpec.builder("value", TypeVariableName("T"))
                .initializer("value")
                .build()
        )
        .build()

    fun createNotProvidedClass(): TypeSpec = TypeSpec.classBuilder("NotProvided")
        .addModifiers(KModifier.DATA)
        .superclass(ClassName("", "ParamState"))
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .build()
        )
        .addProperty(
            PropertySpec.builder("name", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("name")
                .build()
        )
        .addFunction(
            FunSpec.builder("checkSelf")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("checkFlag", Int::class)
                .addStatement("if (checkFlag and CHECK_PROVIDED_FLAG == 0) return")
                .addStatement("throw IllegalArgumentException(\"Parameter [${'$'}name] is not provided.\")")
                .build()
        )
        .build()

    fun createProvidedButNullClass(): TypeSpec = TypeSpec.classBuilder("ProvidedButNull")
        .addModifiers(KModifier.DATA)
        .superclass(ClassName("", "ParamState"))
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .build()
        )
        .addProperty(
            PropertySpec.builder("name", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("name")
                .build()
        )
        .addFunction(
            FunSpec.builder("checkSelf")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("checkFlag", Int::class)
                .addStatement("if (checkFlag and CHECK_IS_NOT_NULL_FLAG == 0) return")
                .addStatement("throw IllegalArgumentException(\"Parameter [\$name] is provided but null.\")")
                .build()
        )
        .build()


    fun createProvidedButWrongTypeClass(): TypeSpec = TypeSpec.classBuilder("ProvidedButWrongType")
        .addModifiers(KModifier.DATA)
        .superclass(ClassName("", "ParamState"))
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .addParameter(
                    ParameterSpec.builder("message", String::class)
                        .defaultValue("\"Parameter [\$name] with a wrong type, please check it.\"")
                        .build()
                )
                .build()
        )
        .addProperty(
            PropertySpec.builder("name", String::class)
                .addModifiers(KModifier.OVERRIDE)
                .initializer("name")
                .build()
        )
        .addProperty(
            PropertySpec.builder("message", String::class)
                .initializer("message")
                .build()
        )
        .addFunction(
            FunSpec.builder("checkSelf")
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("checkFlag", Int::class)
                .addStatement("if (checkFlag and CHECK_TYPE_FLAG == 0) return")
                .addStatement("throw IllegalArgumentException(message)")
                .build()
        )
        .build()

    return TypeSpec.classBuilder("ParamState")
        .addModifiers(KModifier.PRIVATE, KModifier.SEALED)
        .addProperty(
            PropertySpec.builder("name", String::class)
                .initializer("\"\"")
                .addModifiers(KModifier.OPEN)
                .build()
        )
        .addFunction(createCheckSelfFunction())
        .addType(createCompanionObject())
        .addType(createProvidedClass())
        .addType(createNotProvidedClass())
        .addType(createProvidedButNullClass())
        .addType(createProvidedButWrongTypeClass())
        .build()
}