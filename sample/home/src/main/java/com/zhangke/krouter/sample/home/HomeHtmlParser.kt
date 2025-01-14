package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Service
import com.zhangke.krouter.sample.core.HtmlParser

@Service
class HomeHtmlParser: HtmlParser {

    override fun parse(document: String): String {
        return "HomeHtmlParser parse: $document"
    }
}
