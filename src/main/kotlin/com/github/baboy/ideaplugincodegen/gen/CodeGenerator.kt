package com.github.baboy.ideaplugincodegen.gen

import com.github.baboy.ideaplugincodegen.config.ClassGrpCfgModel
import com.github.baboy.ideaplugincodegen.config.CodeCfg
import com.github.baboy.ideaplugincodegen.config.MethodGrpCfgModel
import com.github.baboy.ideaplugincodegen.constants.AppCtx.ENV
import com.github.baboy.ideaplugincodegen.constants.EnvKey
import com.github.baboy.ideaplugincodegen.db.model.DBTable
import com.github.baboy.ideaplugincodegen.gen.define.model.ClassModel
import com.github.baboy.ideaplugincodegen.gen.template.TempRender.render
import com.intellij.util.containers.stream
import org.apache.commons.lang.mutable.Mutable
import java.util.function.Consumer

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class CodeGenerator {
    private var classes:MutableList<ClassModel> = ArrayList()
    fun addClass(cls:ClassModel){
        var t = classes.stream().anyMatch{it.className == cls.className}
        if (!t){
            classes.add(cls)
        }
    }
    fun baseTypes():Array<String>{
        return arrayOf("Integer","Long","Boolean", "String", "Date", "BigDecimal", "List", "Map")
    }
    fun baseTypePkg(type:String):String?{
        if (type == "Integer" || type == "Long" || type == "Boolean" || type == "String"){
            return "java.lang"
        }
        if (type == "BigDecimal" ){
            return "java.math"
        }
        if (type == "Date" || type == "List" || type == "Map"){
            return "java.util"
        }
        return null;
    }
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
    fun getClass(className:String, pkg: String, fields:List<CodeCfg.FieldCfg>, dbTable:DBTable): ClassModel {
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
    fun getMethod(methodCfgModel: MethodGrpCfgModel.MethodCfgModel, pkg: String, dbTable:DBTable): ClassModel.Method {

        val daoMethodInputClass = getClass(methodCfgModel.inputClassName!!, pkg, methodCfgModel.inputFields!!, dbTable)
        val daoMethodOutputClass = getClass(methodCfgModel.outputClassName!!, pkg, methodCfgModel.outputFields!!, dbTable)
        return ClassModel.Method(methodCfgModel.name!!, daoMethodInputClass, daoMethodOutputClass, false)
    }
    fun processModel(cls:ClassModel, models:HashMap<String, ClassModel>){
        if (baseTypes().stream().anyMatch{e -> e.equals(cls.className)}){
            cls.pkg = baseTypePkg(cls.className)!!
            cls.isBaseType = true
            return
        }
        cls.fields!!.forEach{
            it.isBaseType = baseTypes().stream().anyMatch{e -> e.equals(it.javaType)}
            if (it.isBaseType){
                it.pkg = baseTypePkg(it.javaType)!!
            }
        }
        if(!models.contains(cls.className)){
            models[cls.className] = cls
            return;
        }
        var fields:MutableList<ClassModel.Field>? = models[cls.className]!!.fields
        if (fields == null){
            fields = ArrayList<ClassModel.Field>()
        }
        cls.fields!!.forEach {
            var t = fields.stream().anyMatch{e -> e.name == it.name}
            if (!t){
                fields.add(it);
            }
        }
        models[cls.className]!!.fields = fields
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

            val ctrlMethod = getMethod(methodGrp.ctrl!!,  modelPkg, dbTable)
            val svcMethod = getMethod(methodGrp.svc!!, modelPkg, dbTable)
            val daoMethod = getMethod(methodGrp.dao!!, modelPkg, dbTable)
            ctrlMethod.dependency = svcMethod;
            svcMethod.dependency = daoMethod;
            ctrlMethods.add(ctrlMethod)
            svcMethods.add(svcMethod)
            daoMethods.add(daoMethod)
            resultMaps[daoMethod.outputClass.className] = daoMethod.outputClass
            processModel(ctrlMethod.inputClass, models);
            processModel(ctrlMethod.outputClass, models);
            processModel(svcMethod.inputClass, models);
            processModel(svcMethod.outputClass, models);
            processModel(daoMethod.inputClass, models);
            processModel(daoMethod.outputClass, models);
        })
        var data = HashMap<String, Any?>();
        ENV.keys.forEach{
          data.put(it, ENV[it]!!);
        }
        models.keys.forEach{
            var m = models[it]
            data["model"] = m
            var imports:MutableSet<String> = HashSet();
            m!!.fields!!.forEach{f -> imports.add(f.pkg+"."+f.javaType)}
            m.imports = imports
            render("model.ftl", data);
        }
        return

        val daoClass = ClassModel(classGrp.dao!!.className!!, daoPkg, null, null)
        daoClass.tableName = dbTable.name
        daoClass.methods = daoMethods

        data.put("daoClass", daoClass)
        data.put("resultMaps", resultMaps)

//        render("mapper.ftl", data)
//        render("dao.ftl", data)



        render("svc.ftl", data)
    }
}