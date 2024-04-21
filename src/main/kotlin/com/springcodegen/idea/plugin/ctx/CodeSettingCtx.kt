package com.springcodegen.idea.plugin.ctx

import com.springcodegen.idea.plugin.notify.NotificationCenter.sendMessage
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
 * @date 2023/8/16
 */
object CodeSettingCtx {
    var basePkg: String = ""
    var author: String = ""
    var modelBaseCls: String = "net.takela.common.web.model.Model"
    var searchArgBaseCls: String = "net.takela.common.web.model.PagedSearchArg"
    var modelSourceDir:String = ""
    var ctrlBaseCls: String = ""
    var ctrlSourceDir:String = ""
    var svcBaseCls: String = ""
    var svcSourceDir:String = ""
    var daoBaseCls: String = "net.takela.common.web.dao.BaseDao"
    var mybatisMapperDir:String = ""
    var apiPrefix:String = "/api/v1"
    var responseCls:String = "net.takela.common.web.model.HttpResponse"
    var module:String = ""
    var argModelSuffix:String = "Arg"
    var resultModelSuffix:String = "Result"
    var innerModels:String = ""
    var entityModelSuffix:String = ""
    set(value) {
        println("projctcfg:"+field+","+value)
        field = value
        save()
    }
    @JvmStatic fun save(){
        var d = map()
        var s = JsonUtils.toString(d)
        FileUtils.writeStringToFile(cacheFilePath(), s, Charset.forName("UTF-8") )
        sendMessage(NotificationType.CODE_SETTING_UPDATED, null)
    }
    @JvmStatic private fun cacheFilePath(): File {
        return File( ResourceService.getConfigDir()+"/codegen.settings.cfg")
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
            }
        }
        for ( x in arrayOf("ctrlSourceDir", "svcSourceDir", "modelSourceDir")){
            val field = CodeSettingCtx::class.java.getDeclaredField(x)
            var v = field.get(null)
            if (v == null || StringUtils.isEmpty(v as String) ){
                field.set(null, AppCtx.project?.basePath)
            }
        }
        if ( StringUtils.isEmpty(mybatisMapperDir) ){
            mybatisMapperDir = AppCtx.project?.basePath?:""
            var mapperDir = mybatisMapperDir + "/src/main/resources"
            if (File(mapperDir).exists()){
                mybatisMapperDir = mapperDir + "/mybatis"
            }
        }
        sendMessage(NotificationType.CODE_SETTING_UPDATED, CodeSettingCtx)
    }
    @JvmStatic fun hasReady():Boolean{
        var fieldNames = arrayOf( "basePkg", "author", "modelBaseCls", "searchArgBaseCls", "modelSourceDir", "ctrlSourceDir", "svcSourceDir", "daoBaseCls", "mybatisMapperDir", "apiPrefix", "responseCls")
        for (x in fieldNames){
            val field = this::class.java.getDeclaredField(x)
            if ( field != null ) {
                var v = field.get(this)
                if ( v == null || (v as String).isNullOrEmpty() ){
                    return false
                }
            }
        }
        return true
    }
    @JvmStatic fun map():Map<String, String>{
        var d = HashMap<String, String>()
        for ( field in CodeSettingCtx::class.java.declaredFields){
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