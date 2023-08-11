package com.github.baboy.ideaplugincodegen.config

/**
 *
 * @author zhangyinghui
 * @date 2023/8/2
 */
open class CodeCfg{
    var ctrlClass: ClassDefine? = null
    var svcClass: ClassDefine? = null
    var daoClass: ClassDefine? = null
    var methods: List<Method>? = null
    var renderItems: List<RenderItem>? = null

    open class ClassDefine{
        var title: String? = null
        var className: String? = null
        var dir: String? = null
        var baseURI: String? = null
    }
    class Method{
        var uri: UriCfg? = null
        var ctrl: MethodCfg? = null
        var svc: MethodCfg? = null
        var dao: MethodCfg? = null
    }
    open class UriCfg{
        var httpMethod: String? = "GET"
        var path: String? = null

    }
    open class MethodCfg{
        var name: String? = null
        var inputClassName: String? = null
        var inputFields: List<FieldCfg>? = null
        var inputFieldExcludes: String? =  null
        var inputFieldIncludes: String? =  null
        var outputClassName: String? = null
        var outputFields: List<FieldCfg>? = null
        var outputFieldExcludes: String? =  null
        var outputFieldIncludes: String? =  null
        var inputListTypeFlag: Boolean? = false
        var outputListTypeFlag: Boolean? = false
    }
    open class FieldCfg(var name:String, var notNull:Boolean){

    }
    class RenderItem{
        var title: String? = null
        var field: String? = null
    }
}