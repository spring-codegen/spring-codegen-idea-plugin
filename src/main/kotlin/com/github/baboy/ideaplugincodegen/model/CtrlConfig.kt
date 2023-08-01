package com.github.baboy.ideaplugincodegen.model

/**
 *
 * @author zhangyinghui
 * @date 2023/8/1
 */
class CtrlConfig {

    var dir: String? = null
    var clsName: String? = null
    var baseURI: String? = null
    var methods: List<CtrlMethod>? = null

    class CtrlMethod{
        var name: String? = null
        var path: String? = null
        var requestMethod: String? = null
        var dtoClsName: String? = null
        var dtoFields: List<String>? = null
        var voClassName:String? = null
        var voFields: List<String>? = null
    }

}