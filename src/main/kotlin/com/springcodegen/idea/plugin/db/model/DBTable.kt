package com.springcodegen.idea.plugin.db.model

import com.springcodegen.idea.plugin.db.model.DBTableField

class DBTable {
    var name:String? = null
    var comment:String? = null
    var schema:String? = null
    var fields: List<DBTableField>? = null
    var relationTable:DBTable? = null
}