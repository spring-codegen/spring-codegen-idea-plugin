package com.cmcc.paas.ideaplugin.codegen.ui;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;

public class MethodCreateDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JButton buttonOK;
    private JTextField methodNameTextField;
    private JLabel label;
    private JCheckBox ctrlCheckBox;
    private JCheckBox svcCheckBox;
    private JCheckBox daoCheckBox;
    private MethodCreateDialogListener listener;

    public MethodCreateDialogListener getListener() {
        return listener;
    }

    public void setListener(MethodCreateDialogListener listener) {
        this.listener = listener;
    }

    public MethodCreateDialog() {
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
    }

    private void onOK() {
        // add your code here
        if (StringUtils.isEmpty(methodNameTextField.getText())){
            return;
        }
        if (listener != null){
            listener.onOK(methodNameTextField.getText(), ctrlCheckBox.isSelected(), svcCheckBox.isSelected(), daoCheckBox.isSelected());
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static MethodCreateDialog create() {
        MethodCreateDialog dialog = new MethodCreateDialog();
        dialog.pack();
        return dialog;
    }
    public interface MethodCreateDialogListener{
        public void onOK(String methodName, Boolean ctrlChecked, Boolean svcChecked, Boolean daoChecked );
    }
}
