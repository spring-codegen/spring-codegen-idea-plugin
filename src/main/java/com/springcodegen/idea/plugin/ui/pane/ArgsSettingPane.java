package com.springcodegen.idea.plugin.ui.pane;

import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.notify.NotificationCenter;
import com.springcodegen.idea.plugin.ui.dialog.DomainSelectionDialog;
import com.intellij.uiDesigner.core.GridConstraints;
import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.ui.dialog.DomainSelectionDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.springcodegen.idea.plugin.notify.NotificationType.MODEL_UPDATED;

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

        addArgButton.addActionListener(new ActionListener() {
            /**
             * @param actionEvent
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DomainSelectionDialog dialog = DomainSelectionDialog.create();
                dialog.setListener(new DomainSelectionDialog.SelectionListener() {
                    /**
                     * @param classModel
                     */
                    @Override
                    public void onSelectedDomain(ClassModel classModel) {
                        MethodSettingPane.MethodSettingModel.MethodArgModel arg = new MethodSettingPane.MethodSettingModel.MethodArgModel();
                        arg.setClassName(classModel.getClassName());
                        addArgPane(arg);
                    }
                });
                dialog.setVisible(true);

            }
        });

        NotificationCenter.register(MODEL_UPDATED, msg -> {
            ClassModel cls = (ClassModel) msg.getData();

        });
    }
    public void addArgPane(MethodSettingPane.MethodSettingModel.MethodArgModel methodArgModel){
//        this.argPaneContainer.setLayout(new GridLayout(1, argPanes.size() + 1, 2, 3));
        boolean isExists = argPanes.stream()
                .filter( p -> p.getArg().getClassName().equalsIgnoreCase(methodArgModel.getClassName()))
                .count() > 0;
        if (isExists){
            return;
        }
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setRow(0);
        gridConstraints.setColumn(argPanes.size());
        ArgPane argPane = new ArgPane();
        argPane.setArg(methodArgModel, methodArgModel.getPathVar()?String.format("{%s}", methodArgModel.getRefName()) : methodArgModel.getClassName());
        argPanes.add(argPane);
        argPaneContainer.add(argPane.getContent());
    }
    public void setArgs(List<MethodSettingPane.MethodSettingModel.MethodArgModel> args) {
        this.args = args;
        args.forEach( e -> addArgPane(e));
    }

    public void updateClassModel(ClassModel classModel){
        argPanes.stream().forEach( e -> {
            if (e.getArg().getClassModel() == classModel){
                e.getArg().setClassName(classModel.getClassName());
                e.setArg(e.getArg(), classModel.getClassName());
            }
        });
    }
    public JPanel getContent() {
        return content;
    }
}
