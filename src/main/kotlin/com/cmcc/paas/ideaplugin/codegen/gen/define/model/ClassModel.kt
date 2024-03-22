package com.cmcc.paas.ideaplugin.codegen.gen.define.model

import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils
import com.intellij.util.containers.stream
import java.util.stream.Collectors

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
open class ClassModel(var className: String, var pkg: String?, var comment: String?, var fields: MutableList<Field>?) {
    constructor(className: String) : this(className, null, null, null)
    companion object{
        val CLASS_ID_ARG = ClassModel("IdArg", "com.cmit.paas.common.web.model", null, null)
        val CLASS_ID_RESULT = ClassModel("IdResult", "com.cmit.paas.common.web.model", null, null)
        val CLASS_BOOL_RESULT = ClassModel("Boolean")
        val CLASS_LONG_RESULT = ClassModel("Long")
        val CLASS_INTEGER_RESULT = ClassModel("Integer")
        @JvmStatic fun idArgClass():ClassModel{
            return CLASS_ID_ARG
        }
        @JvmStatic fun idResultClass():ClassModel{
            return CLASS_ID_RESULT
        }
        @JvmStatic fun booleanClass():ClassModel{
            return CLASS_BOOL_RESULT
        }
        @JvmStatic fun longClass():ClassModel{
            return CLASS_LONG_RESULT
        }
        @JvmStatic fun integerClass():ClassModel{
            return CLASS_INTEGER_RESULT
        }
        @JvmStatic fun isInnerClass(className:String):Boolean{
            return arrayOf("Boolean", "Long", "IdArg", "IdResult").stream().filter { it.equals(className, true) }.count() > 0;
        }

        @JvmStatic fun baseTypes(): Array<String> {
            return arrayOf("Integer", "Long", "Boolean", "String", "Date", "BigDecimal", "List", "Map")
        }

        @JvmStatic fun isCommonType(clsName: String): Boolean {
            return arrayOf("ListResult", "IdResult", "IdArg").stream().anyMatch { e -> e.equals(clsName) }
        }

        @JvmStatic fun isBaseType(clsName: String): Boolean {
            return baseTypes().stream().anyMatch { e -> e.equals(clsName) }
        }
    }

    var tableName: String? = null
    var methods: MutableList<Method>? = ArrayList()
    var imports: MutableSet<String>? = HashSet()
    var baseType: Boolean = false
    var refName: String? = null
    var implement: ClassModel? = null
    var extend: ClassModel? = null
    var dependency: ClassModel? = null
    open fun isInnerClass(): Boolean {
        return ClassModel.isInnerClass(className)
    }

    open fun clone(): ClassModel {
        var cls: ClassModel = this.javaClass.constructors[0].newInstance(
            className,
            pkg,
            comment,
            if (fields != null) fields!!.stream().map { e -> e.clone() }.toList() else null
        ) as ClassModel
        cls.methods = if (methods != null) methods!!.stream().map { e -> e.clone() }.toList() else null
        cls.imports = if (imports != null) imports!!.stream().map { e -> e }.collect(Collectors.toSet()) else null
        cls.baseType = baseType
        cls.refName = refName
        cls.implement = implement
        cls.extend = extend
        cls.dependency = dependency
        return cls;
    }

    class Field(
        var name: String,
        var javaType: String,
        var comment: String?,
        var notNull: Boolean?,
        var setter: String?,
        var getter: String?
    ) {
        var pkg: String? = null
        var column: String? = null
        var classType: ClassModel? = null
        var baseType: Boolean = false
        var minLen: Int = -1
        var maxLen: Int = -1
        fun clone(): Field {
            var f = Field(name, javaType, comment, notNull, setter, getter)
            f.column = column
            f.classType = if (classType != null) classType!!.clone() else null
            f.baseType = baseType
            return f
        }
    }

    open class Method(
            var name: String,
            var args:MutableList<MethodArg>,
            var result: MethodResult?
    ) {
        var dependency: Method? = null
        var comment: String? = null
        var cls: ClassModel? = null
        var type: String? = null
        open fun clone(): Method {
            var m = Method(name, args, result)
            m.dependency = if (dependency != null) dependency!!.clone() else null
            m.comment = comment
            m.cls = cls
            return m
        }
    }
    open class MethodArg(classModel: ClassModel?, refName: String?){
        constructor(): this(null, null)
        var classModel: ClassModel? = null
        var refName: String? = null
        var isPathVar = false
        var listTypeFlag = false
    }
    open class MethodResult(classModel: ClassModel?, refName: String?){
        constructor(): this(null, null)
        var classModel: ClassModel? = null
        var refName: String? = null
        var outputPaged = false
        var listTypeFlag = false
    }
}