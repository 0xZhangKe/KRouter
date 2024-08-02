package com.zhangke.krouter.compiler.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration

internal fun KSDeclaration.asClassDeclaration(): KSClassDeclaration {
    return this as KSClassDeclaration
}
