package com.cmcc.paas.ideaplugin.codegen.ui.dialog;

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;

public class DomainSelectionDialog extends JDialog implements ActionListener{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton argsRadioButton;
    private JRadioButton entitiesRadioButton;
    private JRadioButton resultsRadioButton;
    private JComboBox modelComboBox;
    private Map<DomainType, List<ClassModel>> modelMaps;

    public DomainSelectionDialog(Map<DomainType, List<ClassModel>> modelMaps) {
        setTitle("选择模型");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.modelMaps = modelMaps;

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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
        argsRadioButton.addActionListener(this);
        entitiesRadioButton.addActionListener(this);
        resultsRadioButton.addActionListener(this);
    }
    private void selectWithType(DomainType type){
        List<ClassModel> clses = modelMaps.get(type);
        DefaultComboBoxModel<String>  methodTypeModel = new DefaultComboBoxModel<>();
        clses.forEach(e -> methodTypeModel.addElement(e.getClassName()));
        modelComboBox.setModel(methodTypeModel);
        modelComboBox.setSelectedIndex(-1);
    }
    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static DomainSelectionDialog create(Map<DomainType, List<ClassModel>> modelMaps) {
        DomainSelectionDialog dialog = new DomainSelectionDialog(modelMaps);
        dialog.pack();
        return dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == argsRadioButton){
            selectWithType(DomainType.ARG);
        }
        if (e.getSource() == entitiesRadioButton){
            selectWithType(DomainType.ENTITY);
        }
        if (e.getSource() == resultsRadioButton){
            selectWithType(DomainType.RESULT);
        }
    }
}
