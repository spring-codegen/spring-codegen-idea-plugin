package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.ui.BeanFieldSelectionDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

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
    private List<DBTableField> tableFields;

    public List<DBTableField> getTableFields() {
        return tableFields;
    }

    public void setTableFields(List<DBTableField> tableFields) {
        this.tableFields = tableFields;
    }

    public DomainClassPane(){
        Arrays.stream(this.content.getComponents()).forEach(e -> e.setBackground(null));
        alterButton.addActionListener(new ActionListener() {
            /**
             * @param actionEvent
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(tableFields);
                dialog.setSelectedFields(classModel.getFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        classModel.setFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            /**
             * @param actionEvent
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

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
}
