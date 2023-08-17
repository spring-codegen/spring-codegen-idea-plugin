package com.cmcc.paas.ideaplugin.codegen.gen.template

import com.ibm.icu.impl.data.ResourceReader
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.StringWriter
import java.net.URL
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object TempRender {
    private var configuration:Configuration? = null;
    fun getConfiguration():Configuration{
        if(configuration == null) {
            var url = TempRender::class.java.getResource("/template")
            configuration = Configuration(Configuration.getVersion())
            configuration!!.setTemplateLoader(ClassTemplateLoader(this.javaClass.classLoader, "/template"))
        }
        return configuration!!;
    }
    fun render(rs:String, data:Map<String, Any?>):String{
        var sw = StringWriter();
        var t:Template = getConfiguration().getTemplate(rs);
        t.process(data, sw)

        System.out.println(sw.toString())
        return sw.toString();
    }
    fun renderToFile(sourceDir:String, pkg:String, clsName: String, rs:String, data:Map<String, Any?>){
        var s = render(rs, data);
        var destPath = sourceDir + "/"+ pkg.replace(".", "/") + "/" + clsName+".java"
        FileUtils.writeStringToFile(File(destPath), s, Charset.forName("UTF-8"))
    }
    fun renderToFile(destPath: String, rs:String, data:Map<String, Any?>){
        var s = render(rs, data);
        FileUtils.writeStringToFile(File(destPath), s, Charset.forName("UTF-8"))
    }
}