package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.gen.CtrlClassGenerator;
import com.cmcc.paas.ideaplugin.codegen.gen.SvcClassGenerator;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class SvcMethodSettingPane extends MethodSettingPane {
    protected JLabel clsTagLabel;
    protected JTextField clsTextField;
    protected JTextField methodTextField;
    protected JPanel content;
    protected JCheckBox outputListTypeCheckBox;
    protected JCheckBox outputPagedCheckBox;
    private JButton closeBtn;
//    private ArgsSettingPane argsSettingPane;
    private JComboBox resultComboBox;
    private JComboBox argComboBox;
    private JButton previewButton;

    //    protected MethodSettingModel model;
    private ClassModel.Method method = null;

    public SvcMethodSettingPane(){
        init();
    }
    public void init(){
        super.init();
        previewButton.addActionListener(actionEvent -> {
//        String c = "<html>" + CtrlClassGenerator.createMethod(method).toString().replaceAll("\n", "<br/>") + "</html>";
            String c = SvcClassGenerator.createMethod(method).toString();
            CodePreviewDialog.preview(c);
        });
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, textField -> {
                        if (textField == methodTextField) {
                            method.setName(methodTextField.getText());
                        }
                });
            }
            if (component instanceof JCheckBox){
                ((JCheckBox)component).addItemListener(itemEvent -> {
                        if (itemEvent.getSource() == outputListTypeCheckBox) {
                            method.getResult().setListTypeFlag(outputListTypeCheckBox.isSelected());
                        }
                        if (itemEvent.getSource() == outputPagedCheckBox) {
                            method.getResult().setOutputPaged(outputPagedCheckBox.isSelected());
                        }
                });
            }
        }
        setCloseBtnAction(closeBtn);
    }
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

//    public MethodSettingModel getModel(){
//        return model;
//    }
//    public void setModel(MethodSettingModel model) {
//        this.model = model;
//        this.clsTextField.setText(model.getClassName());
//        this.methodTextField.setText(model.getMethodName());
////        argsSettingPane.setArgs(model.getArgs());
//        resetArgComboBox();
//        resetReturnComboBox();
//    }

    @Override
    public ClassModel.Method getMethod() {
        return method;
    }

    public void setMethod(ClassModel.Method method) {
        this.method = method;
        methodTextField.setText(method.getName());
        resetArgComboBox();
        resetReturnComboBox();
    }

    @Override
    public MvcClassType getClassType() {
        return MvcClassType.SVC;
    }

    @Override
    public JComboBox getReturnComboBox() {
        return resultComboBox;
    }
//    @Override
//    public ArgsSettingPane getArgsSettingPane() {
//        return argsSettingPane;
//    }

    @Override
    public JComboBox getArgComboBox() {
        return argComboBox;
    }
}
