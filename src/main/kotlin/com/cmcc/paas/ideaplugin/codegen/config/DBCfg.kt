package com.cmcc.paas.ideaplugin.codegen.config

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
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
class DBCfg {
    companion object{
        var x:String? = null
    }
    var dbName:String? = null
    var schema:String? = null
    var host:String? = null
    var port:Int? = null
    var user:String? = null
    var pwd:String? = null
    var driverType:String? = "postgresql"

     fun save(){
        var s = JsonUtils.toString(this)
        FileUtils.writeStringToFile(cacheFile(), s,Charset.forName("UTF-8") )
    }
    private fun cacheFile():File{
        return File( AppCtx.project!!.basePath+"/.idea/.codegen.db.cfg")
    }
    fun load(): DBCfg? {
        var f = cacheFile()
        if (!f.exists()){
            return null
        }
        var s = FileUtils.readFileToString(f, Charset.forName("UTF-8"))
        var dbCfg = JsonUtils.parse(s, DBCfg::class.java)
        BeanUtils.copyProperties(this, dbCfg)
        return this;
    }
}