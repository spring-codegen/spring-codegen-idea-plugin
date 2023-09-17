package com.cmcc.paas.ideaplugin.codegen.config

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
        var request: RequestCfg? = null
        var ctrl: MethodCfg? = null
        var svc: MethodCfg? = null
        var dao: MethodCfg? = null
    }
    open class RequestCfg{
        var httpMethod: String? = "GET"
        var path: String? = null
        var comment:String? = null
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
        var outputPaged: Boolean? = false
    }
    open class FieldCfg(var name:String, var notNull:Boolean){
        var minLen: Int = -1
        var maxLen: Int = -1

    }
    class RenderItem{
        var title: String? = null
        var field: String? = null
    }
}