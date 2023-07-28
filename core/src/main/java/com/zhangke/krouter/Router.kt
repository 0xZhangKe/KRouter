package com.zhangke.krouter

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Router(
    val router: String,
    val type: KClass<*> = Unit::class,
)
