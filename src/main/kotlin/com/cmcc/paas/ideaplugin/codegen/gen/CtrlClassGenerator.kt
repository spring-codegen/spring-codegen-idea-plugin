package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.CodeSettingCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
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
class CtrlClassGenerator (): ClassGenerator() {
    companion object {
        init {
            /**
             * 处理路径参数
             */
            MvcClassCtx.getCtrlClass().methods.forEach {

            }
        }
        @JvmStatic fun getInputArgs(m: CtrlClass.Method):List<ClassModel.MethodArg>{
            var args = ArrayList<ClassModel.MethodArg>(m.args)
            if (!StringUtils.isEmpty(m.request!!.path)) {
                var phs = com.cmcc.paas.ideaplugin.codegen.util.StringUtils.parsePlaceholders(m.request!!.path)
                if (phs != null) {
                    for (ph in phs) {
                        if (m.dependency != null) {
                            for (arg in m.dependency!!.args) {
                                var field = arg.classModel!!.fields!!.find { e2 -> e2.name.equals(ph, true) }
                                if (field != null) {
                                    var c = ClassModel(field.javaType)
                                    c.refName = ph
                                    var phArg = ClassModel.MethodArg(c, ph)
                                    phArg.isPathVar = true
                                    phArg.comment = field.comment
                                    args.add(0, phArg)
                                }
                            }
                        }
                    }
                }
            }
            return args
        }

        @JvmStatic fun createMethod(m: CtrlClass.Method): MethodDeclaration {
            var responseCls = CodeSettingCtx.responseCls?.split(".")?.last()
            var method = MethodDeclaration().setName(m.name).addModifier(Modifier.Keyword.PUBLIC)
            var methodDoc = Javadoc(JavadocDescription.parseText(if (m.comment == null) "" else m.comment))
            var blockStmt = BlockStmt()
            method?.setBody(blockStmt)
            var resultType = ClassOrInterfaceType(null, responseCls)
            if (m.result == null) {
                resultType.setTypeArguments(WildcardType())
            } else if (m.result?.outputPaged == true) {
                resultType.setTypeArguments(
                        ClassOrInterfaceType(null, "ListResult")
                                .setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className))
                )
            } else if (m.result?.listTypeFlag == true){
                resultType.setTypeArguments(
                        ClassOrInterfaceType(null, "List")
                                .setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className))
                )
            }else {
                resultType.setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className))
            }

            method?.setType(resultType)
            var methodAnno = NormalAnnotationExpr(
                    Name("RequestMapping"),
                    NodeList(
                            MemberValuePair("path", StringLiteralExpr(m.request?.path)),
                            MemberValuePair("method", FieldAccessExpr(NameExpr("RequestMethod"), m.request?.httpMethod))
                    )
            )
            method?.addAnnotation(methodAnno)

            var inputArgs = getInputArgs(m)
            //加绑定参数
            var br: Parameter? = null
            inputArgs.forEach {
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
                if (it.isPathVar) {
                    p.addSingleMemberAnnotation("PathVariable", "\"" + varName + "\"")
                }
                //如果是对象参数，需要加br
                if (!ClassModel.isBaseType(it.classModel?.className!!)) {
                    br = Parameter().setType("BindingResult").setName("br")
                    var brIfStmt = IfStmt(
                            MethodCallExpr(NameExpr("br"), "hasErrors"),
                            BlockStmt().addStatement(
                                ReturnStmt(MethodCallExpr(
                                    NameExpr(responseCls),
                                    "paramError"
                                ).addArgument(NameExpr("br.getFieldError().getField() + br.getFieldError().getDefaultMessage()")))
                            ),
                            null)

//                                    ThrowStmt(
//                                            ObjectCreationExpr(
//                                                    null,
//                                                    ClassOrInterfaceType(null, "ParamException"),
//                                                    NodeList<Expression>()
//                                            )
//                                    )
                    blockStmt.addStatement(brIfStmt)
                    p.addAnnotation(MarkerAnnotationExpr("Validated"))
                }
                method?.addParameter(p)
            }
            if (br != null) {
                method?.addParameter(br)
            }

            method.setJavadocComment(methodDoc)
            var callArg: ClassModel.MethodArg? = null
            if (m.dependency != null && m.dependency?.args != null && m.dependency?.args!!.size > 0) {
                callArg = m.dependency?.args!![0]
            }
            var callArgExpr: Expression? = null
            if (callArg != null) {
                callArgExpr = NameExpr(callArg.classModel?.refName)
            }
