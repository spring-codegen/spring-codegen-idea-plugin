package com.springcodegen.idea.plugin.gen.model


import com.springcodegen.idea.plugin.util.FieldUtils
import java.util.stream.Collectors

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
open class ClassModel(var className: String, var pkg: String?, var comment: String?, var fields: MutableList<Field>?) {
    constructor(className: String) : this(className, null, null, null)
    companion object{
        @JvmStatic fun isBaseType(className: String): Boolean {
            return arrayOf("Integer", "Long", "Boolean", "String", "Date", "BigDecimal", "List", "Map").any { it.equals(className, true) }
        }
        @JvmStatic fun parse(clsName: String):ClassModel{
            var i = clsName.lastIndexOf(".")
            var cls = ClassModel(clsName.substring(i+1))
            cls.refName = FieldUtils.getRefName(cls.className)
            cls.pkg =  if (i > 0) clsName.substring(0, i) else null
            return cls
        }
    }

    var tableName: String? = null
    var methods: MutableList<Method> = ArrayList()
    var imports: MutableSet<String>? = HashSet()
    var baseType: Boolean = false
    var refName: String? = null
    var implement: ClassModel? = null
    var extend: ClassModel? = null
    var dependency: ClassModel? = null

    open fun clone(): ClassModel {
        var cls: ClassModel = this.javaClass.constructors[0].newInstance(
            className,
            pkg,
            comment,
            if (fields != null) fields!!.stream().map { e -> e.clone() }.toList() else null
        ) as ClassModel
        cls.methods = methods.stream().map { e -> e.clone() }.toList()
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
            f.maxLen = maxLen
            f.minLen = minLen
            return f
        }
    }

    open class Method(
        var name: String,
        var args: MutableList<MethodArg>,
        var result: MethodResult?
    ) {
        var dependency: Method? = null
        var comment: String? = null
        var cls: ClassModel? = null
        var type: String? = null
        open fun clone(): Method {
            var m = Method(
                name,
                 args.stream().map { e -> e.clone() }.toList(),
                if (result == null) null else result?.clone())
            m.dependency = if (dependency != null) dependency!!.clone() else null
            m.comment = comment
            m.cls = cls
            m.type = type
            return m
        }
    }
    open class MethodArg(var classModel: ClassModel?, var refName: String?){
        constructor(): this(null, null)
        var isPathVar = false
        var listTypeFlag = false
        var comment:String? = null
        open fun clone():MethodArg{
            var result = MethodArg(classModel, refName)
            result.isPathVar = isPathVar
            result.listTypeFlag = listTypeFlag
            result.comment = comment
            return result
        }
    }
    open class MethodResult(var classModel: ClassModel?, var refName: String?){
        constructor(): this(null, null)
        var outputPaged = false
        var listTypeFlag = false
        var comment:String? = null
        open fun clone():MethodResult{
            var result = MethodResult(classModel, refName)
            result.outputPaged = outputPaged
            result.listTypeFlag = listTypeFlag
            result.comment = comment
            return result
        }
    }
}