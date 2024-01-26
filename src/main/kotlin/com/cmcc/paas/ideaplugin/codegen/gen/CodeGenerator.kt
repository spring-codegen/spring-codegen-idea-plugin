package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.template.TempRender.renderToFile
import com.intellij.util.containers.stream
import java.util.*
import java.util.function.Function
import kotlin.collections.ArrayList

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class CodeGenerator {
    private var classes:MutableList<ClassModel> = ArrayList()
    private var CLS_MODEL:ClassModel = ClassModel("Model", "com.cmit.paas.common.web.model", null, null)
    private var CLS_ID_ARG:ClassModel = ClassModel("IdArg", "com.cmit.paas.common.web.model", null, null)
    private var CLS_ID_RESULT:ClassModel = ClassModel("IdResult", "com.cmit.paas.common.web.model", null, null)
    private var CLS_LIST_RESULT:ClassModel = ClassModel("ListResult", "com.cmit.paas.common.web.model", null, null)
    private var CLS_SEARCH_MODEL:ClassModel = ClassModel("PagedSearchArg", "com.cmit.paas.common.web.model", null, null)

    constructor(){
        var a:MutableList<ClassModel.Field> = ArrayList();
        a.add( (ClassModel.Field("id","Long",null, true, "setId", "getId")))
        CLS_ID_ARG.fields = a
        CLS_ID_RESULT.fields = a
    }
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
    fun defaultClassVarName(clsName:String):String{
        if (isBaseType(clsName)){
            return clsName.substring(0,1).toLowerCase(Locale.getDefault())
        }
        return clsName.substring(0,1).toLowerCase() + clsName.substring(1)
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

    fun processImports(cls:ClassModel){
        var imports:MutableSet<String> = HashSet();
        if (cls.fields != null){
            cls!!.fields!!.forEach{f ->
                if ( f.javaType == "Date" ){
                    imports.add("java.util." + f.javaType)
                }else if (f.pkg != null && !FieldUtils.isBaseType(f.javaType)) {
                    imports.add(f.pkg + "." + f.javaType)
                }
             }
        }
        if (cls.methods != null){
            cls.methods!!.forEach{ m ->
                run {
                    if (m.inputClass.pkg != null) {
                        imports.add(m.inputClass.pkg + "." + m.inputClass.className)
                    }
                    if (m.outputClass.pkg != null) {
                        imports.add(m.outputClass.pkg + "." + m.outputClass.className)
                    }
                    if (m.resultListFlag) {
                        imports.add("java.util.List")
                    }
                    if (m.dependency != null ){
                        if ( m.dependency!!.inputClass.pkg != null ){
                            imports.add(m.dependency!!.inputClass.pkg + "." + m.dependency!!.inputClass.className)
                        }
                        if ( m.dependency!!.outputClass.pkg != null ){
                            imports.add(m.dependency!!.outputClass.pkg + "." + m.dependency!!.outputClass.className)
                        }
                    }
                }
            }
        }
        if (cls.implement != null){
            imports.add(cls.implement!!.pkg+"."+cls.implement!!.className)
        }
        if (cls.extend != null){
            imports.add(cls.extend!!.pkg+"."+cls.extend!!.className)
        }
        if (cls.dependency != null){
            imports.add(cls.dependency!!.pkg+"."+cls.dependency!!.className)
        }
        cls.imports = imports
    }
    /*
    fun getClass(className:String, pkg: String, fields:List<CodeCfg.FieldDefine>, dbTable:DBTable): ClassModel {
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
        inputClass.name = defaultClassVarName(inputClass.className)
        outputClass.name = defaultClassVarName(outputClass.className)
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
            cls.baseType = true
            return
        }
        cls.fields!!.forEach{
            it.baseType = baseTypes().stream().anyMatch{ e -> e.equals(it.javaType)}
            if (it.baseType){
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

    @OptIn(ExperimentalStdlibApi::class)
    fun gen(module:String, dbTable: DBTable, classGrp: ClassGrpCfgModel, methodsGrps:List<MethodGrpCfgModel>, projectCfg:ProjectCfg){

        var modelPkg = projectCfg.basePkg + ".model."+module
        var ctrlPkg = projectCfg.basePkg + ".ctrl."+module
        var svcPkg = projectCfg.basePkg + ".svc."+module
        var daoPkg = projectCfg.basePkg + ".dao."+module
        var models = HashMap<String, ClassModel>();

        val ctrlMethods: MutableList<ClassModel.Method> = java.util.ArrayList()
        val svcMethods: MutableList<ClassModel.Method> = java.util.ArrayList()
        val daoMethods: MutableList<ClassModel.Method> = java.util.ArrayList()
        val resultMaps: MutableMap<String, ClassModel> = HashMap()
        methodsGrps.forEach(Consumer {methodGrp ->

            val ctrlMethod = getMethod(methodGrp.ctrl!!,  modelPkg, dbTable)
            val svcMethod = getMethod(methodGrp.svc!!, modelPkg, dbTable)
            val daoMethod = getMethod(methodGrp.dao!!, modelPkg, dbTable)

//            ctrlMethod.request = ClassModel.RequestURI(methodGrp.request!!.httpMethod, methodGrp.request!!.path)
            ctrlMethod.dependency = svcMethod;
            svcMethod.dependency = daoMethod;
            ctrlMethods.add(ctrlMethod)
            svcMethods.add(svcMethod)
            daoMethods.add(daoMethod)

            if (ctrlMethod.resultListFlag && ctrlMethod.paged){
                var daoMethod2 = daoMethod.clone()
                daoMethod2.outputClass.className = "Integer";
                daoMethod2.outputClass.pkg = "java.lang";
                daoMethod2.name = String.format("get%sCount", daoMethod2.name.capitalize(Locale.getDefault()))
                daoMethod2.resultListFlag = false
                daoMethod2.paged = false

                var svcMethod2 = svcMethod.clone()
                svcMethod2.outputClass.className = "Integer";
                svcMethod2.outputClass.pkg = "java.lang";
                svcMethod2.name = daoMethod2.name
                svcMethod2.resultListFlag = false
                svcMethod2.paged = false
                svcMethod2.dependency = daoMethod2
                svcMethods.add(svcMethod2)
                daoMethods.add(daoMethod2)
            }

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
        val daoClass = ClassModel(classGrp.dao!!.className!!, daoPkg, null, null)
        daoClass.tableName = dbTable.name
        daoClass.methods = daoMethods
        daoClass.name = defaultClassVarName(daoClass.className)
        processImports(daoClass)

        data.put("project", projectCfg)
        data.put("daoClass", daoClass)
        data.put("resultMaps", resultMaps)

        val svcClass = ClassModel(classGrp.svc!!.className!!, svcPkg, null, null)
        svcClass.tableName = dbTable.name
        svcClass.methods = svcMethods
        svcClass.name = defaultClassVarName(svcClass.className)


        processImports(svcClass)
        val ctrlClass = ClassModel(classGrp.ctrl!!.className!!, ctrlPkg, null, null)
        ctrlClass.comment = dbTable.comment
        ctrlClass.tableName = dbTable.name
        ctrlClass.methods = ctrlMethods
        ctrlClass.dependency = svcClass
//        ctrlClass.request = ClassModel.RequestURI(null, classGrp.ctrl!!.baseURI)
        ctrlClass.name = defaultClassVarName(ctrlClass.className)



        var i = projectCfg.ctrlBaseCls!!.lastIndexOf(".")
        ctrlClass.extend = ClassModel(projectCfg.ctrlBaseCls!!.substring(i + 1), projectCfg.ctrlBaseCls!!.substring(0, i), null, null)
        processImports(ctrlClass)


        var svcClassImpl = svcClass.clone()
        svcClassImpl.pkg = svcClassImpl.pkg + ".impl";
        svcClassImpl.className = svcClassImpl.className +"Impl";
        svcClassImpl.implement = svcClass;
        svcClassImpl.dependency = daoClass
        processImports(svcClassImpl)


        models.keys.forEach{
            var m = models[it]
            var i = projectCfg.modelBaseCls!!.lastIndexOf(".")
            m!!.implement = ClassModel(projectCfg.modelBaseCls!!.substring(i + 1), projectCfg.modelBaseCls!!.substring(0, i), null, null)
            data["model"] = m
            processImports(m!!)
            renderToFile(projectCfg.sourceDir!!, m.pkg,m.className,"model.ftl", data)
        }
        data.put("ctrlClass", ctrlClass)
        data.put("svcClass", svcClass)
        data.put("svcClassImpl", svcClassImpl)
        data.put("daoClass", daoClass)
        data.put("resultMaps", resultMaps)

        renderToFile(
            projectCfg.sourceDir!!,
            daoClass.pkg,
            daoClass.className,
            "dao.ftl",
            data
        )
        renderToFile(
            projectCfg.mybatisMapperDir!!+"/"+module+"/"+daoClass.className+"Mapper.xml",
            "mapper.ftl",
            data
        )
        renderToFile(
            projectCfg.sourceDir!!,
            svcClass.pkg,
            svcClass.className,
            "svc.ftl",
            data
        )
        renderToFile(
            projectCfg.sourceDir!!,
            svcClassImpl.pkg,
            svcClassImpl.className,
            "svc-impl.ftl",
            data
        )
        renderToFile(
            projectCfg.sourceDir!!,
            ctrlClass.pkg,
            ctrlClass.className,
            "ctrl.ftl",
            data
        )
    }
    */

    fun handleModelPkg(pkg:String, models:List<ClassModel>){
        models?.forEach {

            if (!FieldUtils.isBaseType(it.className)){
                it.pkg = pkg;
            }
            if (it.className == "IdArg"){
                it.pkg = CLS_ID_ARG.pkg
                it.fields = CLS_ID_ARG.fields
            }

            if (it.className == "IdResult"){
                it.pkg = CLS_ID_RESULT.pkg
                it.fields = CLS_ID_RESULT.fields
            }
            it.extend = CLS_MODEL
            if (it.className.endsWith("SearchArg")){
                it.extend = CLS_SEARCH_MODEL
            }

        }
    }
    fun renderModel(module:String, validator:Boolean, models:List<ClassModel>, projectCfg: ProjectCfg){

        models?.forEach {
            if (!FieldUtils.isBaseType(it.className) && !FieldUtils.isCommonType(it.className) ){
                var data = HashMap<String, Any?>();
                data["project"] = projectCfg
                data["model"] = it
                data["validator"] = validator
                it.fields?.forEach{
                    it.setter = FieldUtils.setter(it.name!!)
                    it.getter = FieldUtils.getter(it.name!!)
                }
                processImports(it)
                             renderToFile(projectCfg.sourceDir!!, it.pkg!!, it.className,"model.ftl", data)
            }
        }
    }
    fun gen(module:String, modelResult:ModelResult, projectCfg: ProjectCfg){

        var f:(List<ClassModel>) -> Unit = { a:List<ClassModel> ->
            a.forEach{cls ->
                if(FieldUtils.isBaseType(cls.className)){
                    if (cls.fields!= null && cls.fields!!.size > 0) {
                        cls.refName = cls.fields!![0].name;
                    }
                }else{
                    cls.refName = FieldUtils.getRefName(cls.className)
                }
            }
        };
        f(modelResult.args!!)
        f(modelResult.results!!)
        f(modelResult.entities!!)
        /**
         * args
         */
        handleModelPkg(projectCfg.basePkg + ".domain.arg."+module,  modelResult.args!!)
        handleModelPkg(projectCfg.basePkg + ".domain.results."+module, modelResult.results!!)
        handleModelPkg(projectCfg.basePkg + ".domain.entities."+module, modelResult.entities!!)

        renderModel(module, true, modelResult.args!!, projectCfg)
        renderModel(module, false, modelResult.results!!, projectCfg)
        renderModel(module, false, modelResult.entities!!, projectCfg)
        /**
         *
         */
        var f2:(List<ClassModel.Method>) -> Unit = { a:List<ClassModel.Method> ->
            a.forEach{m ->
                if(FieldUtils.isBaseType(m.inputClass.className)){
                    if (m.inputClass.fields!= null && m.inputClass.fields!!.size > 0) {
                        m.inputClass.refName = m.inputClass.fields!![0].name;
                    }
                }
                if(FieldUtils.isBaseType(m.outputClass.className)){
                    if (m.outputClass.fields!= null && m.outputClass.fields!!.size > 0) {
                        m.outputClass.refName = m.outputClass.fields!![0].name;
                    }
                }
                if (m.dependency != null && FieldUtils.isBaseType(m.dependency!!.inputClass.className) && m.dependency!!.inputClass.fields!!.size > 0){
                    m.dependency!!.inputClass.refName = m.dependency!!.inputClass.fields!![0].name
                }
                if (m.dependency != null && FieldUtils.isBaseType(m.dependency!!.outputClass.className)  && m.dependency!!.outputClass.fields!!.size > 0){
                    m.dependency!!.outputClass.refName = m.dependency!!.outputClass.fields!![0].name
                }
            }
        };
        var ctrlClass = modelResult.ctrlClass
        var svcClass = modelResult.svcClass
        var daoClass = modelResult.daoClass

        ctrlClass!!.dependency = svcClass
        svcClass!!.dependency = daoClass
        svcClass.implement = svcClass

        ctrlClass!!.pkg = projectCfg.basePkg + ".ctrl."+module;
        svcClass!!.pkg = projectCfg.basePkg + ".svc."+module;
        daoClass!!.pkg = projectCfg.basePkg + ".dao."+module;

        processImports(ctrlClass)
        processImports(svcClass)
        processImports(daoClass)

        f2(ctrlClass!!.methods!!)
        f2(svcClass!!.methods!!)
        var data = HashMap<String, Any?>();
        data["project"] = projectCfg
        data["ctrlClass"] = ctrlClass
        data["svcClass"] = svcClass
        data["daoClass"] = daoClass
        renderToFile(
                projectCfg.sourceDir!!,
                ctrlClass.pkg!!,
                ctrlClass.className,
                "ctrl.ftl",
                data
        )
        renderToFile(
                projectCfg.sourceDir!!,
                svcClass.pkg!!,
                svcClass.className,
                "svc.ftl",
                data
        )
        renderToFile(
                projectCfg.sourceDir!!,
                svcClass.pkg!!+".impl",
                svcClass.className+"Impl",
                "svc-impl.ftl",
                data
        )
        renderToFile(
                projectCfg.sourceDir!!,
                daoClass.pkg!!,
                daoClass.className,
                "dao.ftl",
                data
        )
    }
}