package com.springcodegen.idea.plugin.ctx

import com.springcodegen.idea.plugin.notify.NotificationCenter
import com.springcodegen.idea.plugin.notify.NotificationType
import com.springcodegen.idea.plugin.services.ResourceService
import com.springcodegen.idea.plugin.util.JsonUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2024/4/19
 */
object DocSettingCtx {
    var outputDir:String = ""
    var moduleDir:String = ""
    var mvnHomeDir:String = ""
    var docTypes:String = ""
    var buildDir:String = "build/doc"
    var pomFileName:String   = "doc-pom.xml"
    var configFileName:String   = "doc-config.json"


    @JvmStatic fun save(){
        var d = map()
        var s = JsonUtils.toString(d)
        FileUtils.writeStringToFile(cacheFilePath(), s, Charset.forName("UTF-8") )
    }
    @JvmStatic private fun cacheFilePath(): File {
        return File( ResourceService.getConfigDir()+"/codegen.doc.cfg")
    }
    @JvmStatic fun load() {
        var f = cacheFilePath()
        if (f.exists()){
            var s = FileUtils.readFileToString(f, Charset.forName("UTF-8"))
            var map = JsonUtils.parse(s, HashMap::class.java)
            if (map != null){
                for (x in map.entries){
                    val field = DocSettingCtx::class.java.getDeclaredField(x.key.toString())
                    if ( field != null &&  field.type.equals(String::class.java) && x.value != null && !StringUtils.isEmpty(x.value.toString())) {
                        field.set(null, x.value)
                    }
                }
            }
        }
        if(StringUtils.isEmpty(moduleDir)){
            moduleDir = AppCtx.project?.basePath!!
        }
    }
    @JvmStatic fun map():Map<String, String>{
        var d = HashMap<String, String>()
        for ( field in DocSettingCtx::class.java.declaredFields){
            if(field.type.equals(String::class.java)){
                var v = field.get(null)
                if (v != null ){
                    d.put(field.name, v as String )
                }
            }
        }
        return d
    }
}