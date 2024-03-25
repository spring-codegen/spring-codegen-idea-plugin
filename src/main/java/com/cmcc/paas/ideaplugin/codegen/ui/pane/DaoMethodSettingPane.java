package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.model.DaoClass;
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
 * @date 2023/12/21
 */
public class DaoMethodSettingPane extends MethodSettingPane {
    private JLabel clsTagLabel;
    private JTextField clsTextField;
    private JTextField methodTextField;
    private JCheckBox outputPagedCheckBox;
    private JCheckBox outputListTypeCheckBox;
    private JButton dataFieldButton;
    private JButton whereFieldButton;
    private JPanel content;
    private JButton closeBtn;
//    private ArgsSettingPane argsSettingPane;
    private JComboBox resultComboBox;
    private JComboBox argComboBox;

//    private MethodSettingModel model;
    private DaoClass.Method method = null;

    public DaoMethodSettingPane(){
        init();
    }
    public void init(){

        super.init();
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, textField -> {
                    if (textField == methodTextField) {
                        method.setName(methodTextField.getText());
                    }
                });
            }
            if (component instanceof JCheckBox){
                ((JCheckBox)component).addItemListener( itemEvent ->{
                    if (itemEvent.getSource() == outputListTypeCheckBox) {
                        method.getResult().setListTypeFlag(outputListTypeCheckBox.isSelected());
                    }
                    if (itemEvent.getSource() == outputPagedCheckBox) {
                        method.getResult().setOutputPaged(outputPagedCheckBox.isSelected());
                    }
                });
            }
        }
        dataFieldButton.addActionListener(e -> {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(AppCtx.INSTANCE.getCurrentTable().getFields());
                dialog.setSelectedFields(method.getSqlDataFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        method.setSqlDataFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            });
        whereFieldButton.addActionListener(e -> {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(AppCtx.INSTANCE.getCurrentTable().getFields());
                dialog.setSelectedFields(method.getSqlCondFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        method.setSqlCondFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            });


        setCloseBtnAction(closeBtn);
    }

    @Override
    public JPanel getContent() {
        return content;
    }

//    @Override
//    public void setModel(MethodSettingModel model) {
//        this.model = model;
//        this.clsTextField.setText(model.getClassName());
//        this.methodTextField.setText(model.getMethodName());
////        argsSettingPane.setArgs(model.getArgs());
//        resetArgComboBox();
//        resetReturnComboBox();
//    }
//
//    @Override
//    public MethodSettingModel getModel() {
//        return this.model;
//    }

    @Override
    public DaoClass.Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(ClassModel.Method method) {
        this.method = (DaoClass.Method)method;
        this.methodTextField.setText(method.getName());
        this.outputListTypeCheckBox.setSelected(method.getResult().getListTypeFlag());
        resetArgComboBox();
        resetReturnComboBox();
    }

    @Override
    public MvcClassType getClassType() {
        return MvcClassType.DAO;
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
