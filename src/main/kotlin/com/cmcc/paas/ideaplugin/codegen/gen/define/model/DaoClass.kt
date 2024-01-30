package com.cmcc.paas.ideaplugin.codegen.gen.define.model

/**
 *
 * @author zhangyinghui
 * @date 2023/12/1
 */
class DaoClass(className:String, pkg:String?, comment:String?, fields:MutableList<Field>?):ClassModel(className, pkg, comment, fields) {

    constructor(className:String) : this(className, null, null, null)
    class Method(name:String, inputClass:ClassModel, outputClass:ClassModel, resultListFlag:Boolean):ClassModel.Method(name, inputClass, outputClass, resultListFlag){
       var sqlDataFields:List<Field>? = null
        var sqlCondFields:List<Field>? = null
    }
}