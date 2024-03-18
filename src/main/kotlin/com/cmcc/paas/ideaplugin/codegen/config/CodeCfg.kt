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
    var models: List<ModelCfg>? = null
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
    open class ModelCfg{
        var type: String? = null
        var className: String? = null
        var fieldExcludes: String? =  null
        var fieldIncludes: String? =  null
        var outputPaged: Boolean? = false
    }
    open class MethodArg{
        var isPathVar:Boolean = false
        var listTypeFlag: Boolean? = false
        var className: String? = null
        var refName: String? = null
    }
    open class MethodResult{
        var listTypeFlag: Boolean? = false
        var className: String? = null
        var outputPaged: Boolean? = false
        var refName: String? = null
    }
    open class MethodCfg{
        var type: String? = null
        var comment:String? = null
        var request: RequestCfg? = null
        var name: String? = null
        var args:List<MethodArg>? = null
        var result:MethodResult? = null

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