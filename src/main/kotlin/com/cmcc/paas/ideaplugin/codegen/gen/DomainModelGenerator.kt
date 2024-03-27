package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.cmcc.paas.ideaplugin.codegen.util.FieldUtils
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/22
 */
class DomainModelGenerator: ClassGenerator() {
    companion object {

        @JvmStatic fun genModel(classModel: ClassModel, validate: Boolean) {
            if (!ClassModel.isBaseType(classModel.className) && !ClassModel.isCommonType(classModel.className)) {
                var data = HashMap<String, Any?>();
                data["project"] = AppCtx.projectCfg
                data["model"] = classModel
                data["validator"] = validate
                classModel.fields?.forEach {
                    it.setter = FieldUtils.setter(it.name)
                    it.getter = FieldUtils.getter(it.name)
                }
                processImports(classModel)
                TempRender.renderToFile(AppCtx.projectCfg?.modelSourceDir!!, classModel.pkg!!, classModel.className, "model.ftl", data)
            }
        }
        @JvmStatic fun updateImplements(){
            for( x in DomainModelCtx.getAllModels()!!){
                if (!ClassModel.isInnerClass(x.className)) {
                    var s = AppCtx.projectCfg?.modelBaseCls
                    if (x.className.indexOf("Search") >= 0 && !AppCtx.projectCfg?.searchArgBaseCls.isNullOrEmpty()) {
                        s = AppCtx.projectCfg?.searchArgBaseCls
                    }
                    if (!s.isNullOrEmpty()) {
                        x.extend = ClassModel.parse(s)
                    }
                }
            }
        }

        @JvmStatic fun gen() {
            updateImplements()
            DomainModelCtx.getAllModels()!!.forEach {
                setClassModelRefName(it)
            }
            DomainModelCtx.getModesByTypes(DomainType.ARG).forEach {
                if (!ClassModel.isInnerClass(it.className)) {
                    it.pkg = AppCtx.projectCfg?.basePkg + ".domain.arg." + AppCtx.projectCfg?.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ENTITY).forEach {
                if (!ClassModel.isInnerClass(it.className)) {
                    it.pkg = AppCtx.projectCfg?.basePkg + ".domain.entity." + AppCtx.projectCfg?.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.RESULT).forEach {
                if (!ClassModel.isInnerClass(it.className)) {
                    it.pkg = AppCtx.projectCfg?.basePkg + ".domain.result." + AppCtx.projectCfg?.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ARG).forEach {
                if (!it.isInnerClass()) {
                    genModel(it, true)
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ENTITY, DomainType.RESULT).forEach {
                if (!it.isInnerClass()) {
                    genModel(it, false)
                }
            }
        }
    }
}