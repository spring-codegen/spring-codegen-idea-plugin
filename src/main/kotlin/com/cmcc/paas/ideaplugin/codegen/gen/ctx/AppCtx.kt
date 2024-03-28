package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable
import com.intellij.openapi.project.Project

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object AppCtx {
    var ENV:Map<String, Any> = HashMap()
    var project:Project? = null
    var currentTable:DBTable ? = null
}