package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter;
import com.cmcc.paas.ideaplugin.codegen.ui.BeanFieldSelectionDialog;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * @author zhangyinghui
 * @date 2024/3/18
 */
public class DomainClassPane {
    private JPanel content;
    private JButton fieldSelectionButton;
    private JButton deleteButton;
    private JLabel classNameLabel;
    private JButton alterButton;
    private ClassModel classModel;
    private OperationActionListener actionListener;

    public DomainClassPane(){
        Arrays.stream(this.content.getComponents()).forEach(e -> e.setBackground(null));
        alterButton.addActionListener(new ActionListener() {
            /**
             * @param actionEvent
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(AppCtx.INSTANCE.getCurrentTable().getFields());
                dialog.setSelectedFields(classModel.getFields());
                dialog.setClassName(classModel.getClassName());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        classModel.setFields(dialog.getSelectedFields());
                        if (!classModel.getClassName().equalsIgnoreCase(dialog.getClassName())){
                            classModel.setClassName(dialog.getClassName());
                            classNameLabel.setText(dialog.getClassName());
                            NotificationCenter.INSTANCE.sendMessage(NotificationType.MODEL_UPDATED, classModel);
                        }
                    }
                });
                dialog.setVisible(true);
            }
        });
        DomainClassPane handler = this;
        deleteButton.addActionListener(new ActionListener() {
            /**
             * @param actionEvent
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (actionListener != null){
                    actionListener.onDomainClassRemove(handler);
                }
            }
        });
    }

    public JPanel getContent() {
        return content;
    }


    public ClassModel getClassModel() {
        return classModel;
    }

    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
        classNameLabel.setText(classModel.getClassName());
        classNameLabel.setToolTipText(String.format("%s %s", classModel.getClassName(), classModel.getRefName()));
    }
    public static interface OperationActionListener{
        public void onDomainClassRemove(DomainClassPane domainClassPane);
        public void onDomainClassAlter(DomainClassPane domainClassPane);
    }
}
