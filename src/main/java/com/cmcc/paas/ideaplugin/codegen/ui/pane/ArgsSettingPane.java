package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ArgsSettingPane {
    private JPanel content;
    private JPanel argPaneContainer;
    private List<ArgPane> argPanes = new ArrayList<>();
    private JButton addArgButton;

    private List<MethodSettingPane.MethodSettingModel.MethodArgModel> args;
    public List<MethodSettingPane.MethodSettingModel.MethodArgModel> getArgs() {
        return args;
    }
    public ArgsSettingPane(){

    }
    public void addArgPane(MethodSettingPane.MethodSettingModel.MethodArgModel methodArgModel){
        this.content.setLayout(new GridLayout(1, argPanes.size() + 1, 2, 3));
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setRow(0);
        gridConstraints.setColumn(argPanes.size());
        ArgPane argPane = new ArgPane();
        argPane.setArg(methodArgModel, methodArgModel.getPathVar()?String.format("{%s}", methodArgModel.getRefName()) : methodArgModel.getClassName());
        argPanes.add(argPane);
        argPaneContainer.add(argPane.getContent(), gridConstraints);
    }
    public void setArgs(List<MethodSettingPane.MethodSettingModel.MethodArgModel> args) {
        this.args = args;
        args.forEach( e -> addArgPane(e));
    }


    public JPanel getContent() {
        return content;
    }
}
