package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.CodeSettingCtx
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender
import com.cmcc.paas.ideaplugin.codegen.ui.MessageBox
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset
import java.util.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/14
 */
class DaoMapperGenerator :ClassGenerator(){
    companion object {
        @JvmStatic fun getFilePath(): String {
            var fp = CodeSettingCtx.mybatisMapperDir!! + "/mappers/" + CodeSettingCtx.module + "/" + MvcClassCtx.getDaoClass().className + "Mapper.xml"
            return fp
        }
        @JvmStatic fun createMapper():String{

            var classModel = MvcClassCtx.getDaoClass()
            processImports(classModel)
            var data = HashMap<String, Any?>();
            data["project"] = CodeSettingCtx
            data["daoClass"] = classModel
            var a = DomainModelCtx.getModesByType(DomainType.ENTITY)
            val resultMaps: MutableMap<String, ClassModel> = HashMap()
            for (x in a) {
                data["entityClass"] = x
                resultMaps[x.className] = x
            }
            data["resultMaps"] = resultMaps
            var s = TempRender.render("dao-mapper.ftl", data);
            return s
        }
        @JvmStatic fun gen() {
            try {
                var c = createMapper()
                FileUtils.writeStringToFile(File(getFilePath()), c, Charset.forName("UTF-8"))
            }catch (e:Exception){
                MessageBox.showMessageAndFadeout(e.message)
                throw e
            }
        }
    }
}