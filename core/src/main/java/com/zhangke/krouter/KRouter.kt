package com.zhangke.krouter

object KRouter {
    private var getRouteMapFunc: ((String) -> (Map<String, Any?>) -> Any?)? = null

    const val PRESET_ROUTER = "__router" // 预设获取路由的KEY
    const val PRESET_PARAMS = "__params" // 获取路由注入的参数Map, 类型需要注意是Map<String, Any?>

    fun init(getRouteMapFunc: (String) -> (Map<String, Any?>) -> Any?) {
        this.getRouteMapFunc = getRouteMapFunc
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> route(
        router: String,
        extraParams: Map<String, Any?> = emptyMap()
    ): T? {
        val baseRoute = router.substringBefore('?')

        val paramsFromRouter = router
            .takeIf { it.contains('?') }
            ?.substringAfterLast('?')
            ?.split('&')
            ?.mapNotNull {
                val list = it.split('=')
                    .takeIf(List<*>::isNotEmpty)
                    ?: return@mapNotNull null

                list[0] to (list.getOrNull(1) ?: "")
            }?.toMap()
            ?: emptyMap()

        val params = paramsFromRouter + extraParams +
                mapOf(
                    PRESET_ROUTER to router,
                    PRESET_PARAMS to paramsFromRouter + extraParams
                )

        return getRouteMapFunc
            ?.invoke(baseRoute)   // 获取路由
            ?.invoke(params)   // 注入参数
                as? T
    }
}
