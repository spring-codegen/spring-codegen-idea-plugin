package com.springcodegen.idea.plugin.util


import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter


/**
 *
 * @author zhangyinghui
 * @date 2023/8/2
 */
object JsonUtils {
 fun <T> parse(s:String, type: Class<T>): T?{
  var obj: T? = null

  try {
   obj = JSON.parseObject(s, type)
  } catch (var5: Exception) {
   var5.printStackTrace()
  }
  return obj
 }
 fun toString(obj: Any?): String? {
  return JSON.toJSONString(obj, JSONWriter.Feature.PrettyFormat, JSONWriter.Feature.WriteMapNullValue)
 }
}