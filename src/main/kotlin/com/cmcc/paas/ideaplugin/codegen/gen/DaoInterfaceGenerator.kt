package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.DaoClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.SvcClass
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.stmt.ThrowStmt
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.WildcardType
import com.github.javaparser.javadoc.Javadoc
import com.github.javaparser.javadoc.JavadocBlockTag
import com.github.javaparser.javadoc.description.JavadocDescription
import org.apache.commons.lang.StringUtils
import java.nio.charset.StandardCharsets
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class DaoInterfaceGenerator (module:String, var classModel: DaoClass):ClassGenerator(module){
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

        classModel.pkg = AppCtx.projectCfg?.basePkg + ".dao."+module;
        processImports(classModel)
    }

//    fun addMethod(cls:ClassOrInterfaceDeclaration, m: ClassModel.Method):MethodDeclaration{
//
//        var method = cls.addMethod(m.name, Modifier.Keyword.PUBLIC)
//        var methodDoc = Javadoc(JavadocDescription.parseText(if (m.comment == null) "" else m.comment))
//        var resultType:ClassOrInterfaceType = ClassOrInterfaceType(null, m.result?.classModel?.className)
//        if (m.result?.listTypeFlag != null && m.result?.listTypeFlag!!){
//            resultType = ClassOrInterfaceType(null, "List").setTypeArguments(resultType)
//        }
//        method?.setType(resultType)
//        //加绑定参数
//        if (m.args != null){
//            m.args.forEach {
//                var varName = if (StringUtils.isEmpty(it.refName)) it.classModel?.refName else it.refName
//                var p = Parameter()
//                    .setType(it.classModel?.className)
//                    .setName(varName)
//
//                methodDoc.addBlockTag(
//                    JavadocBlockTag(
//                        JavadocBlockTag.Type.PARAM,
//                        String.format(
//                            "%s %s",
//                            varName,
//                            if (it.comment != null) it.comment else ""
//                        )
//                    )
//                )
//                method?.addParameter(p)
//            }
//        }
//        method.setJavadocComment(methodDoc)
//        return method
//    }
    fun gen(){
        var data = HashMap<String, Any?>();
        data["project"] = AppCtx.projectCfg
        data["daoClass"] = classModel
        var a = DomainModelCtx.getModesByType(DomainType.ARG)
        if(a != null){
            for (x in a){
                if (x.className.indexOf("Search") >= 0){
                    data["searchClass"] = x
                }
            }
        }
        a = DomainModelCtx.getModesByType(DomainType.ENTITY)
        if(a != null){
            for (x in a){
                data["entityClass"] = x
            }
        }

        var c = TempRender.render("dao-interface-class.ftl", data)
        System.out.println(c)
        System.out.println("====================")
        val parserConfiguration = ParserConfiguration()
        parserConfiguration.characterEncoding = StandardCharsets.UTF_8
        StaticJavaParser.setConfiguration(parserConfiguration)
        var cu = StaticJavaParser.parse(c)
        cls = cu.getInterfaceByName(classModel.className).get()
//        classModel.methods?.forEach {
//            addMethod(cls!!, it as ClassModel.Method)
//        }
        System.out.println(cu);

    }
}