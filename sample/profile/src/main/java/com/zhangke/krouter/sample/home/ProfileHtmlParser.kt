package com.zhangke.krouter.sample.home

import com.zhangke.krouter.annotation.Service
import com.zhangke.krouter.sample.core.HtmlParser

@Service(service = HtmlParser::class)
class ProfileHtmlParser : HtmlParser {

    override fun parse(document: String): String {
        return "ProfileHtmlParser parse: $document"
    }
}
