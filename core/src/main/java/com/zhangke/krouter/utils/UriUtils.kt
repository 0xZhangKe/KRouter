package com.zhangke.krouter.utils

import java.net.URI

internal val URI.baseUri: String?
    get() {
        if (path.isNullOrEmpty()) return null
        val baseUrlBuilder = StringBuilder()
        scheme?.takeIf { it.isNotEmpty() }?.let {
            baseUrlBuilder.append("$it://")
        }
        host?.takeIf { it.isNotEmpty() }?.let {
            baseUrlBuilder.append(it)
        }
        if (baseUrlBuilder.isEmpty()) return path
        if (!baseUrlBuilder.endsWith("/") && !path.startsWith("/")) {
            baseUrlBuilder.append("/")
        }
        baseUrlBuilder.append(path)
        return baseUrlBuilder.toString()
    }
