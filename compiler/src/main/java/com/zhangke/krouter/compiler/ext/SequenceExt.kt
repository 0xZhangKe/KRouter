package com.zhangke.krouter.compiler.ext

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

fun <T> List<T>.combinations(): List<List<T>> {
    // 如果列表为空，则直接返回一个只包含空列表的列表
    if (isEmpty()) return listOf(emptyList())

    // 获取列表的第一个元素
    val first = this.first()
    // 获取剩余的元素列表
    val rest = this.drop(1)

    // 递归地获取剩余元素的所有组合
    val subCombinations = rest.combinations()

    // 生成包含第一个元素的所有组合
    val withFirst = subCombinations.map { combination -> combination + first }

    // 合并包含第一个元素的组合和不包含第一个元素的组合
    return withFirst + subCombinations
}