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
import org.apache.commons.lang.StringUtils
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class CtrlClassGenerator (var clsModel:ClassModel, var svcClsModel:ClassModel, var projectCfg:ProjectCfg){
    private var cls: ClassOrInterfaceDeclaration? = null
    init {
        cls = ClassOrInterfaceDeclaration()
        cls!!.setName(clsModel.className)
    }
    fun addMethod(cls:ClassOrInterfaceDeclaration, methodCfg:CtrlClass.Method){

        var method = cls?.addMethod(methodCfg.name, Modifier.Keyword.PUBLIC)
        var resultType = ClassOrInterfaceType(null,"HttpResponse")
        if (StringUtils.isEmpty(methodCfg.outputClass.className) || methodCfg.outputClass.className.equals("-")){
            resultType.setTypeArguments(WildcardType())
        }
        else if (!methodCfg.resultListFlag){
            resultType.setTypeArguments(ClassOrInterfaceType(null,methodCfg.outputClass.className))
        }else{
            resultType.setTypeArguments(ClassOrInterfaceType(null, "ListResult").setTypeArguments(ClassOrInterfaceType(null, methodCfg.outputClass.className)))
        }

        method?.setType(resultType)
        var methodAnno = NormalAnnotationExpr(
            Name("RequestMapping"),
            NodeList(
                MemberValuePair("path",StringLiteralExpr(methodCfg.request?.path)),
                MemberValuePair("method",FieldAccessExpr(ThisExpr(Name("HttpMethod")), methodCfg.request?.httpMethod))
            )
        )
        method?.addAnnotation(methodAnno)

        //加路径参数
        var params = ArrayList<Parameter>()
        if (methodCfg.request?.pathVars != null){
            methodCfg.request?.pathVars?.forEach {
                var p = Parameter()
                    .setType(it.javaType)
                    .setName(it.name)
                    .addSingleMemberAnnotation("PathVariable", "\""+it.name+"\"")
                method?.addParameter(p)
                params.add(p)
             }
        }
        //加绑定参数
        if (StringUtils.isNotEmpty( methodCfg.inputClass.className) && !methodCfg.inputClass.className.equals("-")){
            var p = Parameter()
                .setType(methodCfg.inputClass.className)
                .setName(methodCfg.inputClass.refName)
            method?.addParameter(p)
            params.add(p)
            method?.addParameter(Parameter().setType("BindingResult").setName("br"))
        }
        var destCls = methodCfg.dependency?.inputClass
        var callArgs = destCls?.refName;

        var blockStmt = BlockStmt()
        //如果目标类型是基本类型，
        if ( !destCls!!.isBaseType() ){
            if ( !destCls!!.className.equals( methodCfg.inputClass!!.className) && !methodCfg!!.inputClass.isBaseType()){
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
        data["ctrlClass"] = clsModel

        var c = TempRender.render("ctrl-class.ftl", data)
        System.out.println(c)
        System.out.println("====================")
        var cu = StaticJavaParser.parse(c)
        cls = cu.getClassByName(clsModel.className).get()
        clsModel.methods?.forEach {
            addMethod(cls!!, it as CtrlClass.Method)
        }
        System.out.println(cu);

    }
}