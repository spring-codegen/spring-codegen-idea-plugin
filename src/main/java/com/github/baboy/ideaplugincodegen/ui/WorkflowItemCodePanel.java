package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CodeCfgModel;
import org.apache.commons.beanutils.BeanUtils;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangyinghui
 * @date 2023/8/4
 */
public class WorkflowItemCodePanel {
    private JTextField pathTextField;
    private JComboBox httpMethodComboBox;
    private JTextField ctrlMethodNameTextField;
    private JTextField dtoClassNameTextField;
    private JTextField voClassNameTextField;
    private JCheckBox voListFlagCheckbox;
    private FieldComboBox dtoFieldMultiComboBox;
    private FieldComboBox voFieldMultiComboBox;
    private JTextField svcMethodNameTextField;
    private JTextField boClassNameTextField;
    private JTextField boResultClassNameTextField;
    private JCheckBox boResultListFlagCheckbox;
    private FieldComboBox boResultFieldMultiComboBox;
    private FieldComboBox boFieldMultiComboBox;
    private JPanel content;
    private CodeCfgModel model;
    public void createUIComponents(){
        dtoFieldMultiComboBox = new FieldComboBox();
        voFieldMultiComboBox = new FieldComboBox();
        boResultFieldMultiComboBox = new FieldComboBox();
        boFieldMultiComboBox = new FieldComboBox();
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

        List<FieldComboBox.Model> ctrlFieldModel = model.getCtrl().getFields().stream().map(e -> new FieldComboBox.Model().setValue(e).setNotNull(false)).collect(Collectors.toList());
        List<FieldComboBox.Model> svcFieldModel = model.getSvc().getFields().stream().map(e -> new FieldComboBox.Model().setValue(e).setNotNull(false)).collect(Collectors.toList());
        
        this.dtoFieldMultiComboBox.setItems(ctrlFieldModel);
        this.dtoClassNameTextField.setText(model.getCtrl().getDtoClassName());

        this.voClassNameTextField.setText(model.getCtrl().getVoClassName());
        this.voFieldMultiComboBox.setItems(ctrlFieldModel);
        this.voListFlagCheckbox.setSelected(model.getCtrl().getVoListFlag());
        this.svcMethodNameTextField.setText(model.getSvc().getName());
        this.boClassNameTextField.setText(model.getSvc().getBoClassName());
        this.boFieldMultiComboBox.setItems(svcFieldModel);
        this.boResultFieldMultiComboBox.setItems(svcFieldModel);
        this.boResultClassNameTextField.setText(model.getSvc().getBoResultClassName());
        this.boResultListFlagCheckbox.setSelected(model.getSvc().getBoResultListFlag());

        if (model.getCtrl().getDtoFields() != null){
            FieldComboBox.Model[] selectedDtoFields = model.getCtrl().getDtoFields().stream().map(e -> new FieldComboBox.Model().setValue(e)).toArray(FieldComboBox.Model[]::new);
            this.dtoFieldMultiComboBox.setSelectValues(selectedDtoFields);
        }
        if (model.getCtrl().getVoFields() != null){
            FieldComboBox.Model[] selectedDtoFields = model.getCtrl().getVoFields().stream().map(e -> new FieldComboBox.Model().setValue(e)).toArray(FieldComboBox.Model[]::new);
            this.voFieldMultiComboBox.setSelectValues(selectedDtoFields);
        }
        if (model.getSvc().getBoFields() != null){
            FieldComboBox.Model[] selectedDtoFields = model.getSvc().getBoFields().stream().map(e -> new FieldComboBox.Model().setValue(e)).toArray(FieldComboBox.Model[]::new);
            this.boFieldMultiComboBox.setSelectValues(selectedDtoFields);
        }
        if (model.getSvc().getBoResultFields() != null){
            FieldComboBox.Model[] selectedDtoFields = model.getSvc().getBoResultFields().stream().map(e -> new FieldComboBox.Model().setValue(e)).toArray(FieldComboBox.Model[]::new);
            this.boResultFieldMultiComboBox.setSelectValues(selectedDtoFields);
        }
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
