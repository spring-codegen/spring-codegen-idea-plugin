package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
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
//    private ArgsSettingPane argsSettingPane;
    private JComboBox resultComboBox;
    private JComboBox argComboBox;

    protected MethodSettingModel model;

    public SvcMethodSettingPane(){
        init();
    }
    public void init(){
        super.init();
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
//        argsSettingPane.setArgs(model.getArgs());
        resetArgComboBox();
        resetReturnComboBox();
    }

    @Override
    public JComboBox getReturnComboBox() {
        return resultComboBox;
    }
//    @Override
//    public ArgsSettingPane getArgsSettingPane() {
//        return argsSettingPane;
//    }

    @Override
    public JComboBox getArgComboBox() {
        return argComboBox;
    }
}
