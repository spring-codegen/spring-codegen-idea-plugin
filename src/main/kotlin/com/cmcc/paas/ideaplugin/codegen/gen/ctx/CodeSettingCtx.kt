package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx.project
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter.sendMessage
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationType
import com.cmcc.paas.ideaplugin.codegen.util.JsonUtils
import io.ktor.util.reflect.*
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2023/8/16
 */
object CodeSettingCtx {
    var basePkg: String? = null
    var author: String? = null
    var modelBaseCls: String? = "com.cmit.paas.common.web.model.Model"
    var searchArgBaseCls: String? = null
    var modelSourceDir:String? = null
    var ctrlBaseCls: String? = null
    var ctrlSourceDir:String? = null
    var svcBaseCls: String? = null
    var svcSourceDir:String? = null
    var daoBaseCls: String? = null
    var mybatisMapperDir:String? = null
    var apiPrefix:String? = "/api/v1"
    var responseCls:String? = "com.cmit.paas.common.web.model.HttpResponse"
    var module:String? = ""
    set(value) {
        println("projctcfg:"+field+","+value)
        field = value
        save()
    }
    @JvmStatic fun save(){
        var d = HashMap<String, Any>()
        for ( field in CodeSettingCtx::class.java.declaredFields){
            if(field.type.equals(String::class.java)){
                val v = field.get(null)
                if (v != null && !StringUtils.isEmpty(v.toString())){
                    d.put(field.name, v)
                }
            }
        }
        var s = JsonUtils.toString(d)
        FileUtils.writeStringToFile(cacheFilePath(), s, Charset.forName("UTF-8") )
    }
    private @JvmStatic fun cacheFilePath(): File {
        return File( project!!.basePath+"/.idea/.codegen.settings.cfg")
    }
    @JvmStatic fun load() {
        var f = cacheFilePath()
        if (f.exists()){
            var s = FileUtils.readFileToString(f, Charset.forName("UTF-8"))
            var map = JsonUtils.parse(s, HashMap::class.java)
            if (map != null){
                for (x in map.entries){
                    val field = CodeSettingCtx::class.java.getDeclaredField(x.key.toString())
                    if ( field != null &&  field.type.equals(String::class.java) && x.value != null && !StringUtils.isEmpty(x.value.toString())) {
                        field.set(null, x.value)
                    }
                }
                sendMessage(NotificationType.CODE_SETTING_UPDATED, CodeSettingCtx)
            }
        }
    }

}