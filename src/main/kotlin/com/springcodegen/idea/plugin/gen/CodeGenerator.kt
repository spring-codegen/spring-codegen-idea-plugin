package com.springcodegen.idea.plugin.gen

/**
 *
 * @author zhangyinghui
 * @date 2023/8/9
 */
class CodeGenerator() {
    fun gen(){
        DomainModelGenerator.gen()
        DaoMapperGenerator.gen()
        DaoInterfaceGenerator.gen()
        CtrlClassGenerator.gen()
        SvcInterfaceGenerator.gen()
        SvcClassGenerator.gen()
    }
}