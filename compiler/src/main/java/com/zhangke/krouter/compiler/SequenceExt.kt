package com.zhangke.krouter.compiler

internal fun Sequence<Any>.isSingleElement(): Boolean {
    val iterator = iterator()
    if (iterator.hasNext()) {
        iterator.next()
        if (iterator.hasNext()) return false
    } else {
        return false
    }
    return true
}
