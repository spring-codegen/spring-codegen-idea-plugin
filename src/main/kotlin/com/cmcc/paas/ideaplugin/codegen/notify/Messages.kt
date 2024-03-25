package com.cmcc.paas.ideaplugin.codegen.notify

import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
open class Messages {
    open class MethodUpdateData(var classType: MvcClassType, var method:ClassModel.Method){

    }
}