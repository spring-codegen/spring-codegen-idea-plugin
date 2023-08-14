package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.MethodGrpCfgModel;
import com.github.baboy.ideaplugincodegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

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
    private JTextField methodCommentTextField;
    private JButton removeBtn;

    private MethodGrpCfgModel model;
    public MvcItemCfgPanel() {
        removeBtn.setBorder(null);
        removeBtn.setOpaque(false);
        TextFieldUtils.INSTANCE.addTextChangedEvent(methodCommentTextField, new TextFieldUtils.TextChangedEvent() {
            @Override
            public void onTextChanged(@NotNull JTextField textField) {
                getModel().getCtrl().setComment(textField.getText());
                getModel().getSvc().setComment(textField.getText());
                getModel().getDao().setComment(textField.getText());
            }
        });
    }

    public JPanel getContent() {
        return this.content;
    }

    public void init(){
        httpMethodComboBox.setModel(new DefaultComboBoxModel(new String[]{"GET","POST","DELETE","PUT"}));
    }
    public void setModel(MethodGrpCfgModel model) {
        this.model = model;
        this.httpMethodComboBox.setSelectedItem(model.getRequest().getHttpMethod());
        this.baseURITextField.setText(model.getRequest().getPath());
        this.methodCommentTextField.setText(model.getRequest().getComment());
        ctrlMethodCfgPanel.setModel(model.getCtrl());
        svcMethodCfgPanel.setModel(model.getSvc());
        daoMethodCfgPanel.setModel(model.getDao());
    }

    public MethodGrpCfgModel getModel() {
        return model;
    }
}
