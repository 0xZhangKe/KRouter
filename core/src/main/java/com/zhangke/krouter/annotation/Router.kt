package com.zhangke.krouter.annotation


@Deprecated(
    message = "Not useful",
    replaceWith = ReplaceWith("com.zhangke.krouter.annotation.Param")
)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Router
