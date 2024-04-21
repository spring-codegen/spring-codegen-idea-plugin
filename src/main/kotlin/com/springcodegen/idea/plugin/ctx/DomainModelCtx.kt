package com.springcodegen.idea.plugin.ctx

import com.springcodegen.idea.plugin.config.CodeCfg
import com.springcodegen.idea.plugin.config.CodeCfg.ModelCfg
import com.springcodegen.idea.plugin.constants.DomainType
import com.springcodegen.idea.plugin.ctx.AppCtx.currentTable
import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.notify.NotificationCenter
import com.springcodegen.idea.plugin.notify.NotificationCenter.sendMessage
import com.springcodegen.idea.plugin.notify.NotificationType
import com.springcodegen.idea.plugin.util.CodeGenUtils.getDefaultFields
import com.springcodegen.idea.plugin.util.FieldUtils
import com.springcodegen.idea.plugin.util.StringUtils.replacePlaceholders
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

/**
 *
 * @author zhangyinghui
 * @date 2024/3/20
 */
object DomainModelCtx {
    private var INNER_MODEL_NATIVE_CLASSES:MutableList<ClassModel> = arrayListOf(
        ClassModel.parse("java.lang.Boolean"),
        ClassModel.parse("java.lang.Long"),
        ClassModel.parse("java.lang.Integer")
    )
    private var DEFAULT_INNER_CLASSES:MutableList<ClassModel> = arrayListOf(
        ClassModel.parse("net.takela.common.web.model.IdArg"),
        ClassModel.parse("net.takela.common.web.model.IdResult")
    )
    private var INNER_CLASSES:MutableList<ClassModel> = ArrayList<ClassModel>()
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
    private fun setInnerClasses(a:List<ClassModel>){
        INNER_CLASSES.clear()
        INNER_CLASSES.addAll(a)
        for(x in DEFAULT_INNER_CLASSES){
            var t = a.any { it.className.equals(x.className, true) }
            if ( !t ){
                INNER_CLASSES.add(x)
            }
        }
        INNER_CLASSES.addAll(INNER_MODEL_NATIVE_CLASSES)
        NotificationCenter.sendMessage(NotificationType.MODEL_REMOVED, null)
    }
    private fun innerClasses():List<ClassModel>{
        if (INNER_CLASSES.size  == 0){
            INNER_CLASSES.addAll(DEFAULT_INNER_CLASSES)
            INNER_CLASSES.addAll(INNER_MODEL_NATIVE_CLASSES)
        }
        return INNER_CLASSES;
    }
    fun isInnerClass(className:String):Boolean{
        var t = innerClasses().any { it.className.equals(className, true) }
        if (t){
            return true
        }
        return arrayOf("Integer", "Long", "Boolean", "String", "Date", "BigDecimal", "List", "Map","IdArg","IdResult").any{ it.equals(className, true) };
    }
    fun refreshSettings(){
        getModesByTypes(DomainType.ARG).forEach {
            if (!isInnerClass(it.className)) {
                it.pkg = CodeSettingCtx.basePkg + ".domain.arg." + CodeSettingCtx.module
            }
        }
        getModesByTypes(DomainType.ENTITY).forEach {
            if (!isInnerClass(it.className)) {
                it.pkg = CodeSettingCtx.basePkg + ".domain.entity." + CodeSettingCtx.module
            }
        }
        getModesByTypes(DomainType.RESULT).forEach {
            if (!isInnerClass(it.className)) {
                it.pkg = CodeSettingCtx.basePkg + ".domain.result." + CodeSettingCtx.module
            }
        }
        //处理用户配置的内部类
        var a = CodeSettingCtx.innerModels.split(Regex("\n|;|,| "))
        var innerClasses = ArrayList<ClassModel>()
        for (x in a) {
            var s = x.trim()
            if (!StringUtils.isEmpty(s)) {
                innerClasses.add(ClassModel.parse(s))
            }
        }
        setInnerClasses(innerClasses)
    }
    fun createModel(className:String):ClassModel{
        for (x in innerClasses()){
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
        if (isInnerClass(classModel.className)){
            return
        }
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
        if (!isInnerClass(classModel.className)) {
            classModel.pkg = CodeSettingCtx.basePkg + ".domain." + type.toString().toLowerCase() + "." + CodeSettingCtx.module
        }
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
    fun getModesByType(type: DomainType): List<ClassModel> {
        var a:MutableList<ClassModel> = ArrayList(maps[type] ?: ArrayList())
        if (type == DomainType.ENTITY){
            a.addAll(innerClasses())
        }
        return a
    }
    fun getModesByTypes(vararg type: DomainType):List<ClassModel>{
        var a = ArrayList<ClassModel>()
        type.forEach { a.addAll(getModesByType(it)) }
        return a
    }
    fun getAllModels():List<ClassModel>{
        return getModesByTypes(*getAllTypes().toTypedArray())
    }
    fun getClassModelByName(className:String): ClassModel?{
        for(e in getAllModels()){
            if (e.className.equals(className, true)){
                return e
            }
        }
        return null
    }
    fun reset(){
        for( a in maps.values){
            a.clear()
        }
        val tableFields = currentTable!!.fields
        val p: Map<String, *> = AppCtx.getEnvParams()
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