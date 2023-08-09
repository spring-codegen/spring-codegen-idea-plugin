package com.github.baboy.ideaplugincodegen.define.model

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class ModelDefine(val className:String, val pkg:String, val comment:String?, val fields:List<Field>) {
    class Field(val name:String, val javaType:String, val comment:String?, val notNull:Boolean?, val setter:String, val getter:String){
    }
}