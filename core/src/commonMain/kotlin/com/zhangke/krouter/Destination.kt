package com.zhangke.krouter

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Destination(
    val route: String,
)
