package com.zhangke.krouter

import org.junit.Assert
import org.junit.Test
import java.net.URI

class UriUtilsTest {

    @Test
    fun testBaseUri() {
        Assert.assertEquals("home", URI.create("home").baseUri)
        Assert.assertEquals("home-detail/as", URI.create("home-detail/as?name=webb").baseUri)
        Assert.assertEquals("/home/as", URI.create("/home/as?name=webb").baseUri)
        Assert.assertEquals("kroute.com/as", URI.create("kroute.com/as?name=webb").baseUri)
        Assert.assertEquals("https://home/as", URI.create("https://home/as?name=webb").baseUri)
        Assert.assertEquals(
            "https://kroute.com/home/as",
            URI.create("https://kroute.com/home/as?name=webb").baseUri,
        )
    }
}
