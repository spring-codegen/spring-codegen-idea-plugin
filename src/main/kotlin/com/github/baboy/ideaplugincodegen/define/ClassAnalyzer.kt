package com.github.baboy.ideaplugincodegen.define

import com.github.baboy.ideaplugincodegen.config.CodeCfg
import com.github.baboy.ideaplugincodegen.db.model.DBTable
import com.github.baboy.ideaplugincodegen.define.model.ModelDefine
import com.github.baboy.ideaplugincodegen.gen.FieldUtils

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object ClassAnalyzer {
    fun javaType(dbType:String):String{
        if ("int2".equals(dbType)){
            return "Boolean"
        }
        if ("int4".equals(dbType)){
            return "Integer"
        }
        if ("int8".equals(dbType)){
            return "Long"
        }
        if (dbType.indexOf("time") >= 0 || dbType.indexOf("date") >= 0){
            return "Date"
        }
        return "String";
    }
    fun parseModel(className:String, pkg: String, fields:List<CodeCfg.FieldCfg>, dbTable:DBTable):ModelDefine{
        var modelFields = ArrayList<ModelDefine.Field>()
        for (i in 0 until fields.size){
            var e = fields.get(i);
            var dbField = dbTable.fields!!.stream().filter{ e2 -> e.name.equals(e2.name)}.findFirst().get()
            modelFields.add(ModelDefine.Field( FieldUtils.propertyName(dbField.name!!), javaType(dbField.type!!), dbField.comment, e.notNull, FieldUtils.setter(dbField.name!!), FieldUtils.getter(dbField.name!!)))
        }
        var modelDefine = ModelDefine(className, pkg, dbTable.comment, modelFields)
        return modelDefine;
    }
}