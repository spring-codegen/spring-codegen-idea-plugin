package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType
import com.cmcc.paas.ideaplugin.codegen.util.FieldUtils
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.DaoClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.SvcClass
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter.Handler
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationType

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
object MvcClassCtx {
    private var ctrlClass: CtrlClass = CtrlClass("{tableName}Controller")
    private var svcClass: SvcClass = SvcClass("{tableName}ServiceImpl")
    private var svcInterface: SvcClass = SvcClass("{tableName}Service")
    private var daoInterface: DaoClass = DaoClass("{tableName}Dao")
    init {
        println("MvcClassCtx init....")
        svcInterface.methods = svcClass.methods
        ctrlClass.dependency = svcClass
        svcClass.dependency = daoInterface
        svcClass.implement = svcInterface
        NotificationCenter.register(NotificationType.CODE_SETTING_UPDATED, object : Handler {
            override fun handleMessage(msg: NotificationCenter.Message) {
                println("MvcClassCtx CODE_SETTING_UPDATED....")
                refreshSettings(CodeSettingCtx)
            }
        })
        refreshSettings(CodeSettingCtx)
    }
    fun refreshSettings(projectCfg: CodeSettingCtx){
        if (projectCfg.ctrlBaseCls.isNullOrEmpty()){
            ctrlClass.extend = null
        }
        if (!projectCfg.ctrlBaseCls.isNullOrEmpty()){
            var i = CodeSettingCtx.ctrlBaseCls!!.lastIndexOf(".")
            if (i > 0) {
                var baseCtrlCls = ClassModel(CodeSettingCtx.ctrlBaseCls!!.substring(i + 1))
                baseCtrlCls.pkg = CodeSettingCtx.ctrlBaseCls!!.substring(0, i)
                ctrlClass.extend = baseCtrlCls
            }
        }
        ctrlClass.pkg = CodeSettingCtx.basePkg + ".controller." + CodeSettingCtx.module;
        svcInterface.pkg = CodeSettingCtx.basePkg + ".svc." + CodeSettingCtx.module;
        svcClass.pkg = svcInterface.pkg + ".impl";
        daoInterface.pkg = CodeSettingCtx.basePkg + ".dao." + CodeSettingCtx.module;
    }

    fun setClassName(classType: MvcClassType, className:String){
        when (classType){
            MvcClassType.CTRL -> ctrlClass.className = className
            MvcClassType.SVC -> {
                svcClass.className = "${className}Impl"
                svcInterface.className = className
            }
            MvcClassType.DAO -> daoInterface.className = className
        }
        getClassByType(classType).refName = FieldUtils.getRefName(className)
    }
    fun addMethod(classType: MvcClassType, method: ClassModel.Method): Boolean{
        var cls = getClassByType(classType)

        for (m in cls.methods){
            if (m.name.equals(method.name, true)){
                return false
            }
        }

        cls.methods.add(method)
        return true
    }
    fun removeMethod(classType: MvcClassType, methodName: String): Boolean{
        var cls = getClassByType(classType)
        for (m in cls.methods){
            if (m.name.equals(methodName, true)){
                cls.methods.remove(m)
                return true
            }
        }
        return false
    }
    fun getClassByType(classType: MvcClassType): ClassModel {
        return when (classType){
            MvcClassType.CTRL -> ctrlClass
            MvcClassType.SVC -> svcClass
            MvcClassType.DAO -> daoInterface
        }
    }
    fun getCtrlClass(): CtrlClass {
        return ctrlClass
    }
    fun getSvcClass(): SvcClass {
        return svcClass
    }
    fun getSvcInterface(): SvcClass {
        return svcInterface
    }
    fun getDaoClass(): DaoClass {
        return daoInterface
    }
}