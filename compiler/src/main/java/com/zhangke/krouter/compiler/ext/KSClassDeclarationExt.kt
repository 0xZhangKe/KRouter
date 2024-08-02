package com.zhangke.krouter.compiler.ext

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal inline fun <reified T> KSClassDeclaration.requireAnnotation(): KSAnnotation {
    return annotations.first {
        it.typeDeclaration.asClassDeclaration().qualifiedName?.asString() == T::class.qualifiedName
    }
}
