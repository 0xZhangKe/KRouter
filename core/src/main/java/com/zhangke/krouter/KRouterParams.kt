package com.zhangke.krouter

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class KRouterParams(val name: String)