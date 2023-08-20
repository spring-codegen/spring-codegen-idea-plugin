package com.cmcc.paas.ideaplugin.codegen.gen.define.model

import java.util.stream.Collectors

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class ClassModel(var className:String, var pkg:String, var comment:String?, var fields:MutableList<Field>?) {
    var tableName:String? = null
    var methods:MutableList<Method>? = ArrayList()
    var imports:MutableSet<String>? = HashSet()
    var baseType:Boolean = false
    var name:String? = null
    var request:RequestURI? = null
    var superClass:ClassModel? = null
    var dependency:ClassModel? = null
    fun clone():ClassModel{
        var cls = ClassModel(className, pkg,comment, if (fields!= null) fields!!.stream().map { e -> e.clone() }.toList() else null)
        cls.methods = if(methods != null)methods!!.stream().map { e -> e.clone() }.toList() else null
        cls.imports = if(imports != null)imports!!.stream().map { e -> e }.collect(Collectors.toSet()) else null
        cls.baseType = baseType
        cls.name = name
        cls.request = if(request != null)request!!.clone() else null
        cls.superClass = superClass
        cls.dependency = dependency
        return cls;
    }
    class Field(val name:String, val javaType:String, val comment:String?, val notNull:Boolean?, val setter:String, val getter:String){
        var pkg:String? = null
        var column:String? = null
        var classType:ClassModel? = null
        var baseType:Boolean = false

        fun clone():Field{
            var f = Field(name, javaType, comment, notNull, setter, getter)
            f.column = column
            f.classType = if(classType != null) classType!!.clone() else null
            f.baseType = baseType
            return f
        }
    }
    class Method(var name:String, var inputClass:ClassModel, var outputClass:ClassModel, var resultListFlag:Boolean){
        var dependency:Method? = null
        var request:RequestURI? = null
        var paged:Boolean = false
        var comment:String? = null
        fun clone():Method{
            var m = Method(name, inputClass, outputClass, resultListFlag)
            m.dependency = if(dependency != null) dependency!!.clone() else null
            m.request = if(request != null)request!!.clone() else null
            m.paged = paged
            m.comment = comment
            return m
        }
    }
    class RequestURI(var httpMethod:String?, var path:String?){

        fun clone():RequestURI{
            return RequestURI(httpMethod, path)
        }
    }
}