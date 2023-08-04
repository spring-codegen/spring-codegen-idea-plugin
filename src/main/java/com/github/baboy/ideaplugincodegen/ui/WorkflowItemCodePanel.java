package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CodeCfgModel;

import javax.swing.*;

/**
 * @author zhangyinghui
 * @date 2023/8/4
 */
public class WorkflowItemCodePanel {
    private JTextField pathTextField;
    private JComboBox httpMethodComboBox;
    private JTextField ctrlMethodNameTextField;
    private JTextField dtoClassNameTextField;
    private JTextField textField3;
    private JCheckBox voListFlagCheckbox;
    private MultiComboBox dtoFieldMultiComboBox;
    private MultiComboBox voFieldMultiComboBox;
    private JTextField svcMethodNameTextField;
    private JTextField boClassNameTextField;
    private JTextField boResultClassNameTextField;
    private JCheckBox boResultListFlagCheckbox;
    private MultiComboBox boResultMultiComboBox;
    private MultiComboBox boFieldMultiComboBox;
    private JPanel content;
    private CodeCfgModel model;
    public void createUIComponents(){
        dtoFieldMultiComboBox = new MultiComboBox();
        voFieldMultiComboBox = new MultiComboBox();
        boResultMultiComboBox = new MultiComboBox();
        boFieldMultiComboBox = new MultiComboBox();
    }

    public JPanel getContent() {
        return content;
    }

    public CodeCfgModel getModel() {
        return model;
    }

    public void setModel(CodeCfgModel model) {
        this.model = model;
    }
    public void getValue(){

    }
}
