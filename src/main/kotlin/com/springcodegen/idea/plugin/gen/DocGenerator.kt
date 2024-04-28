package com.springcodegen.idea.plugin.gen

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.roots.ProjectRootManager
import com.springcodegen.idea.plugin.ctx.AppCtx
import com.springcodegen.idea.plugin.ctx.CodeSettingCtx
import com.springcodegen.idea.plugin.ctx.DocSettingCtx
import com.springcodegen.idea.plugin.template.TempRender
import org.apache.commons.io.FileUtils
import org.apache.maven.shared.invoker.*
import java.io.File
import java.nio.charset.Charset

/**
 *
 * @author zhangyinghui
 * @date 2024/4/19
 */
object DocGenerator {
    @JvmStatic fun pomFile():String{
        return DocSettingCtx.moduleDir + "/" + DocSettingCtx.buildDir + "/" + DocSettingCtx.pomFileName
    }
    @JvmStatic fun configFile():String{
        return DocSettingCtx.moduleDir + "/" + DocSettingCtx.buildDir +"/" + DocSettingCtx.configFileName
    }
    @JvmStatic fun prepare(){
        //生成samrt-doc config文件
        var data = HashMap<String, String>()
        data["projectName"] = AppCtx.project?.name!!
        data["outputDir"] =  DocSettingCtx.outputDir
        data["moduleDir"] =  DocSettingCtx.moduleDir
        data["basePkg"] = CodeSettingCtx.basePkg
        var configContent = TempRender.render(TempRender.TEMP_DOC_CONFIG, data)
        var buildDir = File(DocSettingCtx.moduleDir + "/" + DocSettingCtx.buildDir )
        if ( !buildDir.exists() ){
            buildDir.mkdirs()
        }
        FileUtils.writeStringToFile( File( configFile() ), configContent, Charset.forName("UTF-8"))
        //生成pom文件
        var pomContent = TempRender.render(TempRender.TEMP_DOC_POM, data)
        FileUtils.writeStringToFile(File(pomFile()), pomContent, Charset.forName("UTF-8"))
    }
    @JvmStatic fun gen( docTypes:List<String>, outputHandler: InvocationOutputHandler, errorHandler: InvocationOutputHandler):Boolean{
        prepare()

        var projectManager: ProjectRootManager? = AppCtx.project?.let { ProjectRootManager.getInstance(it) } ?: return false
        var sdk = projectManager?.projectSdk?: return false;
        var invocationRequest:InvocationRequest = DefaultInvocationRequest()
        invocationRequest.baseDirectory = File( DocSettingCtx.moduleDir )
        // 设置java home
        invocationRequest.javaHome = File(sdk.homePath);
        // pom文件的位置
        invocationRequest.pomFile = File(pomFile());
        // maven命令
        invocationRequest.goals = docTypes.map { "smart-doc:$it" };

        var invoker = DefaultInvoker();
        // 设置maven_home
        invoker.mavenHome = File(DocSettingCtx.mvnHomeDir);

        invoker.setLogger(PrintStreamLogger(System.err, InvokerLogger.DEBUG));
        invoker.setOutputHandler(outputHandler)
        invoker.setErrorHandler(errorHandler)
        // 控制台打印日志
//        invoker.setOutputHandler(System.out::println);
        try {
            invoker.execute(invocationRequest);
        } catch (  e: MavenInvocationException) {
            e.printStackTrace();
            errorHandler.consumeLine(e.localizedMessage)
            return false
        }
        return true;
    }
}