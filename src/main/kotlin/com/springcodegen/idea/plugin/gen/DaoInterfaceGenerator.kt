package com.springcodegen.idea.plugin.gen

import com.springcodegen.idea.plugin.ctx.CodeSettingCtx
import com.springcodegen.idea.plugin.constants.DomainType
import com.springcodegen.idea.plugin.ctx.DomainModelCtx
import com.springcodegen.idea.plugin.ctx.MvcClassCtx
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.template.TempRender
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.javadoc.Javadoc
import com.github.javaparser.javadoc.JavadocBlockTag
import com.github.javaparser.javadoc.description.JavadocDescription
import org.apache.commons.lang.StringUtils
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class DaoInterfaceGenerator:ClassGenerator(){
    companion object {
        private var ignoreMethodNames = arrayOf("add", "get", "remove","update","search")
        init {
        }
        @JvmStatic fun createMethod(m: ClassModel.Method): MethodDeclaration {
            var method = MethodDeclaration().setName(m.name).addModifier(Modifier.Keyword.PUBLIC)
            var methodDoc = Javadoc(JavadocDescription.parseText(if (m.comment == null) "" else m.comment))
            var resultType = ClassOrInterfaceType(null, m.result?.classModel?.className)
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
            return method
        }

        @JvmStatic fun getFilePath(): String {
            var fp = CodeSettingCtx.svcSourceDir + "/src/main/java/" + MvcClassCtx.getDaoClass().pkg?.replace(".", "/") + "/" + MvcClassCtx.getDaoClass().className + ".java"
            return fp
        }
        @JvmStatic fun fileExists(): Boolean {
            return File(getFilePath()).exists();
        }

        @JvmStatic fun createClass(): CompilationUnit {
            var classModel = MvcClassCtx.getDaoClass()
            processImports(classModel)
            var data = HashMap<String, Any?>();
            data["project"] = CodeSettingCtx
            data["daoClass"] = classModel
            var a = DomainModelCtx.getModesByType(DomainType.ARG)

            for (x in a) {
                if (x.className.indexOf("Search") >= 0) {
                    data["searchClass"] = x
                }
            }
            a = DomainModelCtx.getModesByType(DomainType.ENTITY)
            for (x in a) {
                if (!DomainModelCtx.isInnerClass(x.className)) {
                    data["entityClass"] = x
                }
            }

            var c = TempRender.render(TempRender.TEMP_DAO_INTERFACE, data)
            val parserConfiguration = ParserConfiguration()
            parserConfiguration.characterEncoding = StandardCharsets.UTF_8
            StaticJavaParser.setConfiguration(parserConfiguration)
            var cu = StaticJavaParser.parse(c)
            var cls = cu.getInterfaceByName(classModel.className).get()
            for (m in classModel.methods) {
                var dups = ignoreMethodNames.find { e -> e.equals(m.name, true) }
                if (!dups.isNullOrEmpty()) {
                    continue
                }
                var method = createMethod(m)
                method.setBody(null)
                cls.addMember(method)
            }
            return cu;
        }
        @JvmStatic fun gen() {
            var cu = createClass();
            writeFile(getFilePath(), cu.toString())
        }
    }
}