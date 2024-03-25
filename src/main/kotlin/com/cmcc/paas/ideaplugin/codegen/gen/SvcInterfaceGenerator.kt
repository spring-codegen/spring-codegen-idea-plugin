package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.SvcClass
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.WildcardType
import org.apache.commons.lang.StringUtils
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class SvcInterfaceGenerator (module:String, var classModel:SvcClass):ClassGenerator(module){
    private var cls: ClassOrInterfaceDeclaration? = null
    init {
        /**
         * 处理ctrl base class
         */
        if (AppCtx.projectCfg?.svcBaseCls != null) {
            var i = AppCtx.projectCfg?.svcBaseCls!!.lastIndexOf(".")
            if (i > 0) {
                var baseSvcCls = ClassModel(AppCtx.projectCfg?.svcBaseCls!!.substring(i + 1))
                baseSvcCls.pkg = AppCtx.projectCfg?.svcBaseCls!!.substring(0, i)
                classModel.extend = baseSvcCls
            }
        }

        classModel.pkg = AppCtx.projectCfg?.basePkg + ".svc."+module;
        cls = ClassOrInterfaceDeclaration()
        cls!!.setName(classModel.className)
        processImports(classModel)
    }
    fun addMethod(cls:ClassOrInterfaceDeclaration, m:ClassModel.Method){

        var method = cls?.addMethod(m.name, Modifier.Keyword.PUBLIC)
        var resultType = ClassOrInterfaceType(null,m.result?.classModel?.className)

    }
    fun gen(){

    }
}