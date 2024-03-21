package com.cmcc.paas.ideaplugin.codegen.util

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils.javaType
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils.propertyName
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

/**
 *
 * @author zhangyinghui
 * @date 2024/3/19
 */
object CodeGenUtils {

    fun getDefaultFields(fields: List<DBTableField>, includes: String?, excludes: String?): List<ClassModel.Field>? {
        val allowFields: MutableList<ClassModel.Field> = ArrayList()
        for (field in fields){
            if (StringUtils.isNotEmpty(excludes)) {
                val isExclude = Arrays.stream(excludes!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).filter { p: String? -> Pattern.matches(p, field.name) }.findFirst().isPresent
                if (isExclude) {
                    continue
                }
            }
            val f = ClassModel.Field(propertyName(field.name!!), javaType(field.type!!), field.comment, field.notNull, null, null)
            f.column = field.name
            if (field.comment != null && field.comment!!.startsWith("JSON:")) {
                f.javaType = "Map"
            }
            if (field.maxLen != null && field.maxLen!! > 4) {
                f.maxLen = field.maxLen!! - 4
            }
            if (StringUtils.isNotEmpty(includes)) {
                val isInclude = Arrays.stream(includes!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).filter { p: String? -> Pattern.matches(p, field.name) }.findFirst().isPresent
                if (isInclude) {
                    allowFields.add(f)
                }
                continue
            }
            allowFields.add(f)
        }
        return allowFields
    }
}