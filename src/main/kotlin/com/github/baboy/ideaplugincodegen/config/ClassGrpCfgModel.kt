package com.github.baboy.ideaplugincodegen.config

/**
 *
 * @author zhangyinghui
 * @date 2023/8/4
 */
class ClassGrpCfgModel {
   var ctrl: ClassCfgModel? = null
   var svc: ClassCfgModel? = null
   var dao: ClassCfgModel? = null

    class ClassCfgModel: CodeCfg.ClassDefine(){

    }
}