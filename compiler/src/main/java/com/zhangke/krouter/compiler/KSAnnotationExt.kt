package com.zhangke.krouter.compiler

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType

internal val KSAnnotation.typeDeclaration: KSDeclaration get() = annotationType.resolve().declaration

internal fun KSAnnotation.findArgumentTypeByName(name: String): String? {
    return arguments.firstOrNull { it.name?.asString() == name }?.value
        ?.let { it as? KSType }
        ?.declaration
        ?.qualifiedName
        ?.asString()
}
