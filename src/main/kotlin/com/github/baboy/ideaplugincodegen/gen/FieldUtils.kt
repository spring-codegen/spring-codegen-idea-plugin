package com.github.baboy.ideaplugincodegen.gen

/**
 *
 * @author zhangyinghui
 * @date 2023/8/7
 */
object FieldUtils {
    fun propertyName(columnName:String): String{
        var a: List<String> = columnName.split("_");
        var s:StringBuilder = StringBuilder();
        for (i in 0 until a.size ){
            s.append( if ( i > 0) a[i].capitalize() else a[i])
        }
        return s.toString()
    }
    fun className(columnName:String): String{
        return propertyName(columnName).capitalize();
    }
    fun getter(columnName:String): String{
        return "get"+ propertyName(columnName).capitalize();
    }
    fun setter(columnName:String): String{
        return "set"+ propertyName(columnName).capitalize();
    }
}