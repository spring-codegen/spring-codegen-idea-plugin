package com.github.baboy.ideaplugincodegen.setting

/**
 *
 * @author zhangyinghui
 * @date 2023/8/1
 */
class CtrlSetting {

    var dir: String? = null
    var clsName: String? = null
    var baseURI: String? = null
    var methods: List<CtrlMethod>? = null

    class CtrlMethod{
        var name: String? = null
        var path: String? = null
        var httpMethod: String? = null
        var dtoClassName: String? = null
        var dtoFields: List<String>? = null
        var voClassName:String? = null
        var voFields: List<String>? = null
    }

}