package com.cmcc.paas.ideaplugin.codegen.config

import com.cmcc.paas.ideaplugin.codegen.services.ResourceService
import com.cmcc.paas.ideaplugin.codegen.util.JsonUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2023/8/16
 */
object DBSettingCtx {
    var dbName:String? = null
    var schema:String? = null
    var host:String? = null
    var port:Int? = null
    var user:String? = null
    var pwd:String? = null
    var driverType:String? = "postgresql"

     @JvmStatic fun save(){
        var s = JsonUtils.toString(this)
        FileUtils.writeStringToFile(cacheFile(), s,Charset.forName("UTF-8") )
    }
    @JvmStatic private fun cacheFile():File{
        return File( ResourceService.getConfigDir()+"/.codegen.db.cfg")
    }
    @JvmStatic fun load() {
        var f = cacheFile()
        if (!f.exists()){
            return
        }
        var s = FileUtils.readFileToString(f, Charset.forName("UTF-8"))
        var map = JsonUtils.parse(s, HashMap::class.java)
        if (map != null){
            for (x in map.entries){
                val field = this::class.java.getDeclaredField(x.key.toString())
                if ( field != null &&  (field.type.equals(String::class.java) || field.type.equals(Integer::class.java) || field.type.equals(Int::class.java) || field.type.equals(Long::class.java)) && x.value != null && !StringUtils.isEmpty(x.value.toString())) {
                    field.set(null, x.value)
                }
            }
        }
    }
}