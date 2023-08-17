package com.cmcc.paas.ideaplugin.codegen.constants

import com.intellij.openapi.project.Project

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object AppCtx {
    var ENV:Map<String, Any> = HashMap()
    var project:Project? = null
}