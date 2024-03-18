package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.services.ResourceService;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class MethodCreateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JButton buttonOK;
    private JTextField methodNameTextField;
    private JLabel label;
    private JCheckBox ctrlCheckBox;
    private JCheckBox svcCheckBox;
    private JCheckBox daoCheckBox;
    private JComboBox methodTypeComBox;
    private MethodCreateDialogListener listener;
    private List<String> methodTypes;

    public MethodCreateDialogListener getListener() {
        return listener;
    }

    public void setListener(MethodCreateDialogListener listener) {
        this.listener = listener;
    }

    public MethodCreateDialog() {
        setTitle("创建方法");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        methodTypeComBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String methodType = methodTypeComBox.getSelectedItem().toString();
                    methodNameTextField.setText(methodType);
                }
            }
        });
    }

    public List<String> getMethodTypes() {
        return methodTypes;
    }

    public void setMethodTypes(List<String> methodTypes) {
        this.methodTypes = methodTypes;
        DefaultComboBoxModel<String>  methodTypeModel = new DefaultComboBoxModel<>();
        methodTypes.forEach(e -> methodTypeModel.addElement(e));
        methodTypeComBox.setModel(methodTypeModel);
        methodTypeComBox.setSelectedIndex(-1);
    }

    private void onOK() {
        // add your code here
        if (StringUtils.isEmpty(methodNameTextField.getText())){
            MessageBox.showMessageAndFadeout("请输入方法名！");
            return;
        }
        if (methodTypeComBox.getSelectedIndex() < 0){
            MessageBox.showMessageAndFadeout("请选择方法类型！");
            return;
        }
        if (!ctrlCheckBox.isSelected() && !svcCheckBox.isSelected() && !daoCheckBox.isSelected()){
            MessageBox.showMessageAndFadeout("请至少选中一个生成类！");
            return;
        }
        if (listener != null){
            listener.onOK( this);
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
    public String getMethodType(){
        return methodTypeComBox.getSelectedItem().toString();
    }
    public String getMethodName(){
        return methodNameTextField.getText();
    }
    public Boolean isCtrlChecked(){
        return ctrlCheckBox.isSelected();
    }
    public Boolean isSvcChecked(){
        return svcCheckBox.isSelected();
    }
    public Boolean isDaoChecked(){
        return daoCheckBox.isSelected();
    }
    public static MethodCreateDialog create() {
        MethodCreateDialog dialog = new MethodCreateDialog();
        dialog.pack();
        return dialog;
    }
    public interface MethodCreateDialogListener{
        public void onOK( MethodCreateDialog dialog );
    }
}
