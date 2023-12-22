package com.cmcc.paas.ideaplugin.codegen.gen.define.model

/**
 *
 * @author zhangyinghui
 * @date 2023/12/1
 */
class CtrlClass(className:String, pkg:String?, comment:String?, fields:MutableList<Field>?):ClassModel(className, pkg, comment, fields) {
    constructor(className:String) : this(className, null, null, null)
    var request:Request? = null
    override fun clone():ClassModel{
        var cls:CtrlClass = super.clone() as CtrlClass
        cls.request = if (request != null) request?.clone() else null
        return cls
    }
    class Method(name:String, inputClass:ClassModel, outputClass:ClassModel, resultListFlag:Boolean):ClassModel.Method(name, inputClass, outputClass, resultListFlag){
        var request:Request? = null
        override fun clone():CtrlClass.Method{
            var method:Method = super.clone() as Method
            method.request = if (request != null) request?.clone() else null
            return method
        }
    }
    class Request(var path:String, var httpMethod:String?){
        fun clone():Request{
            return Request(path, httpMethod)
        }
    }
}