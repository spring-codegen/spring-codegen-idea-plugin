package com.springcodegen.idea.plugin.ctx

import com.springcodegen.idea.plugin.config.CodeCfg
import com.springcodegen.idea.plugin.config.CodeCfg.MethodCfg
import com.springcodegen.idea.plugin.constants.MvcClassType
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.gen.model.CtrlClass
import com.springcodegen.idea.plugin.gen.model.DaoClass
import com.springcodegen.idea.plugin.util.CodeGenUtils
import com.springcodegen.idea.plugin.util.StringUtils.replacePlaceholders

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
object MethodFactory {
    @JvmStatic private fun formatText(s:String?):String {
        return replacePlaceholders(s, AppCtx.getEnvParams()) ?: ""
    }
    @JvmStatic fun getMethodCfg(classType: MvcClassType, methodType: String): MethodCfg? {
        for (m in CodeCfg.instance!!.methods!!) {
            if (MvcClassType.valueOf(m.type!!) === classType
                && methodType.indexOf(m.name!!) >= 0
            ) {
                return m
            }
        }
        return null
    }
    @JvmStatic fun createMethod(name:String, classType: MvcClassType, methodType:String): ClassModel.Method{
        var methodCfg = getMethodCfg(classType, methodType)!!
        val args: MutableList<ClassModel.MethodArg> = ArrayList()
        for (argCfg in methodCfg.args!!) {
            var argClsName = replacePlaceholders(argCfg.className, AppCtx.getEnvParams())
            var argClsModel = DomainModelCtx.getClassModelByName(argClsName!!)
            val arg = ClassModel.MethodArg( argClsModel, argCfg.refName)
            arg.isPathVar = argCfg.isPathVar
            arg.listTypeFlag = if (argCfg.listTypeFlag == null) false else argCfg.listTypeFlag!!
            arg.comment = formatText(argCfg.comment)
            args.add(arg)
        }
        var result: ClassModel.MethodResult? = null
        if (methodCfg.result != null) {
            var resultClsName = replacePlaceholders(methodCfg.result!!.className, AppCtx.getEnvParams())
            var resultClsModel = DomainModelCtx.getClassModelByName(resultClsName!!)
            result = ClassModel.MethodResult(resultClsModel, methodCfg.result!!.refName)
            if(methodCfg.result  != null){
                result.listTypeFlag = methodCfg.result?.listTypeFlag?:false
                result.outputPaged = methodCfg.result?.outputPaged?:false
            }
            result.comment = formatText(methodCfg.result?.comment)
        }

        var method: ClassModel.Method = when(classType){
            MvcClassType.CTRL -> CtrlClass.Method(name, args, result)
            MvcClassType.SVC -> ClassModel.Method(name, args, result)
            MvcClassType.DAO -> DaoClass.Method(name, args, result!!)
        }

        if (classType == MvcClassType.CTRL && methodCfg.request != null) {
            (method as CtrlClass.Method).request = CtrlClass.Request(methodCfg.request?.path!!, methodCfg.request!!.httpMethod)
        }
        method.comment = formatText(methodCfg.comment)
        method.type = methodCfg.name
        if (classType == MvcClassType.DAO) {
            (method as DaoClass.Method).sqlDataFields = CodeGenUtils.getDefaultFields(AppCtx.currentTable?.fields!!, methodCfg.sqlDataFieldIncludes, methodCfg.sqlDataFieldExcludes)
            method.sqlCondFields = CodeGenUtils.getDefaultFields(AppCtx.currentTable?.fields!!, methodCfg.sqlConditionFieldIncludes, methodCfg.sqlConditionFieldExcludes)
        }
        return method
    }
}