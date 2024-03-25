package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel
import java.util.HashSet

/**
 *
 * @author zhangyinghui
 * @date 2024/3/22
 */
open class ClassGenerator (var module: String){
    fun setClassModelRefName(classModel: ClassModel){
        if(ClassModel.isBaseType(classModel.className)){
            if (classModel.fields!= null && classModel.fields!!.size > 0) {
                classModel.refName = classModel.fields!![0].name;
            }
        }else{
            classModel.refName = FieldUtils.getRefName(classModel.className)
        }
    }
    private fun addImport(list:MutableSet<String>, pkg:String?, className:String?){
        if ( pkg != null && className != null &&  !ClassModel.isBaseType(className)){
            list.add("$pkg.$className")
        }
    }
    fun processImports(cls: ClassModel){
        var imports:MutableSet<String> = HashSet();
        if (cls.fields != null){
            cls.fields!!.forEach{f ->
                if ( f.javaType == "Date" ){
                    imports.add("java.util." + f.javaType)
                }else if (f.pkg != null && !ClassModel.isBaseType(f.javaType)) {
                    addImport(imports, f.pkg!! , f.javaType)
                }
            }
        }
        if (cls.methods != null){
            for (m in cls.methods!!) {
                if (m.args != null) {
                    for ( e in m.args) {
                        if (e.classModel != null && e.classModel?.pkg != null) {
                            addImport(imports, e.classModel?.pkg ,  e.classModel?.className)
                        }
                    }

                    if (m.result != null && m.result?.classModel?.pkg != null) {
                        addImport(imports, m.result?.classModel?.pkg, m.result?.classModel?.className)
                        if (m.result!!.listTypeFlag) {
                            imports.add("java.util.List")
                        }
                    }
                    if (m.dependency != null) {
                        if (m.dependency?.cls != null && m.dependency?.cls?.pkg != null){
                            addImport(imports, m.dependency?.cls?.pkg , m.dependency?.cls?.className)
                        }
                        for ( e in m.dependency?.args!!) {
                            if (e.classModel != null && e.classModel?.pkg != null) {
                                addImport(imports,e.classModel?.pkg , e.classModel?.className)
                            }
                        }
                        if (m.dependency!!.result != null) {
                            addImport(imports,m.dependency!!.result?.classModel!!.pkg, m.dependency!!.result?.classModel!!.className)
                        }
                    }
                }
            }
        }
        if (cls.implement != null && cls.implement!!.pkg != null){
            addImport(imports, cls.implement!!.pkg, cls.implement!!.className)
        }
        if (cls.extend != null && cls.extend!!.pkg != null){
            addImport(imports,cls.extend!!.pkg, cls.extend!!.className)
        }
        if (cls.dependency != null && cls.dependency!!.pkg != null){
            addImport(imports,cls.dependency!!.pkg, cls.dependency!!.className)
        }
        cls.imports = imports
    }
}