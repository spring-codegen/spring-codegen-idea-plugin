package com.github.baboy.ideaplugincodegen.db.model

import com.github.baboy.ideaplugincodegen.db.model.DBTableField

class DBTable {
    var name:String? = null
    var comment:String? = null
    var schema:String? = null
    var fields: List<DBTableField>? = null
}