package com.github.baboy.ideaplugincodegen.gen.define.model

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class ClassModel(val className:String, var pkg:String, val comment:String?, var fields:MutableList<Field>?) {
    var tableName:String? = null
    var methods:MutableList<Method>? = ArrayList()
    var imports:MutableSet<String>? = HashSet()
    var isBaseType:Boolean = false
    class Field(val name:String, val javaType:String, val comment:String?, val notNull:Boolean?, val setter:String, val getter:String){
        var pkg:String? = null
        var column:String? = null
        var classType:ClassModel? = null
        var isBaseType:Boolean = false
    }
    class Method(val name:String, val inputClass:ClassModel, val outputClass:ClassModel, val resultListFlag:Boolean){
        var dependency:Method? = null
    }
}