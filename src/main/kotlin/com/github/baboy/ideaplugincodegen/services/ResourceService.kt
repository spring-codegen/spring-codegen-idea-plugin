package com.github.baboy.ideaplugincodegen.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.github.baboy.ideaplugincodegen.config.CtrlConfig
import com.intellij.openapi.diagnostic.thisLogger
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 * @author zhangyinghui
 * @date 2023/8/2
 */
object ResourceService {
    private var ctrlConfig:CtrlConfig? = null
    fun getCtrlConfig():CtrlConfig?{
        if (ctrlConfig == null){
            ctrlConfig = readYaml("/ctrl.yaml");
        }
        return ctrlConfig;
    }
    fun readYaml( fn:String): CtrlConfig?{
        this.thisLogger().info("readYaml...")
//        var s = CtrlConfig::class.java.getResourceAsStream("/ctrl.yaml")
//        var a = CtrlConfig()
//        var x = Yaml().loadAs(s, a.javaClass) as CtrlConfig
//        this.thisLogger().info("result:"+ x.toString())
//        println(x)
        var f = CtrlConfig::class.java.getResource("/ctrl.yaml").toURI()
        println(f)
//        val path = Paths.get( f)
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())

        try {
            var ret:CtrlConfig? = null
            var s = CtrlConfig::class.java.getResourceAsStream("/ctrl.yaml")
            s.use {
                ret = mapper.readValue(it, CtrlConfig::class.java)
            }
            return ret
        } catch (exception: MissingKotlinParameterException) {
            println("Could not read YAML file!")
            println(exception.message)
        }
        return null
    }
}