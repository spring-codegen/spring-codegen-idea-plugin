package com.cmcc.paas.ideaplugin.codegen.constants

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.intellij.openapi.project.Project

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object AppCtx {
    var ENV:Map<String, Any> = HashMap()
    var project:Project? = null
    var modelMaps:Map<DomainType, List<ClassModel>>? = null
    var currentTable:DBTable ? = null
}