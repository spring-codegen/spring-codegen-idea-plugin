package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg
import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg.ModelCfg
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx.ENV
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx.currentTable
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter.sendMessage
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationType
import com.cmcc.paas.ideaplugin.codegen.util.CodeGenUtils.getDefaultFields
import com.cmcc.paas.ideaplugin.codegen.util.FieldUtils
import com.cmcc.paas.ideaplugin.codegen.util.StringUtils.replacePlaceholders
import org.apache.commons.lang3.StringUtils
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 *
 * @author zhangyinghui
 * @date 2024/3/20
 */
object DomainModelCtx {
    private var maps:MutableMap<DomainType, MutableList<ClassModel>> = HashMap()
    init{
        NotificationCenter.register(NotificationType.CODE_SETTING_UPDATED, object : NotificationCenter.Handler {
            override fun handleMessage(msg: NotificationCenter.Message) {
                println("MvcClassCtx CODE_SETTING_UPDATED....")
                refreshSettings()
            }
        })
        refreshSettings()
    }
    fun refreshSettings(){
        getModesByTypes(DomainType.ARG).forEach {
            if (!ClassModel.isInnerClass(it.className)) {
                it.pkg = CodeSettingCtx.basePkg + ".domain.arg." + CodeSettingCtx.module
            }
        }
        getModesByTypes(DomainType.ENTITY).forEach {
            if (!ClassModel.isInnerClass(it.className)) {
                it.pkg = CodeSettingCtx.basePkg + ".domain.entity." + CodeSettingCtx.module
            }
        }
        getModesByTypes(DomainType.RESULT).forEach {
            if (!ClassModel.isInnerClass(it.className)) {
                it.pkg = CodeSettingCtx.basePkg + ".domain.result." + CodeSettingCtx.module
            }
        }
    }
    fun createModel(className:String):ClassModel{
        for (x in ClassModel.innerClasses()){
            if (x.className.equals(className, true)){
                if (x.refName == null){
                    x.refName = FieldUtils.getRefName(className)
                }
                return x
            }
        }
        var cls = ClassModel(className)
        cls.refName = FieldUtils.getRefName(className)
        return cls
    }
    fun _addModel(type:DomainType, classModel: ClassModel){
        if (!maps.containsKey(type)){
            maps[type] = ArrayList<ClassModel>()
        }
        var isExists = maps[type]!!.stream().anyMatch { it.className.equals(classModel.className, true) }
        if (isExists){
            return
        }
        if ( classModel.fields != null ){
            for (x in classModel.fields!!){
                x.setter = x.setter?:FieldUtils.setter(x.name)
                x.getter = x.getter?:FieldUtils.getter(x.name)
            }
        }
        classModel.pkg =  CodeSettingCtx.basePkg + ".domain." + type.toString().toLowerCase() + "." + CodeSettingCtx.module
        maps[type]!!.add(classModel)
    }
    fun addModel(type:DomainType, classModel: ClassModel){
        _addModel(type, classModel)
        sendMessage(NotificationType.MODEL_ADDED, classModel)
    }
    fun removeModel(className: String){
        for( a in maps.values){
            for(  j in 0 until a.size){
                if (a[j].className.equals(className, true)){
                    a.remove(a[j])
                    return
                }
            }
        }
    }
    fun getAllTypes():Set<DomainType>{
        return maps.keys;
    }
    fun getModesByType(type: DomainType):List<ClassModel>{
        return maps[type]?:ArrayList()
    }
    fun getModesByTypes(vararg type: DomainType):List<ClassModel>{
        var a = ArrayList<ClassModel>()
        type.forEach { a.addAll(maps[it]?:ArrayList()) }
        return a
    }
    fun getAllModels():List<ClassModel>?{
        var a = ArrayList<ClassModel>()
        maps.keys.forEach { a.addAll(maps[it]?:ArrayList()) }
        return a
    }
    fun getClassModelByName(className:String): ClassModel?{
        for( a in maps.values){
            for(e in a){
                if (e.className.equals(className, true)){
                    return e
                }
            }
        }
        return null
    }
    fun reset(){
        for( a in maps.values){
            a.clear()
        }
        val tableFields = currentTable!!.fields
        val p: Map<String, *> = ENV
        CodeCfg.instance?.models?.forEach(Consumer<ModelCfg> { e: ModelCfg ->
            val cls = createModel(
                replacePlaceholders(
                    e.className,
                    p
                )!!
            )
            if (StringUtils.isNotEmpty(e.refName)) {
                cls.refName = e.refName
            }
            val fields =
                getDefaultFields(
                    tableFields!!, e.fieldIncludes, e.fieldExcludes
                )
            cls.fields = ArrayList(fields)
            val domainType =
                DomainType.valueOf(
                    e.type!!
                )
            _addModel(domainType, cls)
        })
        sendMessage(NotificationType.MODEL_ADDED, null)
    }
}