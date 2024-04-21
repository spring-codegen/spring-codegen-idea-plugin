package com.springcodegen.idea.plugin.ui.pane;

import com.springcodegen.idea.plugin.constants.DomainType;
import com.springcodegen.idea.plugin.gen.DomainModelGenerator;
import com.springcodegen.idea.plugin.ctx.AppCtx;
import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.notify.NotificationCenter;
import com.springcodegen.idea.plugin.ui.BeanFieldSelectionDialog;
import com.springcodegen.idea.plugin.notify.NotificationType;

import javax.swing.*;
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
    private JLabel existLabel;
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
                dialog.setFields(AppCtx.getCurrentTable().getFields());
                dialog.setSelectedFields(classModel.getFields());
                dialog.setClassName(classModel.getClassName());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        classModel.setFields(dialog.getSelectedFields());
                        if (!classModel.getClassName().equalsIgnoreCase(dialog.getClassName())){
                            classModel.setClassName(dialog.getClassName());
                            classNameLabel.setText(dialog.getClassName());
                            classUpdated();
                            NotificationCenter.sendMessage(NotificationType.MODEL_UPDATED, classModel);
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

    private void classUpdated(){
        Boolean exist = DomainModelGenerator.fileExists(classModel);
        existLabel.setText(exist ? "!" : "");
        classNameLabel.setToolTipText(exist ? "已存在" : "");
    }
    public void setClassModel(ClassModel classModel) {
        this.classModel = classModel;
        classNameLabel.setText(classModel.getClassName());
        classUpdated();
    }
    public static interface DomainModelActionListener{
        public void onDomainClassRemove(DomainClassPane domainClassPane);
        public void onDomainClassAlter(DomainClassPane domainClassPane);
    }
}
