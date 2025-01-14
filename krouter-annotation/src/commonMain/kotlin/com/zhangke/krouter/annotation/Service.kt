package com.zhangke.krouter.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Service(
    val service: KClass<*> = NoImplementationService::class,
)

object NoImplementationService
