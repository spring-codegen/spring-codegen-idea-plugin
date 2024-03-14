package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class CtrlClassGenerator (var clsModel:ClassModel, var projectCfg:ProjectCfg){
    private var cls: ClassOrInterfaceDeclaration? = null
    init {
        cls = ClassOrInterfaceDeclaration()
        cls!!.setName(clsModel.className)
    }
    fun gen(){
        var data = HashMap<String, Any?>();
        data["project"] = projectCfg
        data["ctrlClass"] = clsModel

        var c = TempRender.render("ctrl-class.ftl", data)
        System.out.println(c)
        System.out.println("====================")
        var cu = StaticJavaParser.parse(c)
        System.out.println(cu);

    }
}