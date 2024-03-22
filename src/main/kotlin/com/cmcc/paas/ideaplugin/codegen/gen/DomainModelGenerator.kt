package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.DomainModels
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/22
 */
class DomainModelGenerator( module: String, projectCfg: ProjectCfg): ClassGenerator(module, projectCfg) {
    init {
        DomainModels.getAllModels()!!.forEach {
            setClassModelRefName(it)
        }
    }
    fun genModel(classModel: ClassModel, validate: Boolean){
        if (!ClassModel.isBaseType(classModel.className) && !ClassModel.isCommonType(classModel.className) ){
            var data = HashMap<String, Any?>();
            data["project"] = projectCfg
            data["model"] = classModel
            data["validator"] = validate
            classModel.fields?.forEach{
                it.setter = FieldUtils.setter(it.name)
                it.getter = FieldUtils.getter(it.name)
            }
            processImports(classModel)
            TempRender.renderToFile(projectCfg.modelSourceDir!!, classModel.pkg!!, classModel.className, "model.ftl", data)
        }
    }
    fun gen(){
        DomainModels.getModesByTypes(DomainType.ARG)!!.forEach {
            it.pkg = projectCfg.basePkg + ".domain.arg." + module
        }
        DomainModels.getModesByTypes(DomainType.ENTITY)!!.forEach {
            it.pkg = projectCfg.basePkg + ".domain.entity." + module
        }
        DomainModels.getModesByTypes(DomainType.RESULT)!!.forEach {
            it.pkg = projectCfg.basePkg + ".domain.result." + module
        }
        DomainModels.getModesByTypes(DomainType.ARG)!!.forEach {
            if (!it.isInnerClass()){
                genModel(it, true)
            }
        }
        DomainModels.getModesByTypes(DomainType.ENTITY, DomainType.RESULT)!!.forEach {
            if (!it.isInnerClass()){
                genModel(it, false)
            }
        }
    }
}