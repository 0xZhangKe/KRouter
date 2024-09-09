package com.zhangke.krouter.internal

data class KRouterUri private constructor(
    val baseUrl: String,
    val queries: Map<String, String>,
) {

    inline fun <reified T> requireQuery(query: String): T {
        return getQuery<T>(query) ?: throw IllegalArgumentException("Missing parameter $query in $this")
    }

    inline fun <reified T> getQuery(query: String): T? {
        val value = queries[query] ?: return null
        if (value.isEmpty()) return null
        return when (T::class) {
            Boolean::class -> value.toBoolean() as T
            Byte::class -> value.toByte() as T
            Char::class -> value.toCharArray().firstOrNull() as T
            Short::class -> value.toShort() as T
            Int::class -> value.toInt() as T
            Long::class -> value.toLong() as T
            Float::class -> value.toFloat() as T
            Double::class -> value.toDouble() as T
            String::class -> value as T
            else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
        }
    }

    override fun toString(): String {
        return buildString {
            append(baseUrl)
            if (queries.isNotEmpty()) {
                val rawQuery = queries.entries.joinToString(prefix = "?", separator = "&") { "${it.key}=${it.value}" }
                append(rawQuery)
            }
        }
    }

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
