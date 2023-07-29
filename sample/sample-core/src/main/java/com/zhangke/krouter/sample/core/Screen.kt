package com.zhangke.krouter.sample.core

interface Screen {

    fun content() {
        println("${this::class.simpleName} not implement the content function")
    }
}
