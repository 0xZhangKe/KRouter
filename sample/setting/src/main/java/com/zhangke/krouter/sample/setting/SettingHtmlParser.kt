package com.zhangke.krouter.sample.setting

import com.zhangke.krouter.annotation.Service
import com.zhangke.krouter.sample.core.HtmlParser

@Service(service = HtmlParser::class)
class SettingHtmlParser : HtmlParser {

    override fun parse(document: String): String {
        return "SettingHtmlParser parse: $document"
    }
}
