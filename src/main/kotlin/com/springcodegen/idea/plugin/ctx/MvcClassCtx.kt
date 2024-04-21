package com.springcodegen.idea.plugin.ctx

import com.springcodegen.idea.plugin.constants.MvcClassType
import com.springcodegen.idea.plugin.util.FieldUtils
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.gen.model.CtrlClass
import com.springcodegen.idea.plugin.gen.model.DaoClass
import com.springcodegen.idea.plugin.gen.model.SvcClass
import com.springcodegen.idea.plugin.notify.NotificationCenter
import com.springcodegen.idea.plugin.notify.NotificationCenter.Handler
import com.springcodegen.idea.plugin.notify.NotificationType
import org.apache.commons.lang3.StringUtils

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
                refreshSettings()
            }
        })
        refreshSettings()
    }
    fun refreshSettings(){
        ctrlClass.extend = if (CodeSettingCtx.ctrlBaseCls.isNullOrEmpty()) null else ClassModel.parse(CodeSettingCtx.ctrlBaseCls)
        ctrlClass.pkg = CodeSettingCtx.basePkg + ".controller." + CodeSettingCtx.module;
        svcInterface.pkg = CodeSettingCtx.basePkg + ".service." + CodeSettingCtx.module;
        svcClass.pkg = svcInterface.pkg + ".impl";
        daoInterface.pkg = CodeSettingCtx.basePkg + ".dao." + CodeSettingCtx.module;
        daoInterface.extend = if(CodeSettingCtx.daoBaseCls.isNullOrEmpty()) null else ClassModel.parse(CodeSettingCtx.daoBaseCls)
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
        if (StringUtils.isEmpty(className)){
            return
        }
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