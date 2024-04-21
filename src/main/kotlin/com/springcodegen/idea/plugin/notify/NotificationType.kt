package com.springcodegen.idea.plugin.notify

/**
 *
 * @author zhangyinghui
 * @date 2024/3/25
 */
class NotificationType {

    companion object {
        const val MODEL_ADDED = "model-added"
        const val MODEL_UPDATED = "model-updated"
        const val MODEL_REMOVED = "model-removed"
        const val REQUEST_PATH_UPDATED = "request-path-updated"
        const val METHOD_UPDATED = "request-path-updated"
        const val CODE_SETTING_UPDATED = "code-setting-updated"
        const val MVC_CLASS_UPDATED = "mvc-class-updated"
        const val DB_CONN_EXCEPTION = "db-conn-exception"

        const val DB_TABLES_UPDATED = "db-tables-updated"
    }
}