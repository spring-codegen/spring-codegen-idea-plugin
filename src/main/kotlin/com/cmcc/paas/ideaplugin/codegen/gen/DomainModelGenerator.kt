package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.CodeSettingCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.cmcc.paas.ideaplugin.codegen.util.FieldUtils
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/22
 */
class DomainModelGenerator: ClassGenerator() {
    companion object {
        @JvmStatic fun createClass(classModel: ClassModel, validate: Boolean):String? {
            if (!ClassModel.isBaseType(classModel.className) && !ClassModel.isCommonType(classModel.className)) {
                var data = HashMap<String, Any?>();
                data["project"] = CodeSettingCtx
                data["model"] = classModel
                data["validator"] = validate
                processImports(classModel)
                var c = TempRender.render("model.ftl", data);
                return c;
            }
            return null;
        }
        @JvmStatic fun getFilePath(classModel:ClassModel): String {
            var fp = CodeSettingCtx.modelSourceDir!! + "/"+ classModel.pkg!!.replace(".", "/") + "/" + classModel.className+".java"
            return fp
        }
        @JvmStatic fun genModel(classModel: ClassModel, validate: Boolean) {
            var c = createClass(classModel, validate)
            FileUtils.writeStringToFile(File(getFilePath(classModel)), c, Charset.forName("UTF-8"))
        }
        @JvmStatic fun updateImplements(){
            for( x in DomainModelCtx.getAllModels()!!){
                if (!ClassModel.isInnerClass(x.className)) {
                    var s = CodeSettingCtx.modelBaseCls
                    if (x.className.indexOf("Search") >= 0 && !CodeSettingCtx.searchArgBaseCls.isNullOrEmpty()) {
                        s = CodeSettingCtx.searchArgBaseCls
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
                    it.pkg = CodeSettingCtx.basePkg + ".domain.arg." + CodeSettingCtx.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ENTITY).forEach {
                if (!ClassModel.isInnerClass(it.className)) {
                    it.pkg = CodeSettingCtx.basePkg + ".domain.entity." + CodeSettingCtx.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.RESULT).forEach {
                if (!ClassModel.isInnerClass(it.className)) {
                    it.pkg = CodeSettingCtx.basePkg + ".domain.result." + CodeSettingCtx.module
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