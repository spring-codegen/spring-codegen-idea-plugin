package com.cmcc.paas.ideaplugin.codegen.swing.util

import java.util.function.Consumer
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 *
 * @author zhangyinghui
 * @date 2023/8/14
 */
class TextFieldUtils {
    companion object INSTANCE{
        fun addTextChangedEvent(textField:JTextField, textChangedEvent: TextChangedEvent){
            textField.getDocument().addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) {
                    textChangedEvent.onTextChanged(textField);
                }

                override fun removeUpdate(e: DocumentEvent) {
                    textChangedEvent.onTextChanged(textField);
                }

                override fun changedUpdate(e: DocumentEvent) {
                    textChangedEvent.onTextChanged(textField);
                }
            })
        }
    }
    interface TextChangedEvent{
        fun onTextChanged(textField:JTextField)
    }
}