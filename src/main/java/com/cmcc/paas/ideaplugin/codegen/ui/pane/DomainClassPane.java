package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.gen.CtrlClassGenerator;
import com.cmcc.paas.ideaplugin.codegen.gen.DomainModelGenerator;
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
    private JButton deleteButton;
    private JLabel classNameLabel;
    private JButton alterButton;
    private JButton previewButton;
    private ClassModel classModel;
    private DomainType domainType;

    public DomainModelActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(DomainModelActionListener actionListener) {
        this.actionListener = actionListener;
    }

    private DomainModelActionListener actionListener;

    public DomainType getDomainType() {
        return domainType;
    }

    public void setDomainType(DomainType domainType) {
        this.domainType = domainType;
    }

    public DomainClassPane(DomainType domainType, ClassModel classModel){
        setDomainType(domainType);
        setClassModel(classModel);
        Arrays.stream(this.content.getComponents()).forEach(e -> e.setBackground(null));
        previewButton.addActionListener(actionEvent -> {
            String c = DomainModelGenerator.createClass(classModel, true);
            CodePreviewDialog.preview(c);
        });
        alterButton.addActionListener(actionEvent -> {
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
            });
        deleteButton.addActionListener(actionEvent ->{
            if (actionListener != null){
                actionListener.onDomainClassRemove(this);
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
    public static interface DomainModelActionListener{
        public void onDomainClassRemove(DomainClassPane domainClassPane);
        public void onDomainClassAlter(DomainClassPane domainClassPane);
    }
}
