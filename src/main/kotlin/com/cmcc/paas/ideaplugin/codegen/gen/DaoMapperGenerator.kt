package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.CodeSettingCtx
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
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
            var fp = CodeSettingCtx.mybatisMapperDir!! + "/mappers/" + CodeSettingCtx.module + "/" + MvcClassCtx.getDaoClass().className + "Mapper.xml"
            return fp
        }

        fun gen() {
            var classModel = MvcClassCtx.getDaoClass()
            processImports(classModel)
            var data = HashMap<String, Any?>();
            data["project"] = CodeSettingCtx
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