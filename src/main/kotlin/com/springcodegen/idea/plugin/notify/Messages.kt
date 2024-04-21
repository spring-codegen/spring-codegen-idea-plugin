package com.springcodegen.idea.plugin.notify

import com.springcodegen.idea.plugin.constants.MvcClassType
import com.springcodegen.idea.plugin.gen.model.ClassModel

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
open class Messages {
    open class MethodUpdateData(var classType: MvcClassType, var method: ClassModel.Method){

    }
}