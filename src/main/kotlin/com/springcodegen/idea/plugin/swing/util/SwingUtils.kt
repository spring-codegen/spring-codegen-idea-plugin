package com.springcodegen.idea.plugin.swing.util

import com.springcodegen.idea.plugin.ctx.AppCtx.project
import org.apache.commons.lang.StringUtils
import java.awt.Component
import java.awt.Container
import java.awt.event.ActionListener
import java.io.File
import java.util.*
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JTextField

object SwingUtils {
    @JvmStatic fun searchComponentsByName(c: Component, name: String): List<Component> {
        val a: MutableList<Component> = ArrayList()
        if (name == c.name) {
            a.add(c)
        }
        if (c is Container) {
            Arrays.stream(c.components).forEach { e: Component ->
                val a2 = searchComponentsByName(e, name)
                a.addAll(a2)
            }
        }
        return a
    }
    @JvmStatic fun addSelectDirEvent(btn:JButton, textField:JTextField, parent:Component, listener: ActionListener?){
        btn.addActionListener( ActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            if ( StringUtils.isNotEmpty(textField.text)) {
                fileChooser.currentDirectory = File(textField.text)
            }
            val result = fileChooser.showDialog(parent, "选择")
            if (result == JFileChooser.APPROVE_OPTION) {
                val selectedFile = fileChooser.selectedFile
                val dir = if (selectedFile.isDirectory) selectedFile else selectedFile.parentFile
                textField.text = dir.absolutePath
            }
            listener?.actionPerformed(it)
        })
    }
}