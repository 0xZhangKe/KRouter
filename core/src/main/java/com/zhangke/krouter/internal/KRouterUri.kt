package com.zhangke.krouter.internal

data class KRouterUri private constructor(
    val baseUrl: String,
    val queries: Map<String, String>,
) {

    companion object {

        fun create(uri: String): KRouterUri {
            val array = uri.split("?")
            val queries = mutableMapOf<String, String>()
            if (array.size == 2) {
                val queryArray = array[1].split("&")
                queryArray.forEach {
                    val query = it.split("=")
                    queries[query[0]] = query[1]
                }
            }
            return KRouterUri(array[0].removeSuffix("/"), queries)
        }
    }
}
