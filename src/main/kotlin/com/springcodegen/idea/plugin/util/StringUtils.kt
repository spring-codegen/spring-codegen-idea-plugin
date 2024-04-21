package com.springcodegen.idea.plugin.util

import org.apache.commons.lang.StringUtils
import java.util.regex.Pattern

/**
 *
 * @author zhangyinghui
 * @date 2024/3/15
 */
object StringUtils {
    fun parsePlaceholders(s: String): List<String>? {
        var result:MutableList<String> = ArrayList()
        try {
            val re = "\\{([^\\{\\}]+)\\}"
            val p = Pattern.compile(re)
            val m = p.matcher(s)
            while (m.find()) {
                val ph = m.group(1)
                result.add(ph)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (result.size == 0) null else result
    }
    fun replacePlaceholders(v: String?, p: Map<String, Any?>): String? {
        if (v == null) {
            return v
        }
        var r: String = v
        for (k in p.keys) {
            if (p[k] == null) {
                continue
            }
            r = r.replace("\\{\\s*$k\\s*\\}".toRegex(), p[k].toString())
        }
        return r
    }
    fun linuxPath(path:String):String{
        return path.replace("\\","/")
    }
}