package com.springcodegen.idea.plugin.gen

import com.springcodegen.idea.plugin.gen.model.ClassModel
import com.springcodegen.idea.plugin.gen.model.CtrlClass
import com.springcodegen.idea.plugin.gen.model.DaoClass
import com.springcodegen.idea.plugin.gen.model.SvcClass

/**
 *
 * @author zhangyinghui
 * @date 2023/12/22
 */
class ModelResult {
    var ctrlClass: CtrlClass? = null
    var svcClass: SvcClass? = null
    var daoClass: DaoClass? = null
    var args:List<ClassModel>? = null
    var results:List<ClassModel>? = null
    var entities:List<ClassModel>? = null
}