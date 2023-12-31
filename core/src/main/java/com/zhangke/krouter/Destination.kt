package com.zhangke.krouter

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Destination(
    vararg val router: String,
    val type: KClass<*> = Unit::class,
)
