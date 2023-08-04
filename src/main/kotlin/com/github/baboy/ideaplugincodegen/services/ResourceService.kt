package com.github.baboy.ideaplugincodegen.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.github.baboy.ideaplugincodegen.config.CodeCfg
import com.intellij.openapi.diagnostic.thisLogger

/**
 *
 * @author zhangyinghui
 * @date 2023/8/2
 */
object ResourceService {
    private var codeCfg:CodeCfg? = null
    fun getCodeCfg():CodeCfg?{
        if (codeCfg == null){
            codeCfg = readYaml("/code-cfg.yaml");
        }
        return codeCfg;
    }
    fun readYaml( fn:String): CodeCfg?{
        this.thisLogger().info("readYaml...")
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())

        try {
            var ret:CodeCfg? = null
            var s = CodeCfg::class.java.getResourceAsStream(fn)
            s.use {
                ret = mapper.readValue(it, CodeCfg::class.java)
            }
            return ret
        } catch (exception: MissingKotlinParameterException) {
            println("Could not read YAML file!")
            println(exception.message)
        }
        return null
    }
}