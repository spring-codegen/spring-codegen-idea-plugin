package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.model.DataSourceConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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


    public CodeGenPanel() {
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                test();
            }
        });
        init();
    }

    /**
     * 初始化参数
     * 从缓存加载配置
     *
     */
    private void init(){

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
