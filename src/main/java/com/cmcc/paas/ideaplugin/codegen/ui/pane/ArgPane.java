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
    public MethodSettingPane.MethodSettingModel.MethodArgModel arg;

    public MethodSettingPane.MethodSettingModel.MethodArgModel getArg() {
        return arg;
    }

    public void setArg(MethodSettingPane.MethodSettingModel.MethodArgModel arg, String displayName) {
        this.arg = arg;
        textLabel.setText(displayName);
        textLabel.setToolTipText(String.format("%s %s %s",
                arg.getPathVar() ? "@PathVariable(\""+arg.getRefName()+"\")":"",
                arg.getClassName(),
                arg.getRefName() == null ? "" : arg.getRefName()));
    }

    public JPanel getContent() {
        return content;
    }

}
