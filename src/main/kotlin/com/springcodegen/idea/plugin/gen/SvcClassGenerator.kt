package com.springcodegen.idea.plugin.gen

import com.springcodegen.idea.plugin.ctx.AppCtx
import com.springcodegen.idea.plugin.ctx.CodeSettingCtx
import com.springcodegen.idea.plugin.ctx.MvcClassCtx
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.gen.model.SvcClass
import com.springcodegen.idea.plugin.template.TempRender
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.stmt.IfStmt
import com.github.javaparser.ast.stmt.ReturnStmt
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.javadoc.Javadoc
import com.github.javaparser.javadoc.JavadocBlockTag
import com.github.javaparser.javadoc.description.JavadocDescription
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class SvcClassGenerator: ClassGenerator(){
    companion object {
        @JvmStatic fun createMethod(m: ClassModel.Method): MethodDeclaration {

            var method = MethodDeclaration().setName(m.name).addModifier(Modifier.Keyword.PUBLIC)
            var methodDoc = Javadoc(JavadocDescription.parseText(if (m.comment == null) "" else m.comment))
            var blockStmt = BlockStmt()
            var resultType: ClassOrInterfaceType = ClassOrInterfaceType(null, m.result?.classModel?.className)
            if (m.result?.listTypeFlag != null && m.result?.listTypeFlag!!) {
                resultType = ClassOrInterfaceType(null, "List").setTypeArguments(resultType)
            }
            method?.setType(resultType)
            //加绑定参数

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

            method.setJavadocComment(methodDoc)
            var callArg: ClassModel.MethodArg? = null
            if (m.dependency != null && m.dependency?.args != null && m.dependency?.args!!.size > 0) {
                callArg = m.dependency?.args!![0]
            }
            var callArgExpr: Expression? = null
            if (callArg != null) {
                callArgExpr = NameExpr(callArg.refName?:callArg.classModel?.refName)
            }

            if (callArg != null) {
                //如果目标类型是基本类型，
                if (ClassModel.isBaseType(callArg.classModel?.className!!)) {

                    //目标类型
                    for (inputArg in m.args) {
                        //都是基本类型
                        if (inputArg.classModel?.className.equals(callArg.classModel?.className)) {
                            callArgExpr = NameExpr(inputArg.refName?:inputArg.classModel?.refName)
                            break;
                        }
                        //需要调用getter
                        if (!ClassModel.isBaseType(inputArg.classModel?.className!!)) {
                            for (f in inputArg.classModel?.fields!!) {
                                if (f.name.equals(callArg.classModel?.refName)) {
                                    //调用get方法
                                    callArgExpr = MethodCallExpr(NameExpr(inputArg.classModel!!.refName), f.getter)
                                }
                            }
                        }
                    }
                } else {
                    //往复合类型转换
                    for (inputArg in m.args) {
                        //类型相同
                        if (inputArg.classModel?.className.equals(callArg.classModel?.className)) {
                            callArgExpr = NameExpr(inputArg.classModel?.refName)
                            break;
                        }
                        //需要调用转换
                        if (!ClassModel.isBaseType(inputArg.classModel?.className!!)) {
                            //调用copyTo
                            var callExp = MethodCallExpr(NameExpr(inputArg.classModel!!.refName), "copyTo");
                            callExp.addArgument(FieldAccessExpr(NameExpr(callArg.classModel?.className), "class"));
                            var varDeclarator = VariableDeclarator(ClassOrInterfaceType(null, callArg.classModel?.className), callArg.classModel?.refName, callExp)
                            blockStmt.addStatement(VariableDeclarationExpr(varDeclarator))
                            callArgExpr = NameExpr(callArg.classModel?.refName)
                            for (x in m.args) {
                                if (ClassModel.isBaseType(x.classModel?.className!!)) {
                                    for (f in inputArg.classModel?.fields!!) {
                                        if (f.name.equals(x.classModel?.refName, true)) {
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
            if (m.dependency == null){
                return method
            }
            var dependencyCallExpr = MethodCallExpr(NameExpr(MvcClassCtx.getSvcClass().dependency?.refName), m.dependency?.name);
            dependencyCallExpr.addArgument(callArgExpr);
            var callReturn = m.dependency?.result
            var resultDataVarName: String? = null
            if (callReturn == null) {
                blockStmt.addStatement(dependencyCallExpr);
            } else {
                var resultDeclar: VariableDeclarator?
                resultDataVarName = callReturn.classModel?.refName!!
                var itemType = ClassOrInterfaceType(null, callReturn.classModel?.className)
                if (m.result?.listTypeFlag != null && m.result?.listTypeFlag!!) {
                    resultDataVarName = "items"
                    var dataType = ClassOrInterfaceType(null, "List")
                            .setTypeArguments(itemType)
                    resultDeclar = VariableDeclarator(
                            dataType,
                            resultDataVarName,
                            dependencyCallExpr
                    )
                } else {
                    resultDeclar = VariableDeclarator(
                            ClassOrInterfaceType(null, callReturn.classModel?.className),
                            resultDataVarName,
                            dependencyCallExpr
                    )
                }
                blockStmt.addStatement(VariableDeclarationExpr(resultDeclar))
            }
            //
            if (m.result != null && callReturn != null){
                //
                if (m.result?.classModel?.className.equals("Boolean", true)
                        && (
                                callReturn.classModel?.className.equals("Long", true)
                                        || callReturn.classModel?.className.equals("Integer", true)
                                )){
                    var transformExpr = VariableDeclarationExpr(
                            VariableDeclarator(
                                    ClassOrInterfaceType(null, m.result?.classModel?.className),
                                    m.result?.classModel?.refName,
                                    ConditionalExpr(
                                            BinaryExpr(NameExpr(resultDataVarName), NameExpr("0"), BinaryExpr.Operator.GREATER),
                                            BooleanLiteralExpr(true),
                                            BooleanLiteralExpr(false)
                                    )
                            )
                    )
                    blockStmt.addStatement(transformExpr)
                    resultDataVarName = m.result?.classModel?.refName;
                }
            }
            if (resultDataVarName != null) {
                blockStmt.addStatement(ReturnStmt(resultDataVarName))
            }

            method?.setBody(blockStmt)
            return method
        }

        @JvmStatic fun getFilePath(): String {
            var fp = CodeSettingCtx.svcSourceDir + "/src/main/java/" + MvcClassCtx.getSvcClass().pkg?.replace(".", "/") + "/" + MvcClassCtx.getSvcClass().className + ".java"
            return fp
        }
        @JvmStatic fun fileExists(): Boolean {
            return File(getFilePath()).exists();
        }
        @JvmStatic fun createClass(): CompilationUnit {
            var classModel = MvcClassCtx.getSvcClass()
            processImports(classModel)
            var data = HashMap<String, Any?>();
            data["project"] = CodeSettingCtx
            data["svcClass"] = classModel

            var c = TempRender.render(TempRender.TEMP_SVC_CLASS, data)
//        System.out.println(c)
//        System.out.println("====================")
            val parserConfiguration = ParserConfiguration()
            parserConfiguration.characterEncoding = StandardCharsets.UTF_8
            StaticJavaParser.setConfiguration(parserConfiguration)
            var cu = StaticJavaParser.parse(c)
            var cls = cu.getClassByName(classModel.className).get()
            classModel.methods.forEach {
                var method = createMethod(it )
                cls.addMember(method)
            }
            return cu;
        }

        @JvmStatic fun gen() {
            var cu = createClass()
            println(cu);
            writeFile(getFilePath(), cu.toString())
        }
    }
}