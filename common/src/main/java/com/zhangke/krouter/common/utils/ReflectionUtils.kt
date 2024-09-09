package com.zhangke.krouter.common.utils

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.zhangke.krouter.RouteParam

inline fun <reified T> KSClassDeclaration.requireAnnotation(): KSAnnotation {
    return annotations.first {
        it.typeDeclaration.asClassDeclaration().qualifiedName?.asString() == T::class.qualifiedName
    }
}

fun KSDeclaration.asClassDeclaration(): KSClassDeclaration {
    return this as KSClassDeclaration
}

val KSAnnotation.typeDeclaration: KSDeclaration get() = annotationType.resolve().declaration

fun KSAnnotation.findAnnotationValue(name: String): String? {
    return arguments.firstOrNull { it.name?.asString() == name }
        ?.value
        ?.toString()
}

fun KSPropertyDeclaration.getRouterParamsNameValue(): String? {
    return annotations.firstOrNull {
        it.typeDeclaration.asClassDeclaration().qualifiedName?.asString() == RouteParam::class.qualifiedName
    }?.findAnnotationValue("name")
}
