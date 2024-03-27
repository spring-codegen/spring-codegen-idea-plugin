package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx
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
class DaoMapperGenerator :ClassGenerator(){
    companion object {
        init {
//            processImports(MvcClassCtx.getDaoClass())
        }

        fun getFilePath(): String {
            var fp = AppCtx.projectCfg?.mybatisMapperDir!! + "/mappers/" + AppCtx.projectCfg!!.module + "/" + MvcClassCtx.getDaoClass().className + "Mapper.xml"
            return fp
        }

        fun gen() {
            var classModel = MvcClassCtx.getDaoClass()
            processImports(classModel)
            var data = HashMap<String, Any?>();
            data["project"] = AppCtx.projectCfg
            data["daoClass"] = classModel
            var a = DomainModelCtx.getModesByType(DomainType.ENTITY)
            val resultMaps: MutableMap<String, ClassModel> = HashMap()
            if (a != null) {
                for (x in a) {
                    data["entityClass"] = x
                    resultMaps[x.className] = x
                }
            }
            data["resultMaps"] = resultMaps
            TempRender.renderToFile(getFilePath(), "dao-mapper.ftl", data)
        }
    }
}