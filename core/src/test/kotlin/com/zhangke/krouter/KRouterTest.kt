package com.zhangke.krouter

import org.junit.Assert
import org.junit.Test

class KRouterTest {

    @Test
    fun `should fill failure when router property is in constructor and have multi params`() {
        class Screen(@Router val router: String = "", name: String = "zhangke", val age: Int = 12)

        val route = "screen/home"
        val screen = KRouter.getFilledRouterService(route, Screen()) as Screen
        Assert.assertEquals(route, screen.router)
    }

    @Test
    fun `should fill failure when router property is in constructor and type invalidate`() {
        class Screen(@Router val router: Int = 0)

        val route = "screen/home"
        val result = runCatching { KRouter.getFilledRouterService(route, Screen()) as Screen }
        Assert.assertEquals(true, result.isFailure)
    }

    @Test
    fun `should fill success when router property is in constructor`() {
        class Screen(@Router val router: String = "")

        val route = "screen/home"
        val screen = KRouter.getFilledRouterService(route, Screen()) as Screen
        Assert.assertEquals(route, screen.router)
    }

    @Test
    fun `should fill success when router property is standard`() {
        class Screen {
            @Router
            var router: String = ""
        }

        val route = "screen/home"
        val screen = KRouter.getFilledRouterService(route, Screen()) as Screen
        Assert.assertEquals(route, screen.router)
    }

    @Test
    fun `should failure when router property not visible`() {
        class Screen {
            @Router
            private var router: String = ""
        }

        val result = runCatching { KRouter.getFilledRouterService("screen/home", Screen()) }
        Assert.assertEquals(true, result.isFailure)
    }

    @Test
    fun `should failure when router property not string`() {
        class Screen {
            @Router
            var router: Int = 0
        }

        val result = runCatching { KRouter.getFilledRouterService("screen/home", Screen()) }
        Assert.assertEquals(true, result.isFailure)
    }

    @Test
    fun `should failure when router property is val`() {
        class Screen {
            @Router
            val router: String = ""
        }

        val result = runCatching { KRouter.getFilledRouterService("screen/home", Screen()) }
        Assert.assertEquals(true, result.isFailure)
    }
}