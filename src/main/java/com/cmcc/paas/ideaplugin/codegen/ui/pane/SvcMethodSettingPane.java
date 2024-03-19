package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import com.cmcc.paas.ideaplugin.codegen.ui.BeanFieldSelectionDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class SvcMethodSettingPane extends MethodSettingPane {
    protected JLabel clsTagLabel;
    protected JTextField clsTextField;
    protected JTextField methodTextField;
    protected JPanel content;
    protected JCheckBox outputListTypeCheckBox;
    protected JCheckBox outputPagedCheckBox;
    private JButton closeBtn;
    private ArgsSettingPane argsSettingPane;
    private JComboBox resultComboBox;

    protected MethodSettingModel model;

    public SvcMethodSettingPane(){
        init();
    }
    public void init(){

        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, new TextFieldUtils.TextChangedEvent() {
                    @Override
                    public void onTextChanged(@NotNull JTextField textField) {
                        if (textField == methodTextField) {
                            model.setMethodName(methodTextField.getText());
                        }
                    }
                });
            }
            if (component instanceof JCheckBox){
                ((JCheckBox)component).addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent itemEvent) {
                        if (itemEvent.getSource() == outputListTypeCheckBox) {
                            model.getResult().setListTypeFlag(outputListTypeCheckBox.isSelected());
                        }
                        if (itemEvent.getSource() == outputPagedCheckBox) {
                            model.getResult().setOutputPaged(outputPagedCheckBox.isSelected());
                        }
                    }
                });
            }
        }
        setCloseBtnAction(closeBtn);
    }
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

    public MethodSettingModel getModel(){
        return model;
    }
    public void setModel(MethodSettingModel model) {
        this.model = model;
        this.clsTextField.setText(model.getClassName());
        this.methodTextField.setText(model.getMethodName());
        argsSettingPane.setArgs(model.getArgs());
        resetResultParams();
    }

    @Override
    public JComboBox getResultParamComboBox() {
        return resultComboBox;
    }
}
