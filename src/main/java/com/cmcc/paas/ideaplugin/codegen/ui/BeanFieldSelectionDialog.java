package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BeanFieldSelectionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable tablePanel;

    private String[] tableHeaders = new String[]{"Include","Name","Type","Not Null", "Min Length", "Max Length",  "Comment"};

    public enum TableHeaderIndex{
        INCLUDE,
        NAME,
        TYPE,
        NOT_NULL,
        MIN_LEN,
        MAX_LEN,
        COMMENT;
    }
    private List<DBTableField> fields;

    public List<DBTableField> getFields() {
        return fields;
    }

    public void setFields(List<DBTableField> fields) {
        this.fields = fields;
        this.refresh();
    }

    public BeanFieldSelectionDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
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
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
    public void refresh(){
        DefaultTableModel tableModel = new DefaultTableModel();
        Object[][] data = new Object[fields.size()][tableHeaders.length];
        for (int i = 0; i < data.length; i++) {
            DBTableField field = fields.get(i);
            data[i][TableHeaderIndex.INCLUDE.ordinal()] = true;
            data[i][TableHeaderIndex.TYPE.ordinal()] = FieldUtils.INSTANCE.javaType(field.getType());
            if (field.getMaxLen() != null && field.getMaxLen() > 4) {
                data[i][TableHeaderIndex.MAX_LEN.ordinal()] = field.getMaxLen() - 4;
            }
            data[i][TableHeaderIndex.COMMENT.ordinal()] = field.getComment();
            data[i][TableHeaderIndex.NAME.ordinal()] = FieldUtils.INSTANCE.propertyName(field.getName());
            data[i][TableHeaderIndex.NOT_NULL.ordinal()] = field.getNotNull();
        }
        tableModel.setDataVector(data, tableHeaders);
        this.tablePanel.setModel(tableModel);
        this.tablePanel.getTableHeader().setDefaultRenderer(new TableHeaderCheckBoxRender(this.tablePanel));

        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.INCLUDE.ordinal()).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.INCLUDE.ordinal()).setCellRenderer(new TableCellFieldRender());

        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.NOT_NULL.ordinal()).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.NOT_NULL.ordinal()).setCellRenderer(new TableCellFieldRender());
    }
    public List<ClassModel.Field> getSelectedFields(){
        List<ClassModel.Field> result = new ArrayList();
        ( (DefaultTableModel) tablePanel.getModel()).getDataVector();
        for (int i = 0 ; i< this.tablePanel.getModel().getRowCount(); i++){
            ClassModel.Field fieldDefine = new ClassModel.Field((String)tablePanel.getModel().getValueAt(i, TableHeaderIndex.NAME.ordinal()),
                    (String) tablePanel.getModel().getValueAt(i, TableHeaderIndex.TYPE.ordinal()),
                    (String) tablePanel.getModel().getValueAt(i, TableHeaderIndex.COMMENT.ordinal()),
                    (Boolean) tablePanel.getModel().getValueAt(i, TableHeaderIndex.NOT_NULL.ordinal()), null, null);
            fieldDefine.setMinLen((Integer) tablePanel.getModel().getValueAt(i, TableHeaderIndex.MIN_LEN.ordinal()));
            fieldDefine.setMaxLen((Integer) tablePanel.getModel().getValueAt(i, TableHeaderIndex.MAX_LEN.ordinal()));
        }
        return result;
    }

    public static BeanFieldSelectionDialog create() {
        BeanFieldSelectionDialog dialog = new BeanFieldSelectionDialog();
        dialog.pack();
        return dialog;
    }

    public static class TableHeaderCheckBoxRender implements TableCellRenderer{
        JTable table;
        JCheckBox checkBox;

        public TableHeaderCheckBoxRender(JTable table) {
            this.table = table;

            checkBox = new JCheckBox();
            checkBox.setSelected(false);

            table.getTableHeader().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectColumn = table.getTableHeader().columnAtPoint(e.getPoint());
                    if (selectColumn == TableHeaderIndex.NOT_NULL.ordinal()) {
                        boolean value = !checkBox.isSelected();
                        checkBox.setSelected(value);
                        for (int i = 0; i < table.getRowCount(); i++) {
                            table.getModel().setValueAt(value, i, selectColumn);
                        }
                        table.getTableHeader().repaint();
                    }
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            System.out.println("=================="+row+","+column);
            String valueStr = (String) value;
            JLabel label = new JLabel(valueStr);
            JComponent component = (column == TableHeaderIndex.NOT_NULL.ordinal()) ? checkBox : label;
            return component;
        }
    }
    public static class TableCellFieldRender extends JCheckBox implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (column == TableHeaderIndex.NOT_NULL.ordinal()){

            }
            System.out.println("value: "+ value) ;
            if (value instanceof Boolean){
                setSelected(((Boolean) value).booleanValue());
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            return this;
        }
    }
    public interface BeanFieldSelectionActionListener{
        void onFieldSelected(BeanFieldSelectionDialog dialog);
    }
}