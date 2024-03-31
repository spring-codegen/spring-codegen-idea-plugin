package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import javax.swing.*;
import java.awt.event.*;

public class CodePreviewDialog extends JDialog {
    private static CodePreviewDialog instance = new CodePreviewDialog();
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextArea codeTextArea;
    private JScrollPane scrollPane;

    private CodePreviewDialog() {
//        scrollPane.setBorder(null);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static CodePreviewDialog preview(String code) {
        instance.codeTextArea.setText(code);
        instance.pack();
        instance.setVisible(true);
        return instance;
    }
}
