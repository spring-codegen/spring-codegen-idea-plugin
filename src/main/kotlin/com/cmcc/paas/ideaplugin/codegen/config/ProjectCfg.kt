package com.cmcc.paas.ideaplugin.codegen.config

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx.project
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
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
    var modelSourceDir:String? = null
    var ctrlBaseCls: String? = null
    var ctrlSourceDir:String? = null
    var svcBaseCls: String? = null
    var svcSourceDir:String? = null
    var mybatisMapperDir:String? = null
    var apiPrefix:String? = "/api/v1"

    companion object {

        @JvmStatic
        fun save(){
            var s = JsonUtils.toString(this)
            FileUtils.writeStringToFile(cacheFile(), s, Charset.forName("UTF-8") )
        }
        private fun cacheFile(): File {
            return File( project!!.basePath+"/.idea/.codegen.settings.cfg")
        }
        @JvmStatic
        fun load() {
            var f = cacheFile()
            var cfg:ProjectCfg? = null
            if (f.exists()){
                var s = FileUtils.readFileToString(f, Charset.forName("UTF-8"))
                cfg = JsonUtils.parse(s, ProjectCfg::class.java)
            }
            if (cfg == null){
                cfg = ProjectCfg()
            }
            AppCtx.projectCfg = cfg
        }
    }
}