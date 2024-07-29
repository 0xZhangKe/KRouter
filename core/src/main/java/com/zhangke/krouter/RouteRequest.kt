package com.zhangke.krouter

class RouteRequest {
    private val _objParams by lazy { mutableMapOf<String, Any>().also { objParams = it } }
    private val _intParams by lazy { mutableMapOf<String, Int>().also { intParams = it } }
    private val _longParams by lazy { mutableMapOf<String, Long>().also { longParams = it } }
    private val _floatParams by lazy { mutableMapOf<String, Float>().also { floatParams = it } }
    private val _doubleParams by lazy { mutableMapOf<String, Double>().also { doubleParams = it } }
    private val _stringParams by lazy { mutableMapOf<String, String>().also { stringParams = it } }
    private val _booleanParams by lazy {
        mutableMapOf<String, Boolean>().also { booleanParams = it }
    }

    var objParams: MutableMap<String, Any>? = null
        private set
    var intParams: MutableMap<String, Int>? = null
        private set
    var longParams: MutableMap<String, Long>? = null
        private set
    var floatParams: MutableMap<String, Float>? = null
        private set
    var doubleParams: MutableMap<String, Double>? = null
        private set
    var stringParams: MutableMap<String, String>? = null
        private set
    var booleanParams: MutableMap<String, Boolean>? = null
        private set

    fun <T : Any> with(key: String, value: T) {
        when {
            value::class == Int::class -> _intParams[key] = value as Int
            value::class == Long::class -> _longParams[key] = value as Long
            value::class == Float::class -> _floatParams[key] = value as Float
            value::class == Double::class -> _doubleParams[key] = value as Double
            value::class == String::class -> _stringParams[key] = value as String
            value::class == Boolean::class -> _booleanParams[key] = value as Boolean
            else -> _objParams[key] = value
        }
    }
}