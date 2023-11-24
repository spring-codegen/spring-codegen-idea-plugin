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
    open class FieldDefine(var name:String, var notNull:Boolean, var type:String?, var comment:String?){
        constructor(name:String, notNull:Boolean, type:String?) : this(name, notNull, type, null)
        var minLen: Int? = -1
        var maxLen: Int? = -1
    }
    open class BeanDefine{
        var className:String? = null
        var comment:String? = null
        var fields: List<FieldDefine>? = null
    }
    open class MethodDefine{
        var name: String? = null
        var comment:String? = null
        var inputClass:BeanDefine? = null
        var outputClass:BeanDefine? = null

        var input: String? = null
        var inputFields: List<FieldDefine>? = null
        var inputFieldExcludes: String? =  null
        var inputFieldIncludes: String? =  null
        var outputClassName: String? = null
        var outputFields: List<FieldDefine>? = null
        var outputFieldExcludes: String? =  null
        var outputFieldIncludes: String? =  null
        var inputListTypeFlag: Boolean? = false
        var outputListTypeFlag: Boolean? = false
        var outputPaged: Boolean? = false
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
        var inputFields: List<FieldDefine>? = null
        var inputFieldExcludes: String? =  null
        var inputFieldIncludes: String? =  null
        var outputClassName: String? = null
        var outputFields: List<FieldDefine>? = null
        var outputFieldExcludes: String? =  null
        var outputFieldIncludes: String? =  null
        var inputListTypeFlag: Boolean? = false
        var outputListTypeFlag: Boolean? = false
        var outputPaged: Boolean? = false
    }
    class RenderItem{
        var title: String? = null
        var field: String? = null
    }
}