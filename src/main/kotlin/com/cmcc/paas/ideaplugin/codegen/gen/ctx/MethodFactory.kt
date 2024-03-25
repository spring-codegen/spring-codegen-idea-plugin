package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg
import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx.ENV
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.model.DaoClass
import com.cmcc.paas.ideaplugin.codegen.util.CodeGenUtils
import com.cmcc.paas.ideaplugin.codegen.util.StringUtils.replacePlaceholders

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
object MethodFactory {
    private fun formatText(s:String?):String {
        return replacePlaceholders(s, ENV) ?: ""
    }
    fun createMethod(name:String, classType: MvcClassType, methodCfg:CodeCfg.MethodCfg): ClassModel.Method{

        val args: MutableList<ClassModel.MethodArg> = ArrayList()
        for (argCfg in methodCfg.args!!) {
            var argClsName = replacePlaceholders(argCfg.className, ENV)
            var argClsModel = DomainModelCtx.getClassModelByName(argClsName!!)
            val arg = ClassModel.MethodArg( argClsModel, argCfg.refName)
            arg.isPathVar = argCfg.isPathVar
            arg.listTypeFlag = if (argCfg.listTypeFlag == null) false else argCfg.listTypeFlag!!
            arg.comment = formatText(argCfg.comment)
            args.add(arg)
        }
        var result: ClassModel.MethodResult? = null
        if (methodCfg.result != null) {
            var resultClsName = replacePlaceholders(methodCfg.result!!.className, ENV)
            var resultClsModel = DomainModelCtx.getClassModelByName(resultClsName!!)
            result = ClassModel.MethodResult(resultClsModel, methodCfg.result!!.refName)
            result.listTypeFlag = if(methodCfg.result  ==null) false else methodCfg.result?.listTypeFlag!!
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
        if (classType == MvcClassType.DAO) {
            (method as DaoClass.Method).sqlDataFields = CodeGenUtils.getDefaultFields(AppCtx.currentTable?.fields!!, methodCfg.sqlDataFieldIncludes, methodCfg.sqlDataFieldExcludes)
            method.sqlCondFields = CodeGenUtils.getDefaultFields(AppCtx.currentTable?.fields!!, methodCfg.sqlConditionFieldIncludes, methodCfg.sqlConditionFieldExcludes)
        }
        return method
    }
}