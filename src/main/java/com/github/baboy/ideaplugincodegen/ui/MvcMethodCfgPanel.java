package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CodeCfgModel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class MvcMethodCfgPanel {
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

    private CodeCfgModel.MethodCfgModel model;
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

    public void setModel(CodeCfgModel.MethodCfgModel model) {
        this.model = model;
        this.clsTextField.setText(model.getClassName());
        this.methodTextField.setText(model.getName());
        this.inputClsTextField.setText(model.getInputClassName());
        this.inputListTypeCheckBox.setSelected(model.getInputListTypeFlag() == null ? false : model.getInputListTypeFlag());
        this.outputClsTextField.setText(model.getOutputClassName());
        this.outputListTypeCheckBox.setSelected(model.getOutputListTypeFlag() == null ? false: model.getOutputListTypeFlag());



        if (model.getFields() != null){
            List<FieldSelectionButton.Model> fields = model.getFields().stream().map(e -> new FieldSelectionButton.Model().setValue(e).setNotNull(false)).collect(Collectors.toList());
            this.inputFieldSelectionBtn.setItems(fields);
            this.outputFieldSelectionBtn.setItems(fields);
        }

        if (model.getInputFields() != null){
            FieldSelectionButton.Model[] inputSelectedFields = model.getInputFields().stream().map(e -> new FieldSelectionButton.Model().setValue(e)).toArray(FieldSelectionButton.Model[]::new);
            this.inputFieldSelectionBtn.setSelectValues(inputSelectedFields);
        }
        if (model.getOutputFields() != null){
            FieldSelectionButton.Model[] outputSelectedFields = model.getOutputFields().stream().map(e -> new FieldSelectionButton.Model().setValue(e)).toArray(FieldSelectionButton.Model[]::new);
            this.inputFieldSelectionBtn.setSelectValues(outputSelectedFields);
        }
    }
}