//            var calleeArgName:String? = null
            if (callArg != null) {
                //如果目标类型是基本类型，
                if (ClassModel.isBaseType(callArg.classModel?.className!!)) {
                    //目标类型
                    for (inputArg in inputArgs) {
                        //都是基本类型
                        if (inputArg.classModel?.className.equals(callArg.classModel?.className)) {
                            callArgExpr = NameExpr(inputArg.classModel?.refName)
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
                    for (inputArg in inputArgs) {
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
                            for (x in inputArgs) {
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
            var dependencyCallExpr = MethodCallExpr(NameExpr(MvcClassCtx.getCtrlClass().dependency?.refName), m.dependency?.name);
            dependencyCallExpr.addArgument(callArgExpr);
            var callReturn = m.dependency?.result
            var callResultVarName: String? = null
//            var resDataVarName: String? = null
            var resDataType: Type? = WildcardType()
            var resultTransformExpr:Expression? = null
//            var resType1 = ClassOrInterfaceType(null, "HttpResponse")
//            //没有返回值
            if (m.result != null){
                resDataType = ClassOrInterfaceType(null, m.result?.classModel?.className)
                if (m.result?.outputPaged == true){
                    resDataType = ClassOrInterfaceType(null, "ListResult").setTypeArguments(resDataType)
                }else if(m.result?.listTypeFlag == true){
                    resDataType = ClassOrInterfaceType(null, "List").setTypeArguments(resDataType)
                }
            }
            //如果service方法返回空
            if (callReturn == null) {
                blockStmt.addStatement(dependencyCallExpr);
            } else {
                var resultDeclar: VariableDeclarator?
                callResultVarName = callReturn.classModel?.refName!!
                var itemType = ClassOrInterfaceType(null, callReturn.classModel?.className)
                if (m.result != null && m.result?.outputPaged == true){
                    blockStmt.addStatement(
                        MethodCallExpr(NameExpr("PageHelper"), "startPage")
                            .addArgument(
                                MethodCallExpr(callArgExpr, "getPageNum")
                            ).addArgument(
                                MethodCallExpr(callArgExpr, "getPageSize")
                            )
                    )
                }
                //如果返回列表
                if (callReturn.listTypeFlag  == true) {
                    callResultVarName = "items"
                    var dataType = ClassOrInterfaceType(null, "List")
                            .setTypeArguments(itemType)
                    resultDeclar = VariableDeclarator(
                            dataType,
                            callResultVarName,
                            dependencyCallExpr
                    )
                    //controller也返回列表,并且类型相同，非基本类型
                    if (m.result != null
                            && m.result?.listTypeFlag  == true
                            && !m.result?.classModel?.className.equals(callReturn.classModel?.className)
                            && !ClassModel.isBaseType(m.result?.classModel?.className!!)
                            && !ClassModel.isBaseType(callReturn.classModel?.className!!)){
                        //items.stream().map( e->e.copyTo(Project.class)).toList();
                        var transformCallExpr =  MethodCallExpr(
                            MethodCallExpr( NameExpr(callResultVarName), "stream" ),
                            "map"
                        )
                            .addArgument(
                                LambdaExpr(
                                    Parameter(itemType, "e"),
                                    MethodCallExpr(NameExpr("e"), "copyTo")
                                        .addArgument(FieldAccessExpr(NameExpr(m.result?.classModel?.className), "class"))

                                ).setEnclosingParameters(true)
                            )
                        resultTransformExpr = VariableDeclarationExpr(
                                VariableDeclarator(
                                        ClassOrInterfaceType(null, "List")
                                            .setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className)),
                                        "data",
                                        MethodCallExpr(transformCallExpr, "toList")
                                )
                        )
                        callResultVarName = "data"

                    }// end if
                }
                else {
                    //返回单个对象 var x = scope.xxx()
                    resultDeclar = VariableDeclarator(
                            ClassOrInterfaceType(null, callReturn.classModel?.className),
                            callResultVarName,
                            dependencyCallExpr
                    )
                    //单个类型转换,非基本类型 data = x.copyTo
                    if (m.result != null
                            && m.result?.listTypeFlag  != true
                            && !m.result?.classModel?.className.equals(callReturn.classModel?.className)
                            && !ClassModel.isBaseType(m.result?.classModel?.className!!)
                            && !ClassModel.isBaseType(callReturn.classModel?.className!!)){

                        resultTransformExpr = VariableDeclarationExpr(
                                VariableDeclarator(
                                        ClassOrInterfaceType(null, m.result?.classModel?.className),
                                        "data",
                                        MethodCallExpr(NameExpr(callResultVarName),"copyTo").addArgument(FieldAccessExpr(NameExpr(m.result?.classModel?.className), "class"))
                                )
                        )
                        callResultVarName = "data"
                    }
                }
                blockStmt.addStatement(VariableDeclarationExpr(resultDeclar))
            }

            if ( resultTransformExpr != null ){
                blockStmt.addStatement(resultTransformExpr)
            }
            //需要分页 生命ListResult
            if (m.result != null && m.result?.outputPaged == true && callReturn != null) {
                var listResultDeclar = VariableDeclarator(
                    ClassOrInterfaceType(null, "ListResult")
                        .setTypeArguments(ClassOrInterfaceType(null, m.result?.classModel?.className)),
                    "listResult"
                )
                listResultDeclar.setInitializer(
                    "new ListResult(((Page) "
                            + callResultVarName + ").getTotal(), ((Page)"
                            +callResultVarName+").getPageSize(), ((Page) "
                            + callResultVarName + ").getPageNum(), "
                            + callResultVarName + ")"
                )
                blockStmt.addStatement(VariableDeclarationExpr(listResultDeclar))
                callResultVarName = "listResult"
            }
            var resDeclar = VariableDeclarator(ClassOrInterfaceType(null, responseCls).setTypeArguments(resDataType), "res")
            blockStmt.addStatement(VariableDeclarationExpr(resDeclar))


            //单独处理下add方法
            if (
                    m.type.equals("add", true)
                    &&  m.result != null
                    && m.result?.classModel?.className.equals("IdResult", true)
                    ){
                callResultVarName = m.result?.classModel?.refName
                blockStmt.addStatement(
                        VariableDeclarationExpr(
                                VariableDeclarator(
                                        ClassOrInterfaceType(null, m.result?.classModel?.className!!),
                                        callResultVarName
                                ).setInitializer("new "+ m.result?.classModel?.className!! +"()")
                        )
                ).addStatement(
                        MethodCallExpr(NameExpr(callResultVarName), "setId")
                                .addArgument(MethodCallExpr(callArgExpr, "getId"))
                )
            }

            //如果callee返回bool类型的诗句，但是controller不返回
            if ( callReturn != null
                    && !callReturn.listTypeFlag
                    && callReturn.classModel?.className.equals("Boolean", true)
                    && m.result == null){
                resDeclar.setInitializer("null")
                var resIfStmt = IfStmt(
                        NameExpr(callResultVarName),
                        BlockStmt().addStatement(
                                AssignExpr(NameExpr(resDeclar.name), MethodCallExpr(NameExpr(responseCls), "success"), AssignExpr.Operator.ASSIGN)
                        ),
                        BlockStmt().addStatement(
                                AssignExpr(NameExpr(resDeclar.name), MethodCallExpr(NameExpr(responseCls), "error"), AssignExpr.Operator.ASSIGN)
                        )
                )
                blockStmt.addStatement(resIfStmt)
            }
            else {
                resDeclar.setInitializer(responseCls + ".success()")
                if (
                        callResultVarName != null
                        && m.result != null ) {
                    blockStmt.addStatement(
                            MethodCallExpr(
                                    NameExpr("res"),
                                    "setData"
                            ).addArgument(NameExpr(callResultVarName))
                    )
                }
            }
            blockStmt.addStatement(ReturnStmt("res"))

            return method
        }

        @JvmStatic fun getFilePath(): String {
            var fp = CodeSettingCtx.ctrlSourceDir!! + "/" + MvcClassCtx.getCtrlClass().pkg?.replace(".", "/") + "/" + MvcClassCtx.getCtrlClass().className + ".java"
            return fp
        }
        @JvmStatic fun createClass(): CompilationUnit {
            var classModel = MvcClassCtx.getCtrlClass()
            processImports(classModel)
            var data = HashMap<String, Any?>();
            data["project"] = CodeSettingCtx
            data["ctrlClass"] = classModel

            var c = TempRender.render("ctrl-class.ftl", data)
            System.out.println(c)
            System.out.println("====================")
            val parserConfiguration = ParserConfiguration()
            parserConfiguration.characterEncoding = StandardCharsets.UTF_8
            StaticJavaParser.setConfiguration(parserConfiguration)
            var cu = StaticJavaParser.parse(c)
            var cls = cu.getClassByName(classModel.className).get()
            classModel.methods.forEach {
                var method = createMethod(it as CtrlClass.Method)
                cls.addMember(method)
            }
            return cu;
        }
        @JvmStatic fun gen() {
            var cu = createClass();
            println(cu);
            writeFile(getFilePath(), cu.toString())
        }
    }
}