package com.github.baboy.ideaplugincodegen.ui;


import com.intellij.uiDesigner.core.GridConstraints;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangyinghui
 * @date 2023/8/3

 */
public class FieldSelectionButton extends JPanel implements Accessible, ActionListener {
    protected boolean isEditable  = false;
    private List<Model> items;
    private MultiPopup popup;
    protected JButton arrowButton;
    private ItemListener itemListener;
    private Boolean supportSelectAll = true;
    public FieldSelectionButton(){
        initComponent();
    }

    public FieldSelectionButton(List<Model> items, Boolean supportSelectAll) {
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

    public List<Model> getItems() {
        return items;
    }

    public void setItems(List<Model> items) {
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
    public ItemListener getItemListener() {
        return itemListener;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
        popup.setItemListener(itemListener);
    }

    public Model[] getSelectedValues() {
        return popup.getSelectedValues();
    }


    public void setSelectValues(Model[] selectvalues) {
        popup.setSelectValues(selectvalues);
        setText(selectvalues);
    }

    private void setText(Model[] values) {
        if (values.length > 0) {
            String[] s = Arrays.stream(values).map(e -> e.isNotNull? (e.getValue()+"!") : e.getValue()).toArray(String[]::new);
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

        private List<Model> items;
        private List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
        private List<JCheckBox> nullChekBoxList = new ArrayList<JCheckBox>();
        private JButton commitButton;
        private JButton cancelButton;
        private ItemListener itemListener;
        private Boolean supportSelectAll = true;
        private JPanel checkboxPane;
        private JCheckBox checkAllBox;
        private List<Model> selectedValues = new ArrayList<>();

        public MultiPopup() {
            super();
            initComponent();
            refreshItems();
        }

        public List<Model> getItems() {
            return items;
        }

        public void setItems(List<Model> items) {
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

        public ItemListener getItemListener() {
            return itemListener;
        }
        public void setItemListener(ItemListener itemListener) {
            this.itemListener = itemListener;
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
                Model v = items.get(i);
                JCheckBox box = new JCheckBox(v.getValue());
                box.setName(v.getValue());
                gridConstraints.setRow(i);
                gridConstraints.setColumn(0);
                checkBoxList.add(box);
                JCheckBox nullCheckBox = new JCheckBox("Not Null");
                nullCheckBox.setName(v.getValue());
                nullChekBoxList.add(nullCheckBox);

                checkboxPane.add(box, gridConstraints);
                gridConstraints.setColumn(1);
                checkboxPane.add(nullCheckBox, gridConstraints);



                box.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (itemListener != null){
                            itemListener.itemStateChanged(e);
                        }
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

        public void setSelectValues(Model[] values) {
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
                Optional<Model> r = selectedValues.stream().filter(e -> e.getValue().equals(checkBoxList.get(j).getName())).findFirst();
                if (!r.isPresent()){
                    checkBoxList.get(i).setSelected(false);
                    continue;
                }
                checkBoxList.get(i).setSelected(true);
                Boolean allowNull = r.get().getNotNull();
                nullChekBoxList.get(i).setSelected(allowNull == null ? false : allowNull );
            }
        }


        public Model[] getSelectedValues() {
            return selectedValues.toArray(FieldSelectionButton.Model[]::new);
        }
        public Integer getItemIndexByValue(String val){
            for(int i = 0; i< items.size(); i++){
                if (items.get(i).getValue().equals(val)){
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

                    List<Model> a = new ArrayList<Model>();
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
                }
                if (button.equals(cancelButton)) {
                    popup.setVisible(false);
                }
            }
        }

    }
    public static class Model{
        private String title;
        private String value;
        private Boolean isNotNull = false;

        public String getTitle() {
            return title;
        }

        public Model setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getValue() {
            return value;
        }

        public Model setValue(String value) {
            this.value = value;
            return this;
        }

        public Boolean getNotNull() {
            return isNotNull;
        }

        public Model setNotNull(Boolean notNull) {
            isNotNull = notNull;
            return this;
        }
    }
}
