package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import javax.swing.*;

/**
 * @author zhangyinghui
 * @date 2024/3/18
 */
public class ArgPane {
    private JLabel textLabel;
    private JButton closeBtn;
    private JPanel content;
    public Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    public void setText(String text){
        this.textLabel.setText(text);
    }
    public String getText(){
        return textLabel.getText();
    }
    public void setTip(String tip){
        this.textLabel.setToolTipText(tip);
    }

    public JPanel getContent() {
        return content;
    }

}
