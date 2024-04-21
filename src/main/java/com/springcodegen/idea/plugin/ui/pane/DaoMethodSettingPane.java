package com.springcodegen.idea.plugin.ui.pane;

import com.springcodegen.idea.plugin.constants.MvcClassType;
import com.springcodegen.idea.plugin.ctx.AppCtx;
import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.gen.model.DaoClass;
import com.springcodegen.idea.plugin.swing.util.TextFieldUtils;
import com.springcodegen.idea.plugin.ui.BeanFieldSelectionDialog;
import javax.swing.*;
import java.awt.*;

/**
 * @author zhangyinghui
 * @date 2023/12/21
 */
public class DaoMethodSettingPane extends MethodSettingPane {
    private JTextField methodTextField;
    private JCheckBox outputListTypeCheckBox;
    private JButton dataFieldButton;
    private JButton whereFieldButton;
    private JPanel content;
    private JButton closeBtn;
    private JComboBox resultComboBox;
    private JComboBox argComboBox;
    private JLabel clsNameLabel;
    private DaoClass.Method method = null;

    public DaoMethodSettingPane(){
        super.init();
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, textField -> {
                    if (textField == methodTextField) {
                        method.setName(methodTextField.getText());
                    }
                });
            }
            if (component instanceof JCheckBox){
                ((JCheckBox)component).addItemListener( itemEvent ->{
                    if (itemEvent.getSource() == outputListTypeCheckBox) {
                        method.getResult().setListTypeFlag(outputListTypeCheckBox.isSelected());
                    }
                });
            }
        }
        dataFieldButton.addActionListener(e -> {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(AppCtx.getCurrentTable().getFields());
                dialog.setSelectedFields(method.getSqlDataFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        method.setSqlDataFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            });
        whereFieldButton.addActionListener(e -> {
                BeanFieldSelectionDialog dialog = BeanFieldSelectionDialog.create();
                dialog.setFields(AppCtx.getCurrentTable().getFields());
                dialog.setSelectedFields(method.getSqlCondFields());
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        method.setSqlCondFields(dialog.getSelectedFields());
                    }
                });
                dialog.setVisible(true);
            });


        setCloseBtnAction(closeBtn);
    }

    @Override
    public JPanel getContent() {
        return content;
    }


    @Override
    public DaoClass.Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(ClassModel.Method method) {
        super.setMethod(method);
        this.method = (DaoClass.Method)method;
        this.methodTextField.setText(method.getName());
        this.outputListTypeCheckBox.setSelected(method.getResult().getListTypeFlag());
        resetArgComboBox();
        resetReturnComboBox();
    }
    @Override
    public JLabel getClassLabel() {
        return clsNameLabel;
    }

    @Override
    public MvcClassType getClassType() {
        return MvcClassType.DAO;
    }

    @Override
    public JComboBox getReturnComboBox() {
        return resultComboBox;
    }


    @Override
    public JComboBox getArgComboBox() {
        return argComboBox;
    }
}
