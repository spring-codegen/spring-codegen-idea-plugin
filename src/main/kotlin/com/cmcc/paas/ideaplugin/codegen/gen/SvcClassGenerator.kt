package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.SvcClass
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.*
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

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class SvcClassGenerator (module:String, var classModel: SvcClass):ClassGenerator(module){
    private var cls: ClassOrInterfaceDeclaration? = null
    init {

        classModel.pkg = AppCtx.projectCfg?.basePkg + ".svc."+module;
        processImports(classModel)
    }
    fun createMethod(m: ClassModel.Method):MethodDeclaration{

        var method = MethodDeclaration().setName(m.name).addModifier( Modifier.Keyword.PUBLIC)
        var methodDoc = Javadoc(JavadocDescription.parseText(if (m.comment == null) "" else m.comment))
        var blockStmt = BlockStmt()
        var resultType:ClassOrInterfaceType = ClassOrInterfaceType(null, m.result?.classModel?.className)
        if (m.result?.listTypeFlag != null && m.result?.listTypeFlag!!){
            resultType = ClassOrInterfaceType(null, "List").setTypeArguments(resultType)
        }
        method?.setType(resultType)
        //加绑定参数
        if (m.args != null){
            m.args.forEach {
                var varName = if (StringUtils.isEmpty(it.refName)) it.classModel?.refName else it.refName
                var p = Parameter()
                        .setType(it.classModel?.className)
                        .setName(varName)
                
                methodDoc.addBlockTag(
                    JavadocBlockTag(
                        JavadocBlockTag.Type.PARAM,
                        String.format(
                            "%s %s",
                            varName,
                            if (it.comment != null) it.comment else ""
                        )
                    )
                )
                method?.addParameter(p)
            }
        }
        method.setJavadocComment(methodDoc)
        var callArg: ClassModel.MethodArg? = null
        if (m.dependency != null && m.dependency?.args != null && m.dependency?.args!!.size > 0){
            callArg = m.dependency?.args!![0]
        }
        var callArgExpr: Expression? = null
        if (callArg != null){
            callArgExpr = NameExpr( callArg.classModel?.refName )
        }

        if ( callArg != null ){
            //如果目标类型是基本类型，
            if ( ClassModel.isBaseType( callArg!!.classModel?.className!!  )){

                //目标类型
                for (inputArg in m.args){
                    //都是基本类型
                    if (inputArg.classModel?.className.equals(callArg.classModel?.className)){
                        callArgExpr = NameExpr( inputArg.classModel?.refName )
                        break;
                    }
                    //需要调用getter
                    if (!ClassModel.isBaseType(inputArg.classModel?.className!!)){
                        for (f in inputArg?.classModel?.fields!!){
                            if (f.name.equals(callArg.classModel?.refName)){
                                //调用get方法
                                callArgExpr = MethodCallExpr(NameExpr(inputArg.classModel!!.refName), f.getter)
                            }
                        }
                    }
                }
            }else{
                //往复合类型转换
                for (inputArg in m.args){
                    //类型相同
                    if (inputArg.classModel?.className.equals(callArg.classModel?.className)){
                        callArgExpr = NameExpr( inputArg.classModel?.refName )
                        break;
                    }
                    //需要调用转换
                    if (!ClassModel.isBaseType(inputArg.classModel?.className!!)){
                        //调用copyTo
                        var callExp = MethodCallExpr(NameExpr(inputArg.classModel!!.refName), "copyTo");
                        callExp.addArgument(FieldAccessExpr(NameExpr(callArg.classModel?.className), "class"));
                        var varDeclarator = VariableDeclarator(ClassOrInterfaceType(null,callArg.classModel?.className), callArg.classModel?.refName, callExp)
                        blockStmt.addStatement(VariableDeclarationExpr(varDeclarator))
                        callArgExpr =  NameExpr( callArg.classModel?.refName )
                        for (x in m.args){
                            if (ClassModel.isBaseType(x.classModel?.className!!)){
                                for (f in inputArg.classModel?.fields!!){
                                    if (f.name.equals(x.classModel?.refName, true)){
                                        callExp = MethodCallExpr(NameExpr(callArg.classModel?.refName), f.setter);
                                        callExp.addArgument(NameExpr(x.classModel?.refName))
                                        blockStmt.addStatement(callExp)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        var dependencyCallExpr = MethodCallExpr(NameExpr(classModel.dependency?.refName), m.dependency?.name);
        dependencyCallExpr.addArgument(callArgExpr);
        var callReturn = m.dependency?.result
        var resultDataVarName:String? = null
        if (callReturn == null){
            blockStmt.addStatement(dependencyCallExpr);
        }else{
            var resultDeclar:VariableDeclarator? = null
            resultDataVarName = callReturn.classModel?.refName!!
            var itemType = ClassOrInterfaceType(null,callReturn.classModel?.className)
            if (m.result?.listTypeFlag != null && m.result?.listTypeFlag!!){
                blockStmt.addStatement( MethodCallExpr(NameExpr("PageHelper"), "startPage"))
                resultDataVarName = "items"
                var dataType = ClassOrInterfaceType(null, "List")
                    .setTypeArguments(itemType)
                resultDeclar = VariableDeclarator(
                    dataType,
                    resultDataVarName,
                    dependencyCallExpr
                )
            }else {
                resultDeclar = VariableDeclarator(
                    ClassOrInterfaceType(null, callReturn.classModel?.className),
                    resultDataVarName,
                    dependencyCallExpr
                )
            }
            blockStmt.addStatement(VariableDeclarationExpr(resultDeclar))
            if (m.result != null && m.result?.outputPaged!!){
                var listResultDeclar =  VariableDeclarator(
                    ClassOrInterfaceType(null, "ListResult")
                        .setTypeArguments(ClassOrInterfaceType(null, callReturn.classModel?.className)),
                    resultDataVarName
                )
                listResultDeclar.setInitializer("new ListResult(((Page) "+resultDataVarName+").totalCount,((Page) "+resultDataVarName+").pageNum), "+resultDataVarName+")")
                blockStmt.addStatement(VariableDeclarationExpr(listResultDeclar))
            }
        }

        var resDeclar =  VariableDeclarator( resType,"res" )
        resDeclar.setInitializer("HttpResponse.success()")
        blockStmt.addStatement(VariableDeclarationExpr(resDeclar))
        if (resultDataVarName != null && m.result != null) {
            blockStmt.addStatement(
                MethodCallExpr(
                    NameExpr("res"),
                    "setData"
                ).addArgument(NameExpr(resultDataVarName))
            )
        }
        blockStmt.addStatement(ReturnStmt("res"))

        method?.setBody(blockStmt)
        return method
    }
    fun gen(){
        var data = HashMap<String, Any?>();
        data["project"] = AppCtx.projectCfg
        data["ctrlClass"] = classModel

        var c = TempRender.render("ctrl-class.ftl", data)
        System.out.println(c)
        System.out.println("====================")
        val parserConfiguration = ParserConfiguration()
        parserConfiguration.characterEncoding = StandardCharsets.UTF_8
        StaticJavaParser.setConfiguration(parserConfiguration)
        var cu = StaticJavaParser.parse(c)
        cls = cu.getClassByName(classModel.className).get()
        classModel.methods?.forEach {
            var method = createMethod( it as CtrlClass.Method)
            cls?.addMember(method)
        }
        println(cu);

    }
}