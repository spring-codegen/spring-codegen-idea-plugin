package com.springcodegen.idea.plugin.config

import com.springcodegen.idea.plugin.services.ResourceService

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
    var relationModels: List<ModelCfg>? = null

    companion object{
        @JvmStatic var instance:CodeCfg? = null
        @JvmStatic fun load(){
            var codeCfg = ResourceService.readCodeCfgYaml("code-cfg.yaml")
            instance = codeCfg
        }
    }

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
        var refName: String? = null
        var fieldExcludes: String? =  null
        var fieldIncludes: String? =  null
    }
    open class MethodArgCfg{
        var isPathVar:Boolean = false
        var listTypeFlag: Boolean? = false
        var className: String? = null
        var refName: String? = null
        var comment: String? = null
    }
    open class MethodResultCfg{
        var listTypeFlag: Boolean? = false
        var className: String? = null
        var outputPaged: Boolean? = false
        var refName: String? = null
        var comment: String? = null
    }
    open class MethodCfg{
        var type: String? = null
        var comment:String? = null
        var request: RequestCfg? = null
        var name: String? = null
        var args:List<MethodArgCfg>? = null
        var result:MethodResultCfg? = null

        var sqlDataFieldExcludes: String? =  null
        var sqlDataFieldIncludes: String? =  null
        var sqlConditionFieldExcludes: String? =  null
        var sqlConditionFieldIncludes: String? =  null
    }
}