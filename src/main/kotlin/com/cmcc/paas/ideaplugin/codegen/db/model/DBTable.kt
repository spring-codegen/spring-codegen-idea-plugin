package com.cmcc.paas.ideaplugin.codegen.db.model

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField

class DBTable {
    var name:String? = null
    var comment:String? = null
    var schema:String? = null
    var fields: List<DBTableField>? = null
}