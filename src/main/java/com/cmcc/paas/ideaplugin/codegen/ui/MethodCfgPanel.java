package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.config.MethodGrpCfgModel;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class MethodCfgPanel {
    private JLabel clsTagLabel;
    private JTextField clsTextField;
    private JTextField methodTextField;
    private JTextField inputClsTextField;
    private JTextField outputClsTextField;
    private JPanel content;
    private FieldSelectionButton inputFieldSelectionBtn;
    private FieldSelectionButton outputFieldSelectionBtn;
    private JCheckBox outputListTypeCheckBox;
    private JCheckBox inputListTypeCheckBox;

    private MethodGrpCfgModel.MethodCfgModel model;
    public MethodCfgPanel(){
        inputFieldSelectionBtn.setValueChangedListener(new FieldSelectionButton.ValueChangedListener() {
            @Override
            public void onValueChanged(FieldSelectionButton btn) {
                model.setInputFields( Arrays.stream(inputFieldSelectionBtn.getSelectedValues()).toList());
            }
        });
        outputFieldSelectionBtn.setValueChangedListener(new FieldSelectionButton.ValueChangedListener() {
            @Override
            public void onValueChanged(FieldSelectionButton btn) {
                model.setInputFields( Arrays.stream(outputFieldSelectionBtn.getSelectedValues()).toList());
            }
        });
    }
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

    public void setModel(MethodGrpCfgModel.MethodCfgModel model) {
        this.model = model;
        this.clsTextField.setText(model.getClassName());
        this.methodTextField.setText(model.getName());
        this.inputClsTextField.setText(model.getInputClassName());
        this.inputListTypeCheckBox.setSelected(model.getInputListTypeFlag() == null ? false : model.getInputListTypeFlag());
        this.outputClsTextField.setText(model.getOutputClassName());
        this.outputListTypeCheckBox.setSelected(model.getOutputListTypeFlag() == null ? false: model.getOutputListTypeFlag());

        if (model.getFields() != null){
            List<CodeCfg.FieldCfg> fields = model.getFields().stream().map(e -> new CodeCfg.FieldCfg(e, false)).collect(Collectors.toList());
            this.inputFieldSelectionBtn.setItems(fields);
            this.outputFieldSelectionBtn.setItems(fields);
        }

        if (model.getInputFields() != null){
            this.inputFieldSelectionBtn.setSelectValues(model.getInputFields().toArray(CodeCfg.FieldCfg[]::new));
        }
        if (model.getOutputFields() != null){
            this.outputFieldSelectionBtn.setSelectValues(model.getOutputFields().toArray(CodeCfg.FieldCfg[]::new));
        }
    }
}
