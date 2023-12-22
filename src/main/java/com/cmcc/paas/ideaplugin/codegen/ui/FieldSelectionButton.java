package com.cmcc.paas.ideaplugin.codegen.ui;


import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2023/8/3

 */
public class FieldSelectionButton extends JPanel implements Accessible, ActionListener {
    protected boolean isEditable  = false;
    private List<DBTableField> items;
    private MultiPopup popup;
    protected JButton arrowButton;
    private ValueChangedListener valueChangedListener;
    private Boolean supportSelectAll = true;
    public FieldSelectionButton(){
        initComponent();
    }

    public FieldSelectionButton(List<DBTableField> items, Boolean supportSelectAll) {
        this();
        this.setItems(items);
        this.setSupportSelectAll(supportSelectAll);
    }

    private void initComponent() {
        this.setLayout(new BorderLayout());
        popup = new MultiPopup();
        popup.setItems(items);
        popup.setSupportSelectAll(supportSelectAll);

        arrowButton = createArrowButton();
        arrowButton.addActionListener(this);
        add(arrowButton, BorderLayout.CENTER);
        Color c = new Color(100,100,100);
        setBorder(BorderFactory.createBevelBorder(1,c,c,c,c));
    }

    public ValueChangedListener getValueChangedListener() {
        return valueChangedListener;
    }

