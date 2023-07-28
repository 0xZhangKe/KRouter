package com.zhangke.krouter

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Route(
    val route: String,
    val type: KClass<*> = Unit::class,
)
