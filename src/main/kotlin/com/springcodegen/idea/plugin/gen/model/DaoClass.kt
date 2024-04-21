package com.springcodegen.idea.plugin.gen.model

/**
 *
 * @author zhangyinghui
 * @date 2023/12/1
 */
class DaoClass(className:String, pkg:String?, comment:String?, fields:MutableList<Field>?):
    ClassModel(className, pkg, comment, fields) {

    constructor(className:String) : this(className, null, null, null)
    class Method(name:String, args:MutableList<MethodArg>, methodResult: MethodResult):
        ClassModel.Method(name, args, methodResult){
       var sqlDataFields:List<Field>? = null
        var sqlCondFields:List<Field>? = null
    }
}