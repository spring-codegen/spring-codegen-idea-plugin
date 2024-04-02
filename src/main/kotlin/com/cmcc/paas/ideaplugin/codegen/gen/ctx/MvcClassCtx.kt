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
        ctrlClass.extend = if (projectCfg.ctrlBaseCls.isNullOrEmpty()) null else ClassModel.parse(projectCfg.ctrlBaseCls!!)
        ctrlClass.pkg = CodeSettingCtx.basePkg + ".controller." + CodeSettingCtx.module;
        svcInterface.pkg = CodeSettingCtx.basePkg + ".svc." + CodeSettingCtx.module;
        svcClass.pkg = svcInterface.pkg + ".impl";
        daoInterface.pkg = CodeSettingCtx.basePkg + ".dao." + CodeSettingCtx.module;
        daoInterface.extend = if(projectCfg.daoBaseCls.isNullOrEmpty()) null else ClassModel.parse(projectCfg.daoBaseCls!!)
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
        var cls = getClassByType(classType)
        cls.refName = FieldUtils.getRefName(className)
        NotificationCenter.sendMessage(NotificationType.MVC_CLASS_UPDATED, cls);
    }
    fun resetClass(classType: MvcClassType, className:String){
        setClassName(classType, className)
        when (classType){
            MvcClassType.CTRL -> ctrlClass.methods.clear()
            MvcClassType.SVC -> svcClass.methods.clear()
            MvcClassType.DAO -> daoInterface.methods.clear()
        }
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