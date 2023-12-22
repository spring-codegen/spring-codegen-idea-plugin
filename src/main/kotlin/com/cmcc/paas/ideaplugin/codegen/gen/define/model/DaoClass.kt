package com.cmcc.paas.ideaplugin.codegen.gen.define.model

/**
 *
 * @author zhangyinghui
 * @date 2023/12/1
 */
class DaoClass(className:String, pkg:String?, comment:String?, fields:MutableList<Field>?):ClassModel(className, pkg, comment, fields) {

    constructor(className:String) : this(className, null, null, null)
}