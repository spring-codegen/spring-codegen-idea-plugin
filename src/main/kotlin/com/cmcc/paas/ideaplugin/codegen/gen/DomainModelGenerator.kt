package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/22
 */
class DomainModelGenerator( module: String): ClassGenerator(module) {
    init {
        DomainModelCtx.getAllModels()!!.forEach {
            setClassModelRefName(it)
        }
    }
    fun genModel(classModel: ClassModel, validate: Boolean){
        if (!ClassModel.isBaseType(classModel.className) && !ClassModel.isCommonType(classModel.className) ){
            var data = HashMap<String, Any?>();
            data["project"] = AppCtx.projectCfg
            data["model"] = classModel
            data["validator"] = validate
            classModel.fields?.forEach{
                it.setter = FieldUtils.setter(it.name)
                it.getter = FieldUtils.getter(it.name)
            }
            processImports(classModel)
            TempRender.renderToFile(AppCtx.projectCfg?.modelSourceDir!!, classModel.pkg!!, classModel.className, "model.ftl", data)
        }
    }
    fun gen(){
        DomainModelCtx.getModesByTypes(DomainType.ARG)!!.forEach {
            it.pkg = AppCtx.projectCfg?.basePkg + ".domain.arg." + module
        }
        DomainModelCtx.getModesByTypes(DomainType.ENTITY)!!.forEach {
            it.pkg = AppCtx.projectCfg?.basePkg + ".domain.entity." + module
        }
        DomainModelCtx.getModesByTypes(DomainType.RESULT)!!.forEach {
            it.pkg = AppCtx.projectCfg?.basePkg + ".domain.result." + module
        }
        DomainModelCtx.getModesByTypes(DomainType.ARG)!!.forEach {
            if (!it.isInnerClass()){
                genModel(it, true)
            }
        }
        DomainModelCtx.getModesByTypes(DomainType.ENTITY, DomainType.RESULT)!!.forEach {
            if (!it.isInnerClass()){
                genModel(it, false)
            }
        }
    }
}