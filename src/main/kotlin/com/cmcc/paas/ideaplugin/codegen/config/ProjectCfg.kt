package com.cmcc.paas.ideaplugin.codegen.config

import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx.project
import com.cmcc.paas.ideaplugin.codegen.util.JsonUtils
import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2023/8/16
 */
class ProjectCfg {
    var basePkg: String? = null
    var author: String? = null
    var modelBaseCls: String? = null
    var ctrlBaseCls: String? = null
    var sourceDir:String? = null
    var mybatisMapperDir:String? = null

    fun save(){
        var s = JsonUtils.toString(this)
        FileUtils.writeStringToFile(cacheFile(), s, Charset.forName("UTF-8") )
    }
    private fun cacheFile(): File {
        return File( project!!.basePath+"/.idea/.codegen.settings.cfg")
    }
    fun load(): ProjectCfg? {
        var f = cacheFile()
        if (!f.exists()){
            return null
        }
        var s = FileUtils.readFileToString(f, Charset.forName("UTF-8"))
        var cfg = JsonUtils.parse(s, ProjectCfg::class.java)
        BeanUtils.copyProperties(this, cfg)
        return this;
    }
}