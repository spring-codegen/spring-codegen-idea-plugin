package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass;
import com.cmcc.paas.ideaplugin.codegen.setting.CtrlSetting;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils.TextChangedEvent;
import com.cmcc.paas.ideaplugin.codegen.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Font.BOLD;
import static java.awt.Font.PLAIN;

/**
 * @author zhangyinghui
 * @date 2023/8/8
 */
public class CtrlMethodSettingPane extends MethodSettingPane {
    protected JLabel clsTagLabel;
    protected JTextField pathTextField;
    protected JTextField methodTextField;
    protected JTextField outputClsTextField;
    protected JPanel content;
    protected JCheckBox outputListTypeCheckBox;
    protected JCheckBox outputPagedCheckBox;
    protected JButton inputButton;
    protected JButton outputButton;
    private JButton closeBtn;
    private JComboBox resultComboBox;
    private JComboBox argComboBox;
    private JLabel methodTypeLabel;
    private JTextArea commentTextArea;

//    protected MethodSettingModel model;
    private Color PATH_TEXT_FIELD_COLOR = Color.decode("#BBBBBB");
    private Color PATH_TEXT_FIELD_COLOR_HL = Color.decode("#BB9E1B");
    protected CtrlClass.Method method = new CtrlClass.Method("", new ArrayList<ClassModel.MethodArg>(), null);


    public CtrlMethodSettingPane(){
        init();
        TextFieldUtils.INSTANCE.addTextChangedEvent(pathTextField, textField -> {
            updatePathTextFieldUI();
        });
    }
    public void init(){
        super.init();
        outputPagedCheckBox.setBackground(null);
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, textField -> {
                    if (textField == pathTextField) {
                        method.getRequest().setPath(pathTextField.getText());
                    }
                    if (textField == methodTextField) {
                        method.setName(methodTextField.getText());
                    }
                });
            }
            if (component instanceof JCheckBox){
                ((JCheckBox)component).addItemListener(itemEvent->{
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
    private void updatePathTextFieldUI(){
        List<String> a = StringUtils.INSTANCE.parsePlaceholders(pathTextField.getText());
        if (a != null){
            pathTextField.setForeground( PATH_TEXT_FIELD_COLOR_HL);
            pathTextField.setFont( pathTextField.getFont().deriveFont( BOLD) );
            pathTextField.setToolTipText(String.format("路径参数%s会作为方法的入参",  String.join(",", a.toArray(new String[0])) ) );
        }else{
            pathTextField.setForeground( PATH_TEXT_FIELD_COLOR );
            pathTextField.setFont( pathTextField.getFont().deriveFont(PLAIN ) );
            pathTextField.setToolTipText(null);
        }
    }
    public JPanel getContent() {
        return content;
    }

//    public MethodSettingModel getModel(){
//        model.setComment(commentTextArea.getText());
//        return model;
//    }

    public CtrlClass.Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(ClassModel.Method method) {
        this.method = (CtrlClass.Method) method;
        this.pathTextField.setText(this.method.getRequest().getPath());
        this.methodTextField.setText(this.method.getName());
        this.methodTypeLabel.setText(this.method.getRequest().getHttpMethod());
        this.commentTextArea.setText(this.method.getComment());
        if (this.method.getResult() != null) {
            this.outputListTypeCheckBox.setSelected(this.method.getResult().getListTypeFlag());
            this.outputPagedCheckBox.setSelected(this.method.getResult().getOutputPaged());
        }
        resetArgComboBox();
        resetReturnComboBox();
        updatePathTextFieldUI();
    }

    @Override
    public MvcClassType getClassType() {
        return MvcClassType.CTRL;
    }

    //    public void setModel(MethodSettingModel model) {
//        this.model = model;
//        this.pathTextField.setText(model.getPath());
//        this.methodTextField.setText(model.getMethodName());
//        this.methodTypeLabel.setText(model.getHttpMethod());
//        this.commentTextArea.setText(model.getComment());
//        if (model.getResult() != null) {
//            this.outputListTypeCheckBox.setSelected(model.getResult().getListTypeFlag() == null ? false : model.getResult().getListTypeFlag());
//            this.outputPagedCheckBox.setSelected(model.getResult().getOutputPaged());
//        }
//        resetArgComboBox();
//        resetReturnComboBox();
//        updatePathTextFieldUI();
//        refreshMethod();
//    }
    @Override
    public JComboBox getReturnComboBox() {
        return resultComboBox;
    }

    @Override
    public JComboBox getArgComboBox() {
        return argComboBox;
    }

//    @Override
//    public ArgsSettingPane getArgsSettingPane() {
//        return argsSettingPane;
//    }
}
