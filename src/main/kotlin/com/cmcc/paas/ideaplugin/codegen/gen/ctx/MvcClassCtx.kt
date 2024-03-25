package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationType
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.DaoClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.SvcClass
import com.cmcc.paas.ideaplugin.codegen.notify.Messages
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
object MvcClassCtx {
    private var ctrlClass: CtrlClass = CtrlClass("{tableName}Controller")
    private var svcClass: SvcClass = SvcClass("{tableName}Service")
    private var daoClass: DaoClass = DaoClass("{tableName}Dao")
    init {
//        NotificationCenter.register(NotificationType.METHOD_UPDATED, object: NotificationCenter.Handler {
//            override fun handleMessage(msg: NotificationCenter.Message) {
//                var d = msg.data as Messages.MethodUpdateData
//                updateMethod(d.classType, d.method)
//            }
//        })

    }

    fun setClassName(classType: MvcClassType, className:String){
        when (classType){
            MvcClassType.CTRL -> ctrlClass.className = className
            MvcClassType.SVC -> svcClass.className = className
            MvcClassType.DAO -> daoClass.className = className
        }
        getClassByType(classType).refName = FieldUtils.getRefName(className)
    }
    fun addMethod(classType: MvcClassType, method: ClassModel.Method): Boolean{
        var cls = getClassByType(classType)
        if (cls.methods != null){
            for (m in cls.methods!!){
                if (m.name.equals(method.name, true)){
                    return false
                }
            }
        }
        cls.methods?.add(method)
        return true
    }
    fun removeMethod(classType: MvcClassType, methodName: String): Boolean{
        var cls = getClassByType(classType)
        for (m in cls.methods!!){
            if (m.name.equals(methodName, true)){
                cls.methods?.remove(m)
                return true
            }
        }
        return false
    }
    fun getClassByType(classType: MvcClassType): ClassModel {
        return when (classType){
            MvcClassType.CTRL -> ctrlClass
            MvcClassType.SVC -> svcClass
            MvcClassType.DAO -> daoClass
        }
    }
    fun getCtrlClass(): CtrlClass {
        return ctrlClass
    }
    fun getSvcClass(): SvcClass {
        return svcClass
    }
    fun getDaoClass(): DaoClass {
        return daoClass
    }
}