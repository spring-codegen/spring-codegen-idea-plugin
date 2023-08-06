package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CodeCfgModel;
import org.apache.commons.beanutils.BeanUtils;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

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
    public WorkflowItemCodePanel(){
    }
    public void init(){
        httpMethodComboBox.setModel(new DefaultComboBoxModel(new String[]{"GET","POST","DELETE","PUT"}));
    }

    public JPanel getContent() {
        return content;
    }

    public CodeCfgModel getModel() {
        return model;
    }

    public void setModel(CodeCfgModel model) {
        System.out.println(""+model.getCtrl().getName()+","+model.getCtrl().getHttpMethod());
        this.model = model;
        this.httpMethodComboBox.setSelectedItem(model.getCtrl().getHttpMethod());
        this.pathTextField.setText(model.getCtrl().getPath());
        this.ctrlMethodNameTextField.setText(model.getCtrl().getName());

        this.dtoFieldMultiComboBox.setItems(model.getCtrl().getFields());
        this.voFieldMultiComboBox.setItems(model.getCtrl().getFields());
        this.voListFlagCheckbox.setSelected(model.getCtrl().getVoListFlag());
        this.svcMethodNameTextField.setText(model.getSvc().getName());
        this.boClassNameTextField.setText(model.getSvc().getBoClassName());
        this.boFieldMultiComboBox.setItems(model.getSvc().getFields());
        this.boResultMultiComboBox.setItems(model.getSvc().getFields());
        this.boResultClassNameTextField.setText(model.getSvc().getBoResultClassName());
        this.boResultListFlagCheckbox.setSelected(model.getSvc().getBoResultListFlag());
    }
    public void getValue(){
        CodeCfgModel val = new CodeCfgModel();
        try {
            BeanUtils.copyProperties(val, this.model);
            val.getCtrl().setPath(pathTextField.getText());
            val.getCtrl().setHttpMethod(String.valueOf(httpMethodComboBox.getSelectedItem()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
