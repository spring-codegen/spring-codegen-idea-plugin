package com.springcodegen.idea.plugin.gen.model

/**
 *
 * @author zhangyinghui
 * @date 2023/12/1
 */
class SvcClass(className:String, pkg:String?, comment:String?, fields:MutableList<Field>?):
    ClassModel(className, pkg, comment, fields) {

    constructor(className:String) : this(className, null, null, null)
}