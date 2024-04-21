package com.springcodegen.idea.plugin.gen

import com.springcodegen.idea.plugin.constants.DomainType
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.ctx.DomainModelCtx
import com.springcodegen.idea.plugin.ctx.CodeSettingCtx
import com.springcodegen.idea.plugin.template.TempRender
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
        @JvmStatic fun updateImplement(classModel: ClassModel){
            if (!DomainModelCtx.isInnerClass(classModel.className)) {
                var s = CodeSettingCtx.modelBaseCls
                if (classModel.className.indexOf("Search") >= 0 && !CodeSettingCtx.searchArgBaseCls.isNullOrEmpty()) {
                    s = CodeSettingCtx.searchArgBaseCls
                }
                if (!s.isNullOrEmpty()) {
                    classModel.extend = ClassModel.parse(s)
                }
            }
        }
        @JvmStatic fun createClass(classModel: ClassModel, validate: Boolean):String? {
            updateImplement(classModel)
            if (!ClassModel.isBaseType(classModel.className) && !DomainModelCtx.isInnerClass(classModel.className)) {
                var data = HashMap<String, Any?>();
                data["project"] = CodeSettingCtx
                data["model"] = classModel
                data["validator"] = validate
                processImports(classModel)
                var c = TempRender.render(TempRender.TEMP_MODEL_CLASS, data);
                return c;
            }
            return null;
        }
        @JvmStatic fun fileExists(classModel:ClassModel):Boolean{
            return File( getFilePath(classModel) ).exists()
        }
        @JvmStatic fun getFilePath(classModel:ClassModel): String {
            var fp = CodeSettingCtx.modelSourceDir + "/src/main/java/"+ classModel.pkg!!.replace(".", "/") + "/" + classModel.className+".java"
            return fp
        }
        @JvmStatic fun genModel(classModel: ClassModel, validate: Boolean) {
            var c = createClass(classModel, validate)
            FileUtils.writeStringToFile(File(getFilePath(classModel)), c, Charset.forName("UTF-8"))
        }

        @JvmStatic fun gen() {
            DomainModelCtx.getAllModels().forEach {
                setClassModelRefName(it)
            }
            DomainModelCtx.getModesByTypes(DomainType.ARG).forEach {
                if (!DomainModelCtx.isInnerClass(it.className)) {
                    it.pkg = CodeSettingCtx.basePkg + ".domain.arg." + CodeSettingCtx.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ENTITY).forEach {
                if (!DomainModelCtx.isInnerClass(it.className)) {
                    it.pkg = CodeSettingCtx.basePkg + ".domain.entity." + CodeSettingCtx.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.RESULT).forEach {
                if (!DomainModelCtx.isInnerClass(it.className)) {
                    it.pkg = CodeSettingCtx.basePkg + ".domain.result." + CodeSettingCtx.module
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ARG).forEach {
                if (!DomainModelCtx.isInnerClass(it.className)) {
                    genModel(it, true)
                }
            }
            DomainModelCtx.getModesByTypes(DomainType.ENTITY, DomainType.RESULT).forEach {
                if (!DomainModelCtx.isInnerClass(it.className)) {
                    genModel(it, false)
                }
            }
        }
    }
}