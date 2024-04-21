package com.springcodegen.idea.plugin.ui.dialog;

import com.springcodegen.idea.plugin.constants.DomainType;
import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.ctx.DomainModelCtx;
import com.springcodegen.idea.plugin.constants.DomainType;
import com.springcodegen.idea.plugin.gen.model.ClassModel;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class DomainSelectionDialog extends JDialog implements ActionListener{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton argsRadioButton;
    private JRadioButton entitiesRadioButton;
    private JRadioButton resultsRadioButton;
    private JComboBox modelComboBox;
    private SelectionListener listener;

    public SelectionListener getListener() {
        return listener;
    }

    public void setListener(SelectionListener listener) {
        this.listener = listener;
    }

    public DomainSelectionDialog() {
        setTitle("选择模型");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (listener != null){
                    List<ClassModel> clses = DomainModelCtx.INSTANCE.getModesByType(getSelectedType());
                    clses.forEach( cls -> {
                        if (cls.getClassName().equalsIgnoreCase((String)modelComboBox.getSelectedItem())){
                            listener.onSelectedDomain(cls);
                        }
                    });
                }
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
    private DomainType getSelectedType(){
        return argsRadioButton.isSelected() ? DomainType.ARG :
                entitiesRadioButton.isSelected() ? DomainType.ENTITY : DomainType.RESULT;
    }
    private void selectWithType(DomainType type){
        List<ClassModel> clses = DomainModelCtx.INSTANCE.getModesByType(type);
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

    public static DomainSelectionDialog create() {
        DomainSelectionDialog dialog = new DomainSelectionDialog();
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
    public static interface SelectionListener {
        public void onSelectedDomain(ClassModel classModel);
    }
}
