package com.springcodegen.idea.plugin.ui.pane;

import com.springcodegen.idea.plugin.constants.DomainType;
import com.springcodegen.idea.plugin.constants.MvcClassType;
import com.springcodegen.idea.plugin.gen.CtrlClassGenerator;
import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.gen.model.CtrlClass;
import com.springcodegen.idea.plugin.swing.util.TextFieldUtils;
import com.springcodegen.idea.plugin.util.StringUtils;

import javax.swing.*;
import java.awt.*;
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
    protected JPanel content;
    protected JCheckBox outputListTypeCheckBox;
    protected JCheckBox outputPagedCheckBox;
    private JButton closeBtn;
    private JComboBox resultComboBox;
    private JComboBox argComboBox;
    private JLabel methodTypeLabel;
    private JTextArea commentTextArea;
    private JButton previewButton;
    private JLabel clsNameLabel;
    private JLabel methodNameDescLabel;

    //    protected MethodSettingModel model;
    private Color PATH_TEXT_FIELD_COLOR = Color.decode("#BBBBBB");
    private Color PATH_TEXT_FIELD_COLOR_HL = Color.decode("#BB9E1B");
    private Color BG_COLOR = Color.decode("#383C3E");
    private Color BG_COLOR_GRAY = Color.decode("#464B4E");
    protected CtrlClass.Method method = new CtrlClass.Method("", new ArrayList<ClassModel.MethodArg>(), null);


    public CtrlMethodSettingPane(){
        super.init();
        previewButton.addActionListener(actionEvent -> {
                String c = CtrlClassGenerator.createMethod(method).toString();
                CodePreviewDialog.preview(c);
        });
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, textField -> {
                    if (textField == pathTextField) {
                        method.getRequest().setPath(pathTextField.getText());
                        updatePathTextFieldUI();
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
    public CtrlClass.Method getMethod() {
        return method;
    }

    @Override
    public void setMethod(ClassModel.Method method) {
        super.setMethod(method);
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
        dataChanged();
    }
    @Override
    public void dataChanged(){
        Object m = CtrlClassGenerator.parseExistMethod(method);
        content.setBackground( m == null ? BG_COLOR : BG_COLOR_GRAY);
        methodNameDescLabel.setVisible(m == null ? false : true);
    }
    @Override
    public MvcClassType getClassType() {
        return MvcClassType.CTRL;
    }

    /**
     * @return
     */
    @Override
    public JLabel getClassLabel() {
        return clsNameLabel;
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
