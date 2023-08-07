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

    class ClassDefine{
        var className: String? = null
        var dir: String? = null
        var baseURI: String? = null
    }
    class Method{
        var ctrl: CtrlMethodCfg? = null
        var svc: SvcMethodCfg? = null
        var dao: DaoMethodCfg? = null
    }

    open class CtrlMethodCfg {
        var name: String? = null
        var path: String? = null
        var httpMethod: String? = null
        var dtoClassName: String? = null
        var dtoFields: List<String>? = null
        var dtoFieldExcludes: String? =  null
        var dtoFieldIncludes: String? =  null
        var voClassName: String? = null
        var voFields: List<String>? = null
        var voListFlag: Boolean? = null
    }

    open class SvcMethodCfg {
        var name: String? = null
        var httpMethod: String? = null
        var boClassName: String? = null
        var boFields: List<String>? = null
        var boFieldExcludes: String? =  null
        var boFieldIncludes: String? =  null
        var boResultClassName: String? = null
        var boResultFields: List<String>? = null
        var boResultListFlag: Boolean? = null
    }

    open class DaoMethodCfg {
        var name: String? = null
        var httpMethod: String? = null
        var poClassName: String? = null
        var poFieldExcludes: String? =  null
        var poFieldIncludes: String? =  null
        var poFields: List<String>? = null
        var poResultClassName: String? = null
        var poResultFields: List<String>? = null
        var poResultListFlag: Boolean? = null
    }
    class RenderItem{
        var title: String? = null
        var field: String? = null
    }
}