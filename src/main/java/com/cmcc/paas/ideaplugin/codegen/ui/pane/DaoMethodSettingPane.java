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
public class DaoMethodSettingPane extends MethodSettingPane {
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

    private MethodSettingModel model;

    public DaoMethodSettingPane(){
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
    public void setModel(MethodSettingModel model) {
        this.model = model;
        this.clsTextField.setText(model.getClassName());
        this.methodTextField.setText(model.getMethodName());
    }

    @Override
    public MethodSettingModel getModel() {
        return this.model;
    }
}
