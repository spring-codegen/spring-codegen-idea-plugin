package com.cmcc.paas.ideaplugin.codegen.gen

import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.DaoClass
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.SvcClass

/**
 *
 * @author zhangyinghui
 * @date 2023/12/22
 */
class ModelResult {
    var ctrlClass:CtrlClass? = null
    var svcClass:SvcClass? = null
    var daoClass:DaoClass? = null
    var args:List<ClassModel>? = null
    var results:List<ClassModel>? = null
    var entities:List<ClassModel>? = null
}