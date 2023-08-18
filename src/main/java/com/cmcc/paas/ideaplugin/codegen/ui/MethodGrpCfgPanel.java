package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.MethodGrpCfgModel;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class MethodGrpCfgPanel {
    private JPanel content;
    private JComboBox httpMethodComboBox;
    private JTextField baseURITextField;
    private MethodCfgPanel ctrlMethodCfgPanel;
    private MethodCfgPanel svcMethodCfgPanel;
    private MethodCfgPanel daoMethodCfgPanel;
    private JTextField methodCommentTextField;
    private JButton removeBtn;

    private MethodGrpCfgModel model;
    private RemoveEvent removeEvent;
    public MethodGrpCfgPanel() {
        removeBtn.setBorder(null);
        TextFieldUtils.INSTANCE.addTextChangedEvent(methodCommentTextField, new TextFieldUtils.TextChangedEvent() {
            @Override
            public void onTextChanged(@NotNull JTextField textField) {
                getModel().getCtrl().setComment(textField.getText());
                getModel().getSvc().setComment(textField.getText());
                getModel().getDao().setComment(textField.getText());
            }
        });
        MethodGrpCfgPanel handler = this;
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int ret = JOptionPane.showOptionDialog(Window.getWindows()[0], String.format("确认删除%s方法？", getModel().getCtrl().getName()), "确认珊瑚",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"确定","取消"}, "确定");
                if (ret == 0){
                    if (removeEvent != null){
                        removeEvent.onRemove(handler);
                    }
                }
                System.out.println("press:"+ret);
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
        model.setCtrl(ctrlMethodCfgPanel.getModel());
        model.setSvc(svcMethodCfgPanel.getModel());
        model.setDao(daoMethodCfgPanel.getModel());
        return model;
    }
    public void setRemoveEvent(RemoveEvent evt){
        this.removeEvent = evt;
    }
    public interface RemoveEvent{
        public void onRemove(MethodGrpCfgPanel grpCfgPanel);
    }
}
