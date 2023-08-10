package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.MethodGrpCfgModel;

import javax.swing.*;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class MvcItemCfgPanel {
    private JPanel content;
    private JComboBox httpMethodComboBox;
    private JTextField baseURITextField;
    private MvcMethodCfgPanel ctrlMethodCfgPanel;
    private MvcMethodCfgPanel svcMethodCfgPanel;
    private MvcMethodCfgPanel daoMethodCfgPanel;

    private MethodGrpCfgModel model;
    public JPanel getContent() {
        return this.content;
    }

    public void init(){
        httpMethodComboBox.setModel(new DefaultComboBoxModel(new String[]{"GET","POST","DELETE","PUT"}));
    }
    public void setModel(MethodGrpCfgModel model) {
        this.model = model;
        this.httpMethodComboBox.setSelectedItem(model.getUri().getHttpMethod());
        this.baseURITextField.setText(model.getUri().getPath());
        ctrlMethodCfgPanel.setModel(model.getCtrl());
        svcMethodCfgPanel.setModel(model.getSvc());
        daoMethodCfgPanel.setModel(model.getDao());
    }

    public MethodGrpCfgModel getModel() {
        return model;
    }
}