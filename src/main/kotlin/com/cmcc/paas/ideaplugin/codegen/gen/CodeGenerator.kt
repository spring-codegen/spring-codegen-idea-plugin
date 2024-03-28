package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import kotlin.collections.ArrayList

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class CodeGenerator() {
    private var classes:MutableList<ClassModel> = ArrayList()
    private var CLS_MODEL: ClassModel = ClassModel("Model", "com.cmit.paas.common.web.model", null, null)
    private var CLS_ID_ARG: ClassModel = ClassModel("IdArg", "com.cmit.paas.common.web.model", null, null)
    private var CLS_ID_RESULT: ClassModel = ClassModel("IdResult", "com.cmit.paas.common.web.model", null, null)
    private var CLS_LIST_RESULT: ClassModel = ClassModel("ListResult", "com.cmit.paas.common.web.model", null, null)
    private var CLS_SEARCH_MODEL: ClassModel = ClassModel("PagedSearchArg", "com.cmit.paas.common.web.model", null, null)

    init {
        var a:MutableList<ClassModel.Field> = ArrayList()
        a.add( (ClassModel.Field("id","Long",null, true, "setId", "getId")))
        CLS_ID_ARG.fields = a
        CLS_ID_RESULT.fields = a
    }
    fun handleModelPkg(pkg:String, models:List<ClassModel>){
        models.forEach {

            if (!ClassModel.isBaseType(it.className)){
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
    fun gen(){

//        var f:(List<ClassModel>) -> Unit = { a:List<ClassModel> ->
//            a.forEach{cls ->
//                if(FieldUtils.isBaseType(cls.className)){
//                    if (cls.fields!= null && cls.fields!!.size > 0) {
//                        cls.refName = cls.fields!![0].name;
//                    }
//                }else{
//                    cls.refName = FieldUtils.getRefName(cls.className)
//                }
//            }
//        };
//        f(modelResult.args!!)
//        f(modelResult.results!!)
//        f(modelResult.entities!!)
        /**
         * args
         */
//        handleModelPkg(projectCfg.basePkg + ".domain.arg."+module,  modelResult.args!!)
//        handleModelPkg(projectCfg.basePkg + ".domain.result."+module, modelResult.results!!)
//        handleModelPkg(projectCfg.basePkg + ".domain.entity."+module, modelResult.entities!!)
//
//        renderModel(module, true, modelResult.args!!, projectCfg)
//        renderModel(module, false, modelResult.results!!, projectCfg)
//        renderModel(module, false, modelResult.entities!!, projectCfg)
        DomainModelGenerator.gen()
        /**
         * 处理refName
         */
//        var f2:(List<ClassModel.Method>) -> Unit = { a:List<ClassModel.Method> ->
//            a.forEach{m ->
//                if(FieldUtils.isBaseType(m.inputClass.className)){
//                    if (m.inputClass.fields!= null && m.inputClass.fields!!.size > 0) {
//                        m.inputClass.refName = m.inputClass.fields!![0].name;
//                    }
//                }
//                if(FieldUtils.isBaseType(m.outputClass.className)){
//                    if (m.outputClass.fields!= null && m.outputClass.fields!!.size > 0) {
//                        m.outputClass.refName = m.outputClass.fields!![0].name;
//                    }
//                }
//                if (m.dependency != null && FieldUtils.isBaseType(m.dependency!!.inputClass.className) && m.dependency!!.inputClass.fields!!.size > 0){
//                    m.dependency!!.inputClass.refName = m.dependency!!.inputClass.fields!![0].name
//                }
//                if (m.dependency != null && FieldUtils.isBaseType(m.dependency!!.outputClass.className)  && m.dependency!!.outputClass.fields!!.size > 0){
//                    m.dependency!!.outputClass.refName = m.dependency!!.outputClass.fields!![0].name
//                }
//            }
//        };
//        var ctrlClass = modelResult.ctrlClass
//        var svcClass = modelResult.svcClass
//        var daoClass = modelResult.daoClass

//        ctrlClass!!.dependency = svcClass
//        svcClass!!.dependency = daoClass
//        svcClass.implement = svcClass
//        var svcInterface = svcClass.clone()
//        svcClass.implement = svcInterface
//
//        var daoMapperGenerator = DaoMapperGenerator( MvcClassCtx.getDaoClass())
//        var daoInterfaceGenerator = DaoInterfaceGenerator(MvcClassCtx.getDaoClass())
//        var svcGenerator = SvcClassGenerator(MvcClassCtx.getSvcClass())
//        var svcInterfaceGenerator = SvcInterfaceGenerator(MvcClassCtx.getSvcInterface())
//        var ctrlClassGenerator = CtrlClassGenerator(MvcClassCtx.getCtrlClass())
        DaoMapperGenerator.gen()
        DaoInterfaceGenerator.gen()
        CtrlClassGenerator.gen()
        SvcInterfaceGenerator.gen()
        SvcClassGenerator.gen()
        return

//        ctrlClass.pkg = projectCfg.basePkg + ".controller."+module;
//        svcClass.pkg = projectCfg.basePkg + ".service."+module;
//        daoClass!!.pkg = projectCfg.basePkg + ".dao."+module;


//        if (projectCfg.svcBaseCls != null) {
//            var i = projectCfg.svcBaseCls!!.lastIndexOf(".")
//            if (i > 0) {
//                var baseSvcCls = ClassModel(projectCfg.svcBaseCls!!.substring(i + 1))
//                baseSvcCls.pkg = projectCfg.svcBaseCls!!.substring(0, i)
//                svcClass.extend = baseSvcCls
//            }
//        }
//        processImports(ctrlClass)
//        processImports(svcClass)
//        processImports(daoClass)

//        f2(ctrlClass.methods!!)
//        f2(svcClass.methods!!)

//        var data = HashMap<String, Any?>();
//        data["project"] = projectCfg
//        data["ctrlClass"] = ctrlClass
//        data["svcClass"] = svcClass
//        data["daoClass"] = daoClass
//        data["entityClass"] = modelResult.entities!![0]
//        modelResult.args!!.forEach {
//            if (it.className.endsWith("SearchArg")){
//                data["searchClass"] = it
//            }
//        }
//        renderToFile(
//                projectCfg.ctrlSourceDir!!,
//                ctrlClass.pkg!!,
//                ctrlClass.className,
//                "ctrl.ftl",
//                data
//        )
//        renderToFile(
//                projectCfg.svcSourceDir!!,
//                svcClass.pkg!!,
//                svcClass.className,
//                "svc.ftl",
//                data
//        )
//        renderToFile(
//                projectCfg.svcSourceDir!!,
//                svcClass.pkg!!+".impl",
//                svcClass.className+"Impl",
//                "svc-impl.ftl",
//                data
//        )
//        renderToFile(
//                projectCfg.svcSourceDir!!,
//                daoClass.pkg!!,
//                daoClass.className,
//                "dao.ftl",
//                data
//        )
//        val resultMaps: MutableMap<String, ClassModel> = HashMap()
//        if(modelResult.entities != null){
//            modelResult.entities!!.forEach { resultMaps.put(it.className, it) }
//        }
//        data.put("resultMaps", resultMaps)
//
//        renderToFile(
//                projectCfg.mybatisMapperDir!!+"/mappers/"+module+"/"+daoClass.className+"Mapper.xml",
//                "mapper.ftl",
//                data
//        )
//        return
    }
}