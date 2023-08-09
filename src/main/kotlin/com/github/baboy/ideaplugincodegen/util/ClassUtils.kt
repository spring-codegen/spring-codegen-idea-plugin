package com.github.baboy.ideaplugincodegen.util

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 *
 * @author zhangyinghui
 * @date 2023/8/4
 */
object ClassUtils {
    fun fieldValue(obj:Any, fieldName:String):Any? {
        var field: Field = obj.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        val v = field.get(obj)
        return v;
    }
    fun call(obj:Any, methodName:String, args:Array<Any>):Any?{
        val getMethod: Method = obj.javaClass.getMethod(methodName)
        val v = getMethod.invoke(obj, args)
        return v;
    }
}