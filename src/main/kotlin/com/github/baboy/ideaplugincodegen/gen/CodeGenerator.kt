package com.github.baboy.ideaplugincodegen.gen

import com.github.baboy.ideaplugincodegen.config.ClassGrpCfgModel
import com.github.baboy.ideaplugincodegen.config.CodeCfg
import com.github.baboy.ideaplugincodegen.config.MethodGrpCfgModel
import com.github.baboy.ideaplugincodegen.constants.AppCtx.ENV
import com.github.baboy.ideaplugincodegen.constants.EnvKey
import com.github.baboy.ideaplugincodegen.db.model.DBTable
import com.github.baboy.ideaplugincodegen.gen.define.model.ClassModel
import com.github.baboy.ideaplugincodegen.gen.template.TempRender.render
import java.util.function.Consumer

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
object CodeGenerator {
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
    fun genClass(className:String, pkg: String, fields:List<CodeCfg.FieldCfg>, dbTable:DBTable): ClassModel {
        var modelFields = ArrayList<ClassModel.Field>()
        for (i in 0 until fields.size){
            var e = fields.get(i);
            var dbField = dbTable.fields!!.stream().filter{ e2 -> e.name.equals(e2.name)}.findFirst().get()
            var field = ClassModel.Field( FieldUtils.propertyName(dbField.name!!), javaType(dbField.type!!), dbField.comment, e.notNull, FieldUtils.setter(dbField.name!!), FieldUtils.getter(dbField.name!!))
            field.column = dbField.name
            modelFields.add(field)
        }
        var classModel = ClassModel(className, pkg, dbTable.comment, modelFields)
        return classModel;
    }
    fun genMethod(methodCfgModel: MethodGrpCfgModel.MethodCfgModel, pkg: String, dbTable:DBTable): ClassModel.Method {
        val daoMethodInputClass = genClass(methodCfgModel.inputClassName!!, pkg, methodCfgModel.inputFields!!, dbTable)
        val daoMethodOutputClass = genClass(methodCfgModel.outputClassName!!, pkg, methodCfgModel.outputFields!!, dbTable)
        return ClassModel.Method(methodCfgModel.name!!, daoMethodInputClass, daoMethodOutputClass, false)
    }
    fun gen(module:String, dbTable: DBTable, classGrp: ClassGrpCfgModel, methodsGrps:List<MethodGrpCfgModel>){
        var basePkg = ENV[EnvKey.BASE_PKG].toString();
        var modelPkg = basePkg + ".model."+module
        var ctrlPkg = basePkg + ".ctrl."+module
        var svcPkg = basePkg + ".svc."+module
        var daoPkg = basePkg + ".dao."+module
        var models = HashMap<String, ClassModel>();

        

        val ctrlMethods: MutableList<ClassModel.Method> = java.util.ArrayList()
        val svcMethods: MutableList<ClassModel.Method> = java.util.ArrayList()
        val daoMethods: MutableList<ClassModel.Method> = java.util.ArrayList()
        val resultMaps: MutableMap<String, ClassModel> = HashMap()
        methodsGrps.forEach(Consumer {methodGrp ->
            if (!models.containsKey())

            val ctrlMethod = genMethod(methodGrp.ctrl!!,  modelPkg, dbTable)
            val svcMethod = genMethod(methodGrp.svc!!, modelPkg, dbTable)
            val daoMethod = genMethod(methodGrp.dao!!, modelPkg, dbTable)
            ctrlMethod.dependency = svcMethod;
            svcMethod.dependency = daoMethod;
            ctrlMethods.add(ctrlMethod)
            svcMethods.add(svcMethod)
            daoMethods.add(daoMethod)
            resultMaps[daoMethod.outputClass.className] = daoMethod.outputClass
        })
        var data = HashMap<String, Any>();
        val daoClass = ClassModel(classGrp.dao!!.className!!, ENV[EnvKey.BASE_PKG].toString() + ".dao." + module, null, null)
        daoClass.tableName = dbTable.name
        daoClass.methods = daoMethods

        data.put("daoClass", daoClass)
        data.put("resultMaps", resultMaps)

        render("mapper.ftl", data)
        render("dao.ftl", data)



        render("svc.ftl", data)
    }
}