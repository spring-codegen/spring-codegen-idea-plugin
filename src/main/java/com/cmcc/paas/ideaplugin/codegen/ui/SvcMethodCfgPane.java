package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class SvcMethodCfgPane implements MethodCfgPane{
    protected JLabel clsTagLabel;
    protected JTextField clsTextField;
    protected JTextField methodTextField;
    protected JTextField inputClsTextField;
    protected JTextField outputClsTextField;
    protected JPanel content;
    protected JCheckBox outputListTypeCheckBox;
    protected JCheckBox inputListTypeCheckBox;
    protected JCheckBox outputPagedCheckBox;
    protected JButton inputButton;
    protected JButton outputButton;

    protected MethodCfgModel model;


    public SvcMethodCfgPane(){
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
                        if (textField == inputClsTextField) {
                            model.setInputClassName(inputClsTextField.getText());
                        }
                        if (textField == outputClsTextField) {
                            model.setOutputClassName(outputClsTextField.getText());
                        }
                    }
                });
            }
            if (component instanceof JCheckBox){
                ((JCheckBox)component).addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent itemEvent) {
                        if (itemEvent.getSource() == inputListTypeCheckBox) {
                            model.setInputListTypeFlag(inputListTypeCheckBox.isSelected());
                        }
                        if (itemEvent.getSource() == outputListTypeCheckBox) {
                            model.setOutputListTypeFlag(outputListTypeCheckBox.isSelected());
                        }
                        if (itemEvent.getSource() == outputPagedCheckBox) {
                            model.setOutputPaged(outputPagedCheckBox.isSelected());
                        }
                    }
                });
            }
        }
        inputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(model.getDbTableFields());
                dialog.setSelectedFields(model.getInputFields());
                dialog.setVisible(true);
            }
        });
        outputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(model.getDbTableFields());
                dialog.setSelectedFields(model.getOutputFields());
                dialog.setVisible(true);
            }
        });
    }
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

    public MethodCfgModel getModel(){
        return model;
    }
    public void setModel(MethodCfgModel model) {
        this.model = model;
        this.clsTextField.setText(model.getClassName());
        this.methodTextField.setText(model.getMethodName());
        this.inputClsTextField.setText(model.getInputClassName());
        this.inputListTypeCheckBox.setSelected(model.getInputListTypeFlag() == null ? false : model.getInputListTypeFlag());
        this.outputClsTextField.setText(model.getOutputClassName());
        this.outputListTypeCheckBox.setSelected(model.getOutputListTypeFlag() == null ? false: model.getOutputListTypeFlag());
        this.outputPagedCheckBox.setSelected(model.getOutputPaged());
//        if (model.getFields() != null){
//            List<DBTableField> fields = model.getFields().stream().map(e -> new DBTableField(e, false, null)).collect(Collectors.toList());
//            this.inputFieldSelectionBtn.setItems(fields);
//            this.outputFieldSelectionBtn.setItems(fields);
//        }
//
//        if (model.getInputFields() != null){
//            this.inputFieldSelectionBtn.setSelectValues(model.getInputFields().toArray(CodeCfg.FieldDefine[]::new));
//        }
//        if (model.getOutputFields() != null){
//            this.outputFieldSelectionBtn.setSelectValues(model.getOutputFields().toArray(CodeCfg.FieldDefine[]::new));
//        }
    }



}
