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
 * @date 2023/12/21
 */
public class DaoMethodCfgPane extends MethodCfgPane{
    private JLabel clsTagLabel;
    private JTextField clsTextField;
    private JTextField methodTextField;
    private JTextField inputClsTextField;
    private JTextField outputClsTextField;
    private JCheckBox outputPagedCheckBox;
    private JCheckBox outputListTypeCheckBox;
    private JCheckBox inputListTypeCheckBox;
    private JButton inputButton;
    private JButton outputButton;
    private JButton dataFieldButton;
    private JButton whereFieldButton;
    private JPanel content;
    private JButton closeBtn;

    private SvcMethodCfgPane.MethodCfgModel model;

    public DaoMethodCfgPane(){
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
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        model.setInputFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            }
        });
        outputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(model.getDbTableFields());
                dialog.setSelectedFields(model.getOutputFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        model.setOutputFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            }
        });
        dataFieldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(model.getDbTableFields());
                dialog.setSelectedFields(model.getSqlDataFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        model.setSqlDataFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            }
        });
        whereFieldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(model.getDbTableFields());
                dialog.setSelectedFields(model.getSqlCondFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        model.setSqlCondFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            }
        });


        setCloseBtnAction(closeBtn);
    }

    @Override
    public JPanel getContent() {
        return content;
    }

    @Override
    public void setModel(SvcMethodCfgPane.MethodCfgModel model) {
        this.model = model;
        this.clsTextField.setText(model.getClassName());
        this.methodTextField.setText(model.getMethodName());
        this.inputClsTextField.setText(model.getInputClassName());
        this.inputListTypeCheckBox.setSelected(model.getInputListTypeFlag() == null ? false : model.getInputListTypeFlag());
        this.outputClsTextField.setText(model.getOutputClassName());
        this.outputListTypeCheckBox.setSelected(model.getOutputListTypeFlag() == null ? false: model.getOutputListTypeFlag());
        this.outputPagedCheckBox.setSelected(model.getOutputPaged());
    }

    @Override
    public MethodCfgModel getModel() {
        return this.model;
    }
}
