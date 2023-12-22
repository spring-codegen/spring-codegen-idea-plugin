package com.cmcc.paas.ideaplugin.codegen.gen.define.model

import com.jetbrains.rd.util.string.printToString
import java.util.stream.Collectors

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
open class ClassModel(var className:String, var pkg:String?, var comment:String?, var fields:MutableList<Field>?) {
    constructor(className:String) : this(className, null, null, null)
    var tableName:String? = null
    var methods:MutableList<Method>? = ArrayList()
    var imports:MutableSet<String>? = HashSet()
    var baseType:Boolean = false
    var name:String? = null
    var implement:ClassModel? = null
    var extend:ClassModel? = null
    var dependency:ClassModel? = null
    open fun clone():ClassModel{
        var cls:ClassModel = this.javaClass.constructors[0].newInstance(className, pkg,comment, if (fields!= null) fields!!.stream().map { e -> e.clone() }.toList() else null) as ClassModel
        cls.methods = if(methods != null)methods!!.stream().map { e -> e.clone() }.toList() else null
        cls.imports = if(imports != null)imports!!.stream().map { e -> e }.collect(Collectors.toSet()) else null
        cls.baseType = baseType
        cls.name = name
        cls.implement = implement
        cls.extend = extend
        cls.dependency = dependency
        return cls;
    }
    class Field(val name:String, val javaType:String, val comment:String?, val notNull:Boolean?, val setter:String?, val getter:String?){
        var pkg:String? = null
        var column:String? = null
        var classType:ClassModel? = null
        var baseType:Boolean = false
        var minLen:Int = -1
        var maxLen:Int = -1
        fun clone():Field{
            var f = Field(name, javaType, comment, notNull, setter, getter)
            f.column = column
            f.classType = if(classType != null) classType!!.clone() else null
            f.baseType = baseType
            return f
        }
    }
    open class Method(var name:String, var inputClass:ClassModel, var outputClass:ClassModel, var resultListFlag:Boolean){
        var dependency:Method? = null
        var paged:Boolean = false
        var comment:String? = null
        var cls:ClassModel? = null
        var inputListFlag:Boolean? = false
        open fun clone():Method{
            var m = Method(name, inputClass, outputClass, resultListFlag)
            m.dependency = if(dependency != null) dependency!!.clone() else null
            m.paged = paged
            m.comment = comment
            return m
        }
    }
}