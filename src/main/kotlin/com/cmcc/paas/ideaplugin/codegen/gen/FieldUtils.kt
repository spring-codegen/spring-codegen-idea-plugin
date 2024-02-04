package com.cmcc.paas.ideaplugin.codegen.gen

import com.intellij.util.containers.stream
import java.util.*

/**
 *
 * @author zhangyinghui
 * @date 2023/8/7
 */
object FieldUtils {
    fun propertyName(columnName: String): String {
        var a: List<String> = columnName.split("_");
        var s: StringBuilder = StringBuilder();
        for (i in 0 until a.size) {
            s.append(if (i > 0) a[i].capitalize() else a[i])
        }
        return s.toString()
    }

    fun baseTypes(): Array<String> {
        return arrayOf("Integer", "Long", "Boolean", "String", "Date", "BigDecimal", "List", "Map")
    }

    fun isCommonType(clsName: String): Boolean {
        return arrayOf("ListResult", "IdResult", "IdArg").stream().anyMatch { e -> e.equals(clsName) }
    }

    fun isBaseType(clsName: String): Boolean {
        return baseTypes().stream().anyMatch { e -> e.equals(clsName) }
    }

    fun javaType(dbType: String): String {
        if ("int2".equals(dbType)) {
            return "Boolean"
        }
        if ("int4".equals(dbType)) {
            return "Integer"
        }
        if ("int8".equals(dbType)) {
            return "Long"
        }
        if (dbType.indexOf("time") >= 0 || dbType.indexOf("date") >= 0) {
            return "Date"
        }
        return "String";
    }

    fun className(columnName: String): String {
        return propertyName(columnName).capitalize();
    }

    fun getter(columnName: String): String {
        return "get" + propertyName(columnName).capitalize();
    }

    fun setter(columnName: String): String {
        return "set" + propertyName(columnName).capitalize();
    }

    fun getRefName(v: String): String {
        if (isBaseType(v)) {
            return v.substring(0, 1).toLowerCase(Locale.getDefault())
        }
        return v.substring(0, 1).toLowerCase() + v.substring(1)
    }
}