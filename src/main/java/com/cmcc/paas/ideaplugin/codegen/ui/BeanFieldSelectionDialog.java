package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BeanFieldSelectionDialog extends JDialog {
    private Object userInfo;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable tablePanel;
    private JTextField classNameTextField;

    private String[] tableHeaders = new String[]{"Include", "Column","Name","Type","Not Null", "Min Length", "Max Length",  "Comment"};

    public enum TableHeaderIndex{
        INCLUDE,
        COLUMN_NAME,
        NAME,
        TYPE,
        NOT_NULL,
        MIN_LEN,
        MAX_LEN,
        COMMENT;
    }
    private List<DBTableField> fields;
    private BeanFieldSelectionActionListener actionListener;

    public Object getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(Object userInfo) {
        this.userInfo = userInfo;
    }

    public List<DBTableField> getFields() {
        return fields;
    }

    public void setFields(List<DBTableField> fields) {
        this.fields = fields;
        this.refresh();
    }

    public BeanFieldSelectionActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(BeanFieldSelectionActionListener actionListener) {
        this.actionListener = actionListener;
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
        if (actionListener != null){
            actionListener.onFieldSelected(this);
        }
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
            data[i][TableHeaderIndex.INCLUDE.ordinal()] = false;
            data[i][TableHeaderIndex.TYPE.ordinal()] = FieldUtils.INSTANCE.javaType(field.getType());
//            data[i][TableHeaderIndex.TYPE.ordinal()] = field.getType();
            if (field.getMaxLen() != null && field.getMaxLen() > 4) {
                data[i][TableHeaderIndex.MAX_LEN.ordinal()] = field.getMaxLen() - 4;
            }
            data[i][TableHeaderIndex.COMMENT.ordinal()] = field.getComment();
            data[i][TableHeaderIndex.NAME.ordinal()] = FieldUtils.INSTANCE.propertyName(field.getName());
            data[i][TableHeaderIndex.NOT_NULL.ordinal()] = field.getNotNull();
            data[i][TableHeaderIndex.COLUMN_NAME.ordinal()] = field.getName();
        }
        tableModel.setDataVector(data, tableHeaders);
        this.tablePanel.setModel(tableModel);
        this.tablePanel.getTableHeader().setDefaultRenderer(new TableHeaderCheckBoxRender(this.tablePanel));

        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.INCLUDE.ordinal()).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.INCLUDE.ordinal()).setCellRenderer(new TableCellFieldRender());

        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.NOT_NULL.ordinal()).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.NOT_NULL.ordinal()).setCellRenderer(new TableCellFieldRender());
    }
    public void setClassName(String className){
        this.classNameTextField.setText(className);
    }
    public String getClassName(){
        return this.classNameTextField.getText();
    }
    public void setSelectedFields(List<ClassModel.Field> fields){
        for (int i = 0 ; i< this.tablePanel.getModel().getRowCount(); i++){
            String columnName = (String)tablePanel.getModel().getValueAt(i, TableHeaderIndex.COLUMN_NAME.ordinal());
            for (ClassModel.Field f  : fields){
                if (columnName.equalsIgnoreCase( f.getColumn())){
                    tablePanel.getModel().setValueAt(true, i, TableHeaderIndex.INCLUDE.ordinal()) ;
                    tablePanel.getModel().setValueAt( f.getJavaType(), i, TableHeaderIndex.TYPE.ordinal());
                    tablePanel.getModel().setValueAt( f.getComment(), i, TableHeaderIndex.COMMENT.ordinal()) ;
                    tablePanel.getModel().setValueAt( f.getName(), i, TableHeaderIndex.NAME.ordinal());
                    tablePanel.getModel().setValueAt( f.getNotNull() , i, TableHeaderIndex.NOT_NULL.ordinal());
                }
            }
        }
    }
    public List<ClassModel.Field> getSelectedFields(){
        List<ClassModel.Field> result = new ArrayList();
        ( (DefaultTableModel) tablePanel.getModel()).getDataVector();
        for (int i = 0 ; i< this.tablePanel.getModel().getRowCount(); i++){
            Boolean include = (Boolean) tablePanel.getModel().getValueAt(i, TableHeaderIndex.INCLUDE.ordinal());
            if (!include){
                continue;
            }
            ClassModel.Field fieldDefine = new ClassModel.Field((String)tablePanel.getModel().getValueAt(i, TableHeaderIndex.NAME.ordinal()),
                    (String) tablePanel.getModel().getValueAt(i, TableHeaderIndex.TYPE.ordinal()),
                    (String) tablePanel.getModel().getValueAt(i, TableHeaderIndex.COMMENT.ordinal()),
                    (Boolean) tablePanel.getModel().getValueAt(i, TableHeaderIndex.NOT_NULL.ordinal()), null, null);
            Object v = tablePanel.getModel().getValueAt(i, TableHeaderIndex.MIN_LEN.ordinal());
            fieldDefine.setMinLen( v == null ? -1 : Integer.parseInt(v.toString()) );
            v = tablePanel.getModel().getValueAt(i, TableHeaderIndex.MAX_LEN.ordinal());
            fieldDefine.setMaxLen(v == null ?  -1 : Integer.parseInt(v.toString()));
            fieldDefine.setColumn((String) tablePanel.getModel().getValueAt(i, TableHeaderIndex.COLUMN_NAME.ordinal()));
            result.add(fieldDefine);
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
