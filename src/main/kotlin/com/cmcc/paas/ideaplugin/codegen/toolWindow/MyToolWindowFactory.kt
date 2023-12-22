package com.cmcc.paas.ideaplugin.codegen.toolWindow

import com.cmcc.paas.ideaplugin.codegen.MyBundle
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx
import com.cmcc.paas.ideaplugin.codegen.services.MyProjectService
import com.cmcc.paas.ideaplugin.codegen.ui.CodeGenPane
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import javax.swing.JButton


class MyToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        AppCtx.project = project
//        val myToolWindow = MyToolWindow(toolWindow)
        val myToolWindow = CodeGenPane()
        val content = ContentFactory.getInstance().createContent(myToolWindow.content, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            val label = JBLabel(MyBundle.message("randomLabel", "?"))

            add(label)
            add(JButton(MyBundle.message("shuffle")).apply {
                addActionListener {
                    label.text = MyBundle.message("randomLabel", service.getRandomNumber())
                }
            })
        }
    }
}
