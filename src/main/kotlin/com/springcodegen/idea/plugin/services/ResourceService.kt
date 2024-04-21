package com.springcodegen.idea.plugin.services

import com.springcodegen.idea.plugin.config.CodeCfg
import com.springcodegen.idea.plugin.ctx.AppCtx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.io.isDirectory
import java.io.File
import java.net.URL
import java.nio.file.*


/**
 *
 * @author zhangyinghui
 * @date 2023/8/2
 */
object ResourceService {
    fun getConfigDir():String{
        var dir = AppCtx.project?.basePath + "/.idea/.codegen"
        var f = File(dir)
        if (!f.exists()){
            f.mkdirs()
        }
        return dir
    }
    private fun copyFile(srcPath:Path, destFile:String){
        if (srcPath.isDirectory()){
            var df = Paths.get(destFile).toFile()
            if (!df.exists()){
                df.mkdirs()
            }
            Files.list(srcPath).forEach {
                copyFile(it, destFile+"/"+it.fileName)
            }
        }
        else{
            var f = Paths.get(destFile).toFile()
            if (!f.exists()){
                if ( !f.parentFile.exists() ){
                    f.parentFile.mkdirs()
                }
            }
            if (!f.exists()){
                Files.copy(srcPath, Paths.get(destFile))
            }
        }
    }
    fun prepareConfigFiles(){
        var fileNames = arrayListOf<String>("/code-cfg.yaml", "/template")
//            FileSystems.newFileSystem(r?.toURI(), HashMap<String,Any>());
        var fileSystem: FileSystem? = null
        for (fn in fileNames){
            var r: URL? = this::class.java.getResource(fn) ?: continue
            println("resource file:"+fn+","+r?.toURI())
            if (fileSystem == null) {
                fileSystem = FileSystems.newFileSystem(r?.toURI(), HashMap<String, Any>());
            }
            var srcFiePath = Paths.get(r?.toURI())
            var destFp = getConfigDir() + "/"+fn
            copyFile(srcFiePath, destFp)
        }
    }
    fun readCodeCfgYaml( fn:String): CodeCfg?{
//        this.thisLogger().info("readYaml:"+CodeCfg::class.java.getResource(fn))
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())

        try {
            var f = File(getConfigDir()+"/"+fn)
            var ret:CodeCfg? = mapper.readValue(f, CodeCfg::class.java)
            return ret
        } catch (exception: MissingKotlinParameterException) {
            println("Could not read YAML file!")
            println(exception.message)
        }
        return null
    }
}