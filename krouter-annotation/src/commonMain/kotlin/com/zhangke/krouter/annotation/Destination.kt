package com.zhangke.krouter.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Destination(
    val route: String,
)
