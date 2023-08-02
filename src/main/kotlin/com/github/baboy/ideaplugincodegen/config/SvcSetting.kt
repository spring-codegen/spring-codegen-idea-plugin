package com.github.baboy.ideaplugincodegen.config

/**
 *
 * @author zhangyinghui
 * @date 2023/8/1
 */
class SvcSetting {

    var dir: String? = null
    var clsName: String? = null
    var baseURI: String? = null
    var methods: List<SvcMethod>? = null

    class SvcMethod{
        var name: String? = null
        var path: String? = null
        var requestMethod: String? = null
        var dtoClsName: String? = null
        var dtoFields: List<String>? = null
        var voClassName:String? = null
        var voFields: List<String>? = null
    }

}