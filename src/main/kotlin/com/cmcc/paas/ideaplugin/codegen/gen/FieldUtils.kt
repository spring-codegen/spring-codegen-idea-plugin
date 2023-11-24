package com.cmcc.paas.ideaplugin.codegen.gen

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
    fun javaType(dbType:String):String{
        if ("int2".equals(dbType)){
            return "Boolean"
        }
        if ("int4".equals(dbType)){
            return "Integer"
        }
        if ("int8".equals(dbType)){
            return "Long"
        }
        if (dbType.indexOf("time") >= 0 || dbType.indexOf("date") >= 0){
            return "Date"
        }
        return "String";
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