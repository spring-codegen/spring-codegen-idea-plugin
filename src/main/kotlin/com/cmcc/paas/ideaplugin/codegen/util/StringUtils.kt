package com.cmcc.paas.ideaplugin.codegen.util

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
            if (s != null) {
                val re = "\\{([^\\{\\}]+)\\}"
                val p = Pattern.compile(re)
                var ph: String
                var v: String
                val m = p.matcher(s)
                while (m.find()) {
                    val ph = m.group(1)
                    result.add(ph)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (result.size == 0) null else result
    }
}