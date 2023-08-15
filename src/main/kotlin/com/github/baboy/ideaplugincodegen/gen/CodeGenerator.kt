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
import org.apache.commons.beanutils.BeanUtils
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
    fun isBaseType(clsName:String):Boolean{
        return baseTypes().stream().anyMatch{e -> e.equals(clsName)}
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

        val inputClass = getClass(methodCfgModel.inputClassName!!, pkg, methodCfgModel.inputFields!!, dbTable)
        val outputClass = getClass(methodCfgModel.outputClassName!!, pkg, methodCfgModel.outputFields!!, dbTable)
        inputClass.name = inputClass.className.substring(0,1).toLowerCase() + inputClass.className.substring(1)
        outputClass.name = outputClass.className.substring(0,1).toLowerCase() + outputClass.className.substring(1)
        if (isBaseType(inputClass.className) && inputClass.fields != null && inputClass.fields!!.size == 1){
            inputClass.name = inputClass.fields!![0].name
        }
        if (isBaseType(outputClass.className) && outputClass.fields != null && outputClass.fields!!.size == 1){
            outputClass.name = outputClass.fields!![0].name
        }
        var method = ClassModel.Method(methodCfgModel.name!!, inputClass, outputClass, if (methodCfgModel.outputListTypeFlag == null) false else methodCfgModel.outputListTypeFlag!!)
        method.comment = methodCfgModel.comment
        method.paged = methodCfgModel.outputPaged!!
        return method
    }
    fun processModel(cls:ClassModel, models:HashMap<String, ClassModel>){
        if (cls.className == "-"){
            return
        }
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
    fun processImports(cls:ClassModel){
        var imports:MutableSet<String> = HashSet();
        if (cls.fields != null){
            cls!!.fields!!.forEach{f -> imports.add(f.pkg+"."+f.javaType)}
        }
        if (cls.methods != null){
            cls.methods!!.forEach{ m ->
                run {
                    imports.add(m.inputClass.pkg+"." + m.inputClass.className)
                    if (m.outputClass.className != "-"){
                        imports.add(m.outputClass.pkg+"." + m.outputClass.className)
                    }
                }
            }
        }
        cls.imports = imports
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
            ctrlMethod.request = ClassModel.RequestURI(methodGrp.request!!.httpMethod, methodGrp.request!!.path)
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
            processImports(m!!)
            render("model.ftl", data);
        }
        val daoClass = ClassModel(classGrp.dao!!.className!!, daoPkg, null, null)
        daoClass.tableName = dbTable.name
        daoClass.methods = daoMethods
        daoClass.name = daoClass.className.substring(0,1).toLowerCase() + daoClass.className.substring(1)
        processImports(daoClass)

        data.put("daoClass", daoClass)
        data.put("resultMaps", resultMaps)

        val ctrlClass = ClassModel(classGrp.ctrl!!.className!!, ctrlPkg, null, null)
        ctrlClass.comment = dbTable.comment
        ctrlClass.tableName = dbTable.name
        ctrlClass.methods = ctrlMethods
        ctrlClass.request = ClassModel.RequestURI(null, classGrp.ctrl!!.baseURI)
        ctrlClass.name = ctrlClass.className.substring(0,1).toLowerCase() + ctrlClass.className.substring(1)
        processImports(ctrlClass)

        val svcClass = ClassModel(classGrp.svc!!.className!!, svcPkg, null, null)
        svcClass.tableName = dbTable.name
        svcClass.methods = svcMethods
        svcClass.name = svcClass.className.substring(0,1).toLowerCase() + svcClass.className.substring(1)


        processImports(svcClass)

        var svcClassImpl = svcClass.clone()
        svcClassImpl.pkg = svcClassImpl.pkg + ".impl";
        svcClassImpl.className = svcClassImpl.className +"Impl";

        data.put("ctrlClass", ctrlClass)
        data.put("svcClass", svcClass)
        data.put("svcClassImpl", svcClassImpl)
        data.put("daoClass", daoClass)
        data.put("resultMaps", resultMaps)




//        render("mapper.ftl", data)
//        render("dao.ftl", data)



//        render("ctrl.ftl", data)
        render("svc.ftl", data)
        render("svc-impl.ftl", data)
//        render("dao.ftl", data)
    }
}