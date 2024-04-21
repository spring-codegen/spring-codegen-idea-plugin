package com.springcodegen.idea.plugin.gen

import com.springcodegen.idea.plugin.ctx.CodeSettingCtx
import com.springcodegen.idea.plugin.constants.DomainType
import com.springcodegen.idea.plugin.ctx.DomainModelCtx
import com.springcodegen.idea.plugin.ctx.MvcClassCtx
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.template.TempRender
import com.springcodegen.idea.plugin.ui.tookit.MessageBoxUtils
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
            var fp = CodeSettingCtx.mybatisMapperDir + "/mappers/" + CodeSettingCtx.module + "/" + MvcClassCtx.getDaoClass().className + "Mapper.xml"
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
                if ( !DomainModelCtx.isInnerClass(x.className) ) {
                    data["entityClass"] = x
                    resultMaps[x.className] = x
                }
            }
            data["resultMaps"] = resultMaps
            var s = TempRender.render(TempRender.TEMP_DAO_MAPPER, data);
            return s
        }
        @JvmStatic fun gen() {
            try {
                var c = createMapper()
                FileUtils.writeStringToFile(File(getFilePath()), c, Charset.forName("UTF-8"))
            }catch (e:Exception){
                MessageBoxUtils.showMessageAndFadeout(e.message)
                throw e
            }
        }
    }
}