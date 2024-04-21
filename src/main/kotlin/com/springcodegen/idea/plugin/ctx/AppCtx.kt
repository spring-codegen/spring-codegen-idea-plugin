package com.springcodegen.idea.plugin.ctx

import com.springcodegen.idea.plugin.db.model.DBTable
import com.intellij.openapi.project.Project
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.notify.NotificationCenter
import com.springcodegen.idea.plugin.notify.NotificationType
import org.apache.commons.lang3.StringUtils

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object AppCtx {
    @JvmStatic var ENV:Map<String, Any> = HashMap()
    @JvmStatic var project:Project? = null
    @JvmStatic var currentTable:DBTable ? = null
    @JvmStatic var tables: ArrayList<DBTable> = ArrayList()

    @JvmStatic fun getEnvParams():Map<String, Any>{
        var p = HashMap<String, Any>(ENV)
        var d = CodeSettingCtx.map()
        p.putAll(d)
        return p
    }
}