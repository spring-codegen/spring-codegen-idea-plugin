package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class CtrlMethodSettingPane extends MethodSettingPane {
    protected JLabel clsTagLabel;
    protected JTextField pathTextField;
    protected JTextField methodTextField;
    protected JTextField outputClsTextField;
    protected JPanel content;
    protected JCheckBox outputListTypeCheckBox;
    protected JCheckBox outputPagedCheckBox;
    protected JButton inputButton;
    protected JButton outputButton;
    private JButton closeBtn;
    private JComboBox resultComboBox;
    private ArgsSettingPane argsSettingPane;

    protected MethodSettingModel model;

    public CtrlMethodSettingPane(){
        init();
    }
    public void init(){
        super.init();
        outputPagedCheckBox.setBackground(null);
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, new TextFieldUtils.TextChangedEvent() {
                    @Override
                    public void onTextChanged(@NotNull JTextField textField) {
                        if (textField == pathTextField) {
                            model.setPath(pathTextField.getText());
                        }
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
//                        if (itemEvent.getSource() == inputListTypeCheckBox) {
//                            model.setInputListTypeFlag(inputListTypeCheckBox.isSelected());
//                        }
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
        this.pathTextField.setText(model.getPath());
        this.methodTextField.setText(model.getMethodName());
        if (model.getResult() != null) {
            this.outputListTypeCheckBox.setSelected(model.getResult().getListTypeFlag() == null ? false : model.getResult().getListTypeFlag());
            this.outputPagedCheckBox.setSelected(model.getResult().getOutputPaged());
        }
        argsSettingPane.setArgs(model.getArgs());
        resetResultParams();
    }

    @Override
    public JComboBox getResultParamComboBox() {
        return resultComboBox;
    }
    @Override
    public ArgsSettingPane getArgsSettingPane() {
        return argsSettingPane;
    }
}
