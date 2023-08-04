package com.github.baboy.ideaplugincodegen.config

/**
 *
 * @author zhangyinghui
 * @date 2023/8/4
 */
class CodeCfgModel {

   var ctrl: CtrlModel? = null
   var svc: SvcModel? = null
   var dao: DaoModel? = null

    class CtrlModel: CodeCfg.CtrlMethodCfg(){
        var fields: List<String>? = null
    }

    class SvcModel: CodeCfg.SvcMethodCfg() {
        var fields: List<String>? = null
    }

    class DaoModel: CodeCfg.DaoMethodCfg() {
        var fields: List<String>? = null
    }
}