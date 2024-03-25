package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.util.JsonUtils
import com.intellij.openapi.project.Project
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.io.FileUtils
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object AppCtx {
    var ENV:Map<String, Any> = HashMap()
    var projectCfg:ProjectCfg? = null
    var project:Project? = null
    var modelMaps:Map<DomainType, List<ClassModel>>? = null
    var currentTable:DBTable ? = null
}