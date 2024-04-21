package com.springcodegen.idea.plugin.template

import com.springcodegen.idea.plugin.services.ResourceService
import com.springcodegen.idea.plugin.ui.tookit.MessageBoxUtils
import freemarker.cache.ClassTemplateLoader
import freemarker.cache.FileTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.StringWriter
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object TempRender {
    var TEMP_MODEL_CLASS = "code/model.ftl"
    /**
     * controller class template
     */
    var TEMP_CTRL_CLASS = "code/ctrl-class.ftl"
    var TEMP_SVC_CLASS = "code/svc-class-impl.ftl"
    var TEMP_SVC_INTERFACE = "code/svc-interface-class.ftl"
    var TEMP_DAO_INTERFACE = "code/dao-interface-class.ftl"
    var TEMP_DAO_MAPPER = "code/dao-mapper.ftl"
    var TEMP_DOC_CONFIG = "doc/doc-config.ftl"
    var TEMP_DOC_POM = "doc/doc-pom.ftl"
    private var configuration:Configuration? = null;
    fun getConfiguration():Configuration{
        if(configuration == null) {
//            var url = TempRender::class.java.getResource("/template")
            configuration = Configuration(Configuration.getVersion())
            configuration!!.templateLoader = FileTemplateLoader(File(ResourceService.getConfigDir()+"/template"));//ClassTemplateLoader(this.javaClass.classLoader, "/template")
            configuration!!.defaultEncoding = "UTF-8";
        }
        return configuration!!;
    }
    fun render(rs:String, data:Map<String, Any?>):String{
        var sw = StringWriter();
        var t:Template = getConfiguration().getTemplate(rs);
        t.process(data, sw)

//        System.out.println(sw.toString())
        return sw.toString();
    }
    fun renderToFile(sourceDir:String, pkg:String, clsName: String, rs:String, data:Map<String, Any?>){
        var s = render(rs, data);
        var destPath = sourceDir + "/"+ pkg.replace(".", "/") + "/" + clsName+".java"
        try {
            FileUtils.writeStringToFile(File(destPath), s, Charset.forName("UTF-8"))
        }catch (e:Exception){
            MessageBoxUtils.showMessageAndFadeout(e.message)
            throw e
        }
    }
    fun renderToFile(destPath: String, rs:String, data:Map<String, Any?>){
        var s = render(rs, data);
        try {
            FileUtils.writeStringToFile(File(destPath), s, Charset.forName("UTF-8"))
        }catch (e:Exception){
            MessageBoxUtils.showMessageAndFadeout(e.message)
            throw e
        }
    }
}