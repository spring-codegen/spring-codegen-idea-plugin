package com.cmcc.paas.ideaplugin.codegen.gen.ctx

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.util.FieldUtils

/**
 *
 * @author zhangyinghui
 * @date 2024/3/20
 */
object DomainModelCtx {
    private var maps:MutableMap<DomainType, MutableList<ClassModel>> = HashMap()
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
    fun addModel(type:DomainType, classModel: ClassModel){
        if (!maps.containsKey(type)){
            maps[type] = ArrayList<ClassModel>()
        }
        var isExists = maps[type]!!.stream().anyMatch { it.className.equals(classModel.className, true) }
        if (isExists){
            return
        }
        maps[type]!!.add(classModel)
    }
    fun removeModel(classModel: ClassModel){
        for( a in maps.values){
            for(e in a){
                if (e.className.equals(classModel.className, true)){
                    a.remove(e)
                }
            }
        }
    }
    fun getAllTypes():Set<DomainType>{
        return maps.keys;
    }
    fun getModesByType(type: DomainType):List<ClassModel>?{
        return maps[type]
    }
    fun getModesByTypes(vararg type: DomainType):List<ClassModel>{
        var a = ArrayList<ClassModel>()
        type.forEach { a.addAll(maps[it]!!) }
        return a
    }
    fun getAllModels():List<ClassModel>?{
        var a = ArrayList<ClassModel>()
        maps.keys.forEach { a.addAll(maps[it]!!) }
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
    fun clear(){
        for( a in maps.values){
            a.clear()
        }
    }
}