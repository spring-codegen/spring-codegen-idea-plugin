package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.cmcc.paas.ideaplugin.codegen.setting.CtrlSetting
import com.github.javaparser.JavaParser
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.WildcardType
import com.intellij.buildsystem.model.BuildDependency
import org.apache.commons.lang.StringUtils
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class CtrlClassGenerator (module:String, var classModel:CtrlClass, projectCfg:ProjectCfg):ClassGenerator(module, projectCfg){
    private var cls: ClassOrInterfaceDeclaration? = null
    init {

        /**
         * 处理ctrl base class
         */
        if (projectCfg.ctrlBaseCls != null) {
            var i = projectCfg.ctrlBaseCls!!.lastIndexOf(".")
            if (i > 0) {
                var baseCtrlCls = ClassModel(projectCfg.ctrlBaseCls!!.substring(i + 1))
                baseCtrlCls.pkg = projectCfg.ctrlBaseCls!!.substring(0, i)
                classModel.extend = baseCtrlCls
            }
        }

        classModel.pkg = projectCfg.basePkg + ".controller."+module;
        cls = ClassOrInterfaceDeclaration()
        cls!!.setName(classModel.className)
        /**
         * 处理路径参数
         */
        classModel.methods!!.forEach {
            var m = it as CtrlClass.Method
            if (!StringUtils.isEmpty(m.request!!.path)){
                var phs = com.cmcc.paas.ideaplugin.codegen.util.StringUtils.parsePlaceholders(m.request!!.path)
                if( phs != null) {
                    for (ph in phs){
                        if (m.dependency != null && m.dependency!!.args != null){
                            for (arg in m.dependency!!.args ){
                                var field = arg.classModel!!.fields!!.find { e2 -> e2.name.equals(ph, true) }
                                if (field != null){
                                    var c = ClassModel(field.javaType)
                                    c.refName = ph
                                    var phArg = ClassModel.MethodArg(c, ph)
                                    phArg.isPathVar = true
                                    m.args.add(0, phArg)
                                }
                            }
                        }
                    }
                }
            }
        }
        processImports(classModel)
    }
    fun addMethod(cls:ClassOrInterfaceDeclaration, m:CtrlClass.Method){

        var method = cls?.addMethod(m.name, Modifier.Keyword.PUBLIC)
        var resultType = ClassOrInterfaceType(null,"HttpResponse")
        if (m.result == null ){
            resultType.setTypeArguments(WildcardType())
        }
        else if (m.result?.listTypeFlag != null && m.result?.listTypeFlag!!){
            resultType.setTypeArguments(
                    ClassOrInterfaceType(null, "ListResult")
                            .setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className))
            )
        }else{
            resultType.setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className))
        }

        method?.setType(resultType)
        var methodAnno = NormalAnnotationExpr(
            Name("RequestMapping"),
            NodeList(
                MemberValuePair("path",StringLiteralExpr(m.request?.path)),
                MemberValuePair("method",FieldAccessExpr(ThisExpr(Name("HttpMethod")), m.request?.httpMethod))
            )
        )
        method?.addAnnotation(methodAnno)

        //加路径参数
        var params = ArrayList<Parameter>()
        if (m.request?.pathVars != null){
            m.request?.pathVars?.forEach {
                var p = Parameter()
                    .setType(it.javaType)
                    .setName(it.name)
                    .addSingleMemberAnnotation("PathVariable", "\""+it.name+"\"")
                method?.addParameter(p)
                params.add(p)
             }
        }
        //加绑定参数
        if (m.args != null){
            var br:Parameter? = null
            m.args.forEach {
                var varName = if (StringUtils.isEmpty(it.refName)) it.refName else it.classModel?.refName
                var p = Parameter()
                        .setType(it.classModel?.className)
                        .setName(varName)
                if (it.isPathVar){
                    p.addSingleMemberAnnotation("PathVariable", "\""+varName+"\"")
                }
                if ( ClassModel.isBaseType(it.classModel?.className!!)){
                    br = Parameter().setType("BindingResult").setName("br")
                }
                method?.addParameter(p)
            }
            if (br != null){
                method?.addParameter(br)
            }
        }
        var destCls:ClassModel.MethodArg? = null
        if (m.dependency != null && m.dependency?.args != null && m.dependency?.args!!.size > 0){
            destCls = m.dependency?.args!![0]
        }
        var callArgs = destCls?.refName;

        var blockStmt = BlockStmt()
        //如果目标类型是基本类型，
        if ( destCls != null && ClassModel.isBaseType( destCls!!.classModel?.className!!  ) ){
            if ( !destCls!!.classModel?.className.equals( m.inputClass!!.className) && !methodCfg!!.inputClass.isBaseType()){
                //调用copyTo
                var callExp = MethodCallExpr(NameExpr(methodCfg.inputClass!!.refName), "copyTo");
                callExp.addArgument(FieldAccessExpr(ThisExpr(Name(destCls.className)), "class"));
                var varDeclarator = VariableDeclarator(ClassOrInterfaceType(null,destCls.className), destCls.refName, callExp)
                blockStmt.addStatement(VariableDeclarationExpr(varDeclarator))
                callArgs = destCls.refName
            }
        }
        var methodCallExpr = MethodCallExpr(NameExpr(svcClsModel.refName), methodCfg.dependency?.name);

        methodCallExpr.addArgument(callArgs);

        blockStmt.addStatement(methodCallExpr);
        method?.setBody(blockStmt)

    }
    fun gen(){
        var data = HashMap<String, Any?>();
        data["project"] = projectCfg
        data["ctrlClass"] = classModel

        var c = TempRender.render("ctrl-class.ftl", data)
        System.out.println(c)
        System.out.println("====================")
        var cu = StaticJavaParser.parse(c)
        cls = cu.getClassByName(classModel.className).get()
        classModel.methods?.forEach {
            addMethod(cls!!, it as CtrlClass.Method)
        }
        System.out.println(cu);

    }
}