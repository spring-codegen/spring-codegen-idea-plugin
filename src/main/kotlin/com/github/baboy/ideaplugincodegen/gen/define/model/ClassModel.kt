package com.github.baboy.ideaplugincodegen.gen.define.model

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class ClassModel(val className:String, val pkg:String, val comment:String?, val fields:List<Field>?) {
    var tableName:String? = null
    var methods:List<Method>? = null
    var imports:List<String>? = null
    class Field(val name:String, val javaType:String, val comment:String?, val notNull:Boolean?, val setter:String, val getter:String){
        var column:String? = null
    }
    class Method(val name:String, val inputClass:ClassModel, val outputClass:ClassModel, val resultListFlag:Boolean){
        var dependency:Method? = null
    }
}