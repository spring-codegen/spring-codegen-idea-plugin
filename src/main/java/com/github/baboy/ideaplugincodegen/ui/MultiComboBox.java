package com.github.baboy.ideaplugincodegen.ui;




import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
/**
 * @author zhangyinghui
 * @date 2023/8/3

 */
public class MultiComboBox extends JComponent implements SwingConstants, Accessible, ActionListener {
    protected boolean isEditable  = false;
    private Vector<String> items;
    private MultiPopup popup;
    private JTextField editor;
    protected JButton arrowButton;
    private ItemListener itemListener;
    private Boolean supportSelectAll = true;
    public MultiComboBox(){
        initComponent();
    }

    public MultiComboBox(Vector<String> items,Boolean supportSelectAll) {
        this();
        this.setItems(items);
        this.setSupportSelectAll(supportSelectAll);
    }

    private void initComponent() {
        this.setLayout(new BorderLayout());
        popup = new MultiPopup();
        popup.setItems(items);
        popup.setSupportSelectAll(supportSelectAll);
        editor = new JTextField();
        editor.setBackground(Color.WHITE);
        editor.setEditable(false);
        editor.setPreferredSize(new Dimension(140, 22));
        editor.addActionListener(this);
        arrowButton = createArrowButton();
        arrowButton.addActionListener(this);
        add(editor, BorderLayout.WEST);
        add(arrowButton, BorderLayout.CENTER);
    }

    public Vector<String> getItems() {
        return items;
    }

    public void setItems(Vector<String> items) {
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

    public String[] getSelectedValues() {
        return popup.getSelectedValues();
    }


    public void setSelectValues(String[] selectvalues) {
        popup.setSelectValues(selectvalues);
        setText(selectvalues);
    }

    private void setText(Object[] values) {
        if (values.length > 0) {
            String value = Arrays.toString(values);
            value = value.replace("[", "");
            value = value.replace("]", "");
            editor.setText(value);
        } else {
            editor.setText("");
        }
        editor.setToolTipText(editor.getText());
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
        JButton button = new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"),
                UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"),
                UIManager.getColor("ComboBox.buttonHighlight"));
        button.setName("ComboBox.arrowButton");
        return button;
    }



    //内部类MultiPopup

    public class MultiPopup extends JPopupMenu implements ActionListener {

        private Vector<String> items;
        private List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
        private JButton commitButton;
        private JButton cancelButton;
        private ItemListener itemListener;
        private Boolean supportSelectAll = true;
        private JPanel checkboxPane;
        private List<String> selectedValues = new ArrayList<>();

        public MultiPopup() {
            super();
            initComponent();
            refreshItems();
        }

        public Vector<String> getItems() {
            return items;
        }

        public void setItems(Vector<String> items) {
            this.items = items == null ? null : (Vector<String>) items.clone();
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
            checkboxPane = new JPanel();
            checkboxPane.setLayout(new GridLayout(checkBoxList.size(), 1, 3, 3));

            JPanel buttonPane = new JPanel();
            this.setLayout(new BorderLayout());

            commitButton = new JButton("Enter");
            commitButton.addActionListener(this);

            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);

            buttonPane.add(commitButton);
            buttonPane.add(cancelButton);
            this.add(checkboxPane, BorderLayout.CENTER);
            this.add(buttonPane, BorderLayout.SOUTH);

        }
        private void refreshItems(){
            for (JCheckBox box : checkBoxList){
                box.getParent().remove(box);
            }
            checkBoxList.clear();
            if (supportSelectAll){
                JCheckBox box = new JCheckBox("Select All");
                box.setName(String.valueOf(-1));
                checkBoxList.add(box);
                checkboxPane.add(box);
            }
            if (items == null){
                return;
            }
            for (int i = 0; i< items.size(); i++) {
                String v = items.get(i);
                JCheckBox box = new JCheckBox(v.toString());
                box.setName(String.valueOf(i ));
                checkBoxList.add(box);
                checkboxPane.add(box);

            }
            for (JCheckBox box : checkBoxList){
                box.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        JCheckBox checkBox = (JCheckBox) e.getSource();
                        int index = Integer.parseInt(checkBox.getName());
                        if (supportSelectAll && index == -1){
                            if (e.getStateChange() == ItemEvent.SELECTED){
                                selectAll();
                            }else{
                                deselectAll();
                            }
                        }
                        if (itemListener != null){
                            itemListener.itemStateChanged(e);
                        }
                    }
                });
            }
        }
        public void selectAll(){
            for (int i = 1; i < checkBoxList.size(); i++) {
                if (!checkBoxList.get(i).isSelected()) {
                    checkBoxList.get(i).setSelected(true);
                }
            }
        }
        public void deselectAll(){

            for (int i = 1; i < checkBoxList.size(); i++) {
                if (checkBoxList.get(i).isSelected()) {
                    checkBoxList.get(i).setSelected(false);
                }
            }
        }

        public void setSelectValues(String[] values) {
            this.selectedValues.clear();
            this.selectedValues.addAll(Arrays.asList(values));
            if (values.length > 0) {
                for (int i = 0; i < values.length; i++) {
                    for (int j = 0; j < checkBoxList.size(); j++) {
                        boolean selected = values[i].equals(checkBoxList.get(j).getText());
                        checkBoxList.get(j).setSelected(selected);
                    }
                }
                setText(getSelectedValues());
            }
        }
        public void refresh(){
            for (int i = 0; i < this.selectedValues.size(); i++) {
                for (int j = 0; j < checkBoxList.size(); j++) {
                    boolean selected = selectedValues.get(i).equals(checkBoxList.get(j).getText());
                    checkBoxList.get(j).setSelected(selected);
                }
            }
        }


        public String[] getSelectedValues() {
            return selectedValues.toArray(new String[selectedValues.size()]);
        }



        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            Object source = arg0.getSource();
            if (source instanceof JButton) {
                JButton button = (JButton) source;
                if (button.equals(commitButton)) {

                    List<String> a = new ArrayList<String>();
                    for (int i = 0; i< checkBoxList.size(); i++){
                        JCheckBox checkBox = checkBoxList.get(i);
                        int index = Integer.parseInt(checkBox.getName());
                        if (index >= 0 && checkBox.isSelected()){
                            a.add(items.get(i));
                        }
                    }
                    this.selectedValues.clear();
                    this.selectedValues.addAll(a);
                    setText(getSelectedValues());
                    popup.setVisible(false);
                } else if (button.equals(cancelButton)) {
                    popup.setVisible(false);
                }
            }
        }

    }
}