    public void setValueChangedListener(ValueChangedListener valueChangedListener) {
        this.valueChangedListener = valueChangedListener;
        FieldSelectionButton handler = this;
        popup.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void onValueChanged(FieldSelectionButton btn) {
                valueChangedListener.onValueChanged(handler);
            }
        });
    }

    public List<DBTableField> getItems() {
        return items;
    }

    public void setItems(List<DBTableField> items) {
        this.items = items;
        popup.setItems(items);
    }

    public Boolean getSupportSelectAll() {
        return supportSelectAll;
    }

    public void setSupportSelectAll(Boolean supportSelectAll) {
        if (this.supportSelectAll == supportSelectAll){
            return;
        }
        this.supportSelectAll = supportSelectAll;
        popup.setSupportSelectAll(supportSelectAll);
    }

    public void setEditable(boolean aFlag) {
        boolean oldFlag = isEditable;
        isEditable = aFlag;
        firePropertyChange( "editable", oldFlag, isEditable );
    }

    /**
     * Returns true if the <code>JComboBox</code> is editable.
     * By default, a combo box is not editable.
     *
     * @return true if the <code>JComboBox</code> is editable, else false
     */
    public boolean isEditable() {
        return isEditable;
    }


    public DBTableField[] getSelectedValues() {
        return popup.getSelectedValues();
    }


    public void setSelectValues(DBTableField[] selectvalues) {
        popup.setSelectValues(selectvalues);
        setText(selectvalues);
    }

    private void setText(DBTableField[] values) {
        if (values.length > 0) {
            String[] s = Arrays.stream(values).map(e -> e.getNotNull()? (e.getName()+"!") : e.getName()).toArray(String[]::new);
            arrowButton.setToolTipText(String.join(", ", s));
        } else {
            arrowButton.setToolTipText("");
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (!popup.isVisible()) {
            popup.refresh();
            popup.show(this, 0, getHeight());
        }
    }

    protected JButton createArrowButton() {
//        JButton button = new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"),
//                UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"),
//                UIManager.getColor("ComboBox.buttonHighlight"));
        JButton button = new BasicArrowButton(BasicArrowButton.SOUTH);
        button.setName("ComboBox.arrowButton");
        return button;
    }



    //内部类MultiPopup

    public class MultiPopup extends JPopupMenu implements ActionListener {

        private List<DBTableField> items;
        private List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
        private List<JCheckBox> nullChekBoxList = new ArrayList<JCheckBox>();
        private JButton commitButton;
        private JButton cancelButton;
        private ValueChangedListener valueChangedListener;
        private Boolean supportSelectAll = true;
        private JPanel checkboxPane;
        private JCheckBox checkAllBox;
        private List<DBTableField> selectedValues = new ArrayList<>();

        public MultiPopup() {
            super();
            initComponent();
            refreshItems();
        }

        public List<DBTableField> getItems() {
            return items;
        }

        public void setItems(List<DBTableField> items) {
            this.items = items;
            refreshItems();
        }

        public Boolean getSupportSelectAll() {
            return supportSelectAll;
        }

        public void setSupportSelectAll(Boolean supportSelectAll) {
            this.supportSelectAll = supportSelectAll;
            refreshItems();
        }

        public ValueChangedListener getValueChangedListener() {
            return valueChangedListener;
        }

        public void setValueChangedListener(ValueChangedListener valueChangedListener) {
            this.valueChangedListener = valueChangedListener;
        }

        private void initComponent() {
            this.setLayout(new BorderLayout());
            checkboxPane = new JPanel();
            checkboxPane.setLayout(new GridLayout(checkBoxList.size(), 2, 3, 3));
//            checkboxPane.setPreferredSize(new Dimension());

            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new GridLayout(1,3));
            GridConstraints gridConstraints = new GridConstraints();
            gridConstraints.setRow(0);
            gridConstraints.setColumn(0);

            checkAllBox = new JCheckBox("All");
            checkAllBox.addActionListener(this);
            commitButton = new JButton("Enter");
            commitButton.addActionListener(this);

            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);

            buttonPane.add(checkAllBox);
            gridConstraints.setColumn(1);
            buttonPane.add(commitButton);
            gridConstraints.setColumn(2);
            buttonPane.add(cancelButton);
            this.add(checkboxPane, BorderLayout.CENTER);
            this.add(buttonPane, BorderLayout.SOUTH);

        }
        private void refreshItems(){
            checkBoxList.forEach(e -> e.getParent().remove(e));
            nullChekBoxList.forEach(e -> e.getParent().remove(e));
            checkBoxList.clear();
            nullChekBoxList.clear();
            if (supportSelectAll){
//                JCheckBox box = new JCheckBox("Select All");
//                box.setName(String.valueOf(-1));
//                checkBoxList.add(box);
//                checkboxPane.add(box);
            }
            if (items == null){
                return;
            }
            GridLayout layout = (GridLayout)checkboxPane.getLayout();
            GridConstraints gridConstraints = new GridConstraints();
            for (int i = 0; i< items.size(); i++) {
                DBTableField v = items.get(i);
                JCheckBox box = new JCheckBox(v.getName());
                box.setName(v.getName());
                gridConstraints.setRow(i);
                gridConstraints.setColumn(0);
                checkBoxList.add(box);
                JCheckBox nullCheckBox = new JCheckBox("Not Null");
                nullCheckBox.setName(v.getName());
                nullChekBoxList.add(nullCheckBox);

                checkboxPane.add(box, gridConstraints);
                gridConstraints.setColumn(1);
                checkboxPane.add(nullCheckBox, gridConstraints);



                box.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {

                    }
                });

                nullCheckBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        JCheckBox b = (JCheckBox) e.getSource();
                        if (b.isSelected()){
                            int i = getItemIndexByValue(b.getName());
                            checkBoxList.get(i).setSelected(true);
                        }
                    }
                });

            }
        }
        public void selectAll(){
            for (int i = 0; i < checkBoxList.size(); i++) {
                if (!checkBoxList.get(i).isSelected()) {
                    checkBoxList.get(i).setSelected(true);
                }
            }
        }
        public void deselectAll(){

            for (int i = 0; i < checkBoxList.size(); i++) {
                if (checkBoxList.get(i).isSelected()) {
                    checkBoxList.get(i).setSelected(false);
                }
            }
        }

        public void setSelectValues(DBTableField[] values) {
            this.selectedValues.clear();
            this.selectedValues.addAll(Arrays.asList(values));
            refresh();
            if (values.length > 0) {
                setText(getSelectedValues());
            }
        }
        public void refresh(){
            for (int i = 0; i < checkBoxList.size(); i++) {
                final int j = i;
                Optional<DBTableField> r = selectedValues.stream().filter(e -> e.getName().equals(checkBoxList.get(j).getName())).findFirst();
                if (!r.isPresent()){
                    checkBoxList.get(i).setSelected(false);
                    continue;
                }
                checkBoxList.get(i).setSelected(true);
                Boolean allowNull = r.get().getNotNull();
                nullChekBoxList.get(i).setSelected(allowNull == null ? false : allowNull );
            }
        }


        public DBTableField[] getSelectedValues() {
            return selectedValues.toArray(DBTableField[]::new);
        }
        public Integer getItemIndexByValue(String val){
            for(int i = 0; i< items.size(); i++){
                if (items.get(i).getName().equals(val)){
                    return i;
                }
            }
            return null;
        }



        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            Object source = arg0.getSource();


            if (source == checkAllBox) {
                if (checkAllBox.isSelected()){
                    selectAll();
                }else{
                    deselectAll();
                }
            }
            if (source instanceof JButton) {
                JButton button = (JButton) source;
                if (button.equals(commitButton)) {

                    List<DBTableField> a = new ArrayList<DBTableField>();
                    for (int i = 0; i< checkBoxList.size(); i++){
                        JCheckBox checkBox = checkBoxList.get(i);
                        Integer index = getItemIndexByValue(checkBox.getName());
                        if (index != null && checkBox.isSelected()){
                            items.get(i).setNotNull(nullChekBoxList.get(i).isSelected());
                            a.add(items.get(i));
                        }
                    }
                    this.selectedValues.clear();
                    this.selectedValues.addAll(a);
                    setText(getSelectedValues());
                    popup.setVisible(false);
                    if (valueChangedListener != null){
                        valueChangedListener.onValueChanged(null);
                    }
                }
                if (button.equals(cancelButton)) {
                    popup.setVisible(false);
                }
            }
        }

    }
    public interface ValueChangedListener{
        public void onValueChanged(FieldSelectionButton btn);
    }
}
