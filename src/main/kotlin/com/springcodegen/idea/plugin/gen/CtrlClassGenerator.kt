package com.springcodegen.idea.plugin.gen

import com.springcodegen.idea.plugin.ctx.CodeSettingCtx
import com.springcodegen.idea.plugin.ctx.MvcClassCtx
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.gen.model.CtrlClass
import com.springcodegen.idea.plugin.template.TempRender
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
import com.springcodegen.idea.plugin.ctx.DomainModelCtx
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class CtrlClassGenerator (): ClassGenerator() {
    companion object {
        private var BIND_RESULT_CLASS = "BindingResult"
        private var BIND_RESULT_CLASS_REF_NAME = "br"
        init {
            val parserConfiguration = ParserConfiguration()
            parserConfiguration.characterEncoding = StandardCharsets.UTF_8
            StaticJavaParser.setConfiguration(parserConfiguration)
            /**
             * 处理路径参数
             */
            MvcClassCtx.getCtrlClass().methods.forEach {

            }
        }
        @JvmStatic fun getInputArgs(m: CtrlClass.Method):List<ClassModel.MethodArg>{
            var args = ArrayList<ClassModel.MethodArg>(m.args)
            if (!StringUtils.isEmpty(m.request!!.path)) {
                var phs = com.springcodegen.idea.plugin.util.StringUtils.parsePlaceholders(m.request!!.path)
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

            //如果是对象参数，需要加br
            if (!ClassModel.isBaseType(args.last().classModel?.className!!)) {
                var brCls = ClassModel(BIND_RESULT_CLASS)
                brCls.refName = BIND_RESULT_CLASS_REF_NAME
                args.add(ClassModel.MethodArg(brCls, brCls.refName))
            }
            return args
        }

        @JvmStatic fun createMethod(m: CtrlClass.Method): MethodDeclaration {
            var responseCls = CodeSettingCtx.responseCls.split(".").last()
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
            //加绑定参数,设置方法参数
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

                method?.addParameter(p)
            }
            if (inputArgs.last().classModel?.className!!.equals(BIND_RESULT_CLASS, true)){
                //如果是br,需要加校验
                var brRefName = inputArgs.last().classModel?.refName
                var brIfStmt = IfStmt(
                    MethodCallExpr(NameExpr( brRefName ), "hasErrors"),
                    BlockStmt().addStatement(
                        ReturnStmt(MethodCallExpr(
                            NameExpr(responseCls),
                            "paramError"
                        ).addArgument(NameExpr("$brRefName.getFieldError().getField() + $brRefName.getFieldError().getDefaultMessage()")))
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
                method?.parameters?.get(method.parameters!!.lastIndex-1)?.addAnnotation(MarkerAnnotationExpr("Validated"))
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
                        if (inputArg.classModel?.className.equals(BIND_RESULT_CLASS, true)){
                            break
                        }
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
                        if (inputArg.classModel?.className.equals(BIND_RESULT_CLASS, true)){
                            break
                        }
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
                                if (inputArg.classModel?.className.equals(BIND_RESULT_CLASS, true)){
                                    break
                                }
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
                    && !ClassModel.isBaseType(m.result?.classModel?.className!!)
                    && m.result?.classModel?.fields != null
                    ){
                var t = m.result?.classModel?.fields!!.any { it.name.equals("id", true)  }
                if (t) {
                    callResultVarName = m.result?.classModel?.refName
                    blockStmt.addStatement(
                        VariableDeclarationExpr(
                            VariableDeclarator(
                                ClassOrInterfaceType(null, m.result?.classModel?.className!!),
                                callResultVarName
                            ).setInitializer("new " + m.result?.classModel?.className!! + "()")
                        )
                    ).addStatement(
                        MethodCallExpr(NameExpr(callResultVarName), "setId")
                            .addArgument(MethodCallExpr(callArgExpr, "getId"))
                    )
                }
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
            var fp = CodeSettingCtx.ctrlSourceDir + "/src/main/java/" + MvcClassCtx.getCtrlClass().pkg?.replace(".", "/") + "/" + MvcClassCtx.getCtrlClass().className + ".java"
            return fp
        }
        @JvmStatic fun fileExists(): Boolean {
            return File(getFilePath()).exists();
        }
        @JvmStatic fun createClass(): CompilationUnit {

            val classModel = MvcClassCtx.getCtrlClass()
            var cu = parseExistClassFile()
            var cls:ClassOrInterfaceDeclaration? = null
            if (cu != null){
                val op = cu.getClassByName(classModel.className)
                if (op.isPresent) {
                    cls = op.get ()
                }
            }
            if ( cu == null || cls == null){
                processImports(classModel)
                val data = HashMap<String, Any?>();
                data["project"] = CodeSettingCtx
                data["ctrlClass"] = classModel

                var c = TempRender.render(TempRender.TEMP_CTRL_CLASS, data)
                System.out.println(c)
                System.out.println("====================")
                cu = StaticJavaParser.parse(c)
                cls = cu.getClassByName(classModel.className).get()
            }
            for (x in classModel.methods){
                if (parseExistMethod(x as CtrlClass.Method) != null){
                    continue
                }
                val method = createMethod(x as CtrlClass.Method)
                cls.addMember(method)
            }
            return cu!!;
        }
        @JvmStatic fun parseExistClassFile():CompilationUnit?{
            var clsFile = File(getFilePath())
            if(!clsFile.exists()){
                return null
            }
            val cu = StaticJavaParser.parse(clsFile)
            return cu
        }
        @JvmStatic fun parseExistMethod(m:CtrlClass.Method):MethodDeclaration?{
            var cu = parseExistClassFile() ?: return null
            var classModel = MvcClassCtx.getCtrlClass()
            var op= cu.getClassByName(classModel.className)
            if ( !op.isPresent ){
                return null
            }
            var cls = op.get()
            var a = cls.getMethodsByName(m.name)
            if ( a == null || a.size == 0 ){
                return null
            }
            var inputArgs = getInputArgs(m)
            for (x in a){
                if (x.parameters.size != inputArgs.size){
                    continue
                }
                var matched = true
                for ( i in inputArgs.indices){
                    if ( !x.parameters[i].type.toString().equals(inputArgs[i].classModel?.className, true)){
                        matched = false
                        break
                    }
                }
                if (matched){
                    return x
                }
            }
            return null
        }
        @JvmStatic fun gen() {
            var cu = createClass();
            println(cu);
            writeFile(getFilePath(), cu.toString())
        }
    }
}