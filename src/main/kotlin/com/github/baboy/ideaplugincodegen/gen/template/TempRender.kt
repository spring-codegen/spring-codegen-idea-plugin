package com.github.baboy.ideaplugincodegen.gen.template

import com.ibm.icu.impl.data.ResourceReader
import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.io.StringWriter
import java.net.URL

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
    fun render(rs:String, data:Map<String, Any?>){
        var sw = StringWriter();
        var t:Template = getConfiguration().getTemplate(rs);
        t.process(data, sw)
        System.out.println(sw.toString())
    }
}