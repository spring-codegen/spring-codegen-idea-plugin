package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.model.CtrlConfig;
import com.github.baboy.ideaplugincodegen.model.DataSourceConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class CodeGenPanel {
    private JTabbedPane tabbedPanel;
    private JPanel mainPanel;
    private JPanel codePanel;
    private JPanel sourcePanel;
    private JPanel settingPanel;
    private JTextField usernameTextField;
    private JTextField pwdTextField;
    private JTextField dbUrlTextField;
    private JButton testButton;
    private JComboBox tableComboBox;
    private JTextField ctrlDirTextField;
    private JTextField ctrlClsNameTextField;
    private JTextField baseUriTextField;
    private JTable ctrlTable;

    private String tableName;
    private CtrlConfig ctrlConfig;

    public CodeGenPanel() {
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                test();
            }
        });
        init();

        tableComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // 选择的下拉框选项
                    System.out.println(e.getItem());
                    tableUpdated();
                }
            }
        });
    }
    private void setDbTableItems(List<String> items){
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        // elements 下拉框中的选项
        for (String element : items) {
            comboBoxModel.addElement(element);
        }
        tableComboBox.setModel(comboBoxModel);
    }
    private void ctrlDirUpdated(){
        ctrlConfig.setBaseURI(String.format("/api/v1/%s", ctrlConfig.getDir()));
        baseUriTextField.setText(ctrlConfig.getBaseURI());
    }
    private void tableUpdated(){
        String tableName = tableComboBox.getSelectedItem().toString();
        String tableSymbol = tableName;
        ctrlConfig.setClsName(String.format("%sController", tableSymbol));

    }
    /**
     *
     */
    private void init(){
        ctrlConfig = new CtrlConfig();
        List tables = new ArrayList();
        tables.add("t_table1");
        tables.add("t_tables2");
        setDbTableItems(tables);
        String[] ctrlMethods = new String[]{"add", "remove", "update", "get", "search"};
        List<CtrlConfig.CtrlMethod> methods = new ArrayList<>();
        for (String m: ctrlMethods){
            CtrlConfig.CtrlMethod method = new CtrlConfig.CtrlMethod();
            method.setName(m);
            method.setPath(String.format("/%s", m));
            method.setRequestMethod("POST");
            methods.add(method);
        }
        ctrlConfig.setMethods(methods);
    }
    private DataSourceConfig getDataSourceConfig(){
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(dbUrlTextField.getText());
        dataSourceConfig.setUsername(usernameTextField.getText());
        dataSourceConfig.setPwd(pwdTextField.getText());
        return dataSourceConfig;
    }
    private void test(){
        DataSourceConfig dataSourceConfig = getDataSourceConfig();
        System.out.println("url:"+ dataSourceConfig.getUrl());
        System.out.println("username:"+ dataSourceConfig.getUsername());
        System.out.println("password:"+ dataSourceConfig.getPwd());
    }

    public JPanel getContent()  {
        return this.mainPanel;
    }

}
