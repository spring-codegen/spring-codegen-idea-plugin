package com.cmcc.paas.ideaplugin.codegen.config

/**
 *
 * @author zhangyinghui
 * @date 2023/8/2
 */
open class CodeCfg{
    var ctrlClass: ClassCfg? = null
    var svcClass: ClassCfg? = null
    var daoClass: ClassCfg? = null
    var methods: List<MethodCfg>? = null
    var renderItems: List<RenderItem>? = null

    open class ClassCfg{
        var title: String? = null
        var className: String? = null
        var dir: String? = null
        var baseURI: String? = null
    }
    open class RequestCfg{
        var httpMethod: String? = "GET"
        var path: String? = null
        var comment:String? = null
    }
    open class MethodCfg{
        var type: String? = null
        var comment:String? = null
        var request: RequestCfg? = null
        var name: String? = null
        var inputClassName: String? = null
        var inputFieldExcludes: String? =  null
        var inputFieldIncludes: String? =  null
        var outputClassName: String? = null
        var outputFieldExcludes: String? =  null
        var outputFieldIncludes: String? =  null
        var inputListTypeFlag: Boolean? = false
        var outputListTypeFlag: Boolean? = false
        var outputPaged: Boolean? = false
        var sqlDataFieldExcludes: String? =  null
        var sqlDataFieldIncludes: String? =  null
        var sqlConditionFieldExcludes: String? =  null
        var sqlConditionFieldIncludes: String? =  null
    }
    class RenderItem{
        var title: String? = null
        var field: String? = null
    }
}