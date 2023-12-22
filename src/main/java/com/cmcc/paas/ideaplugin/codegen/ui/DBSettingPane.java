package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import com.cmcc.paas.ideaplugin.codegen.config.DBCfg;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author zhangyinghui
 * @date 2023/8/16
 */
public class DBSettingPane {
    private JTextField tableSchemaTextField;
    private JTextField hostTextField;
    private JTextField dbNameTextField;
    private JTextField portTextField;
    private JTextField userTextField;
    private JPanel content;
    private JTextField pwdTextField;
    private JButton 保存Button;
    private ValueChangedListener valueChangedListener;

    private DBCfg model;
    public DBSettingPane(){
        System.out.println("CodeSettingPanel...");
        DBSettingPane handler = this;
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, new TextFieldUtils.TextChangedEvent() {
                    @Override
                    public void onTextChanged(@NotNull JTextField textField) {
                        if (valueChangedListener != null){
                            valueChangedListener.onValueChanged(handler);
                        }

                    }
                });
            }
        }
        保存Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getModel().save();
            }
        });
    }

    public JPanel getContent() {
        return content;
    }

    public ValueChangedListener getValueChangedListener() {
        return valueChangedListener;
    }

    public void setValueChangedListener(ValueChangedListener valueChangedListener) {
        this.valueChangedListener = valueChangedListener;
    }

    public DBCfg getModel() {
        model.setDbName(dbNameTextField.getText());
        model.setHost(hostTextField.getText());
        if (StringUtils.isNotEmpty(portTextField.getText())) {
            Integer p = Integer.parseInt(portTextField.getText());
            model.setPort(p);
        }
        model.setSchema(tableSchemaTextField.getText());
        model.setUser(userTextField.getText());
        model.setPwd(pwdTextField.getText());
        return model;
    }

    public void setModel(DBCfg model) {
        this.model = model;
        if (model.getDbName() != null) {
            dbNameTextField.setText(model.getDbName());
        }
        if (model.getHost() != null) {
            hostTextField.setText(model.getHost());
        }
        if (model.getPort() != null) {
            portTextField.setText(model.getPort().toString());
        }
        if (model.getSchema() != null) {
            tableSchemaTextField.setText(model.getSchema());
        }
        if (model.getUser() != null) {
            userTextField.setText(model.getUser());
        }
        if (model.getPwd() != null) {
            pwdTextField.setText(model.getPwd());
        }

    }
    public interface ValueChangedListener{
        public void onValueChanged(DBSettingPane dbSettingPanel);
    }
}
