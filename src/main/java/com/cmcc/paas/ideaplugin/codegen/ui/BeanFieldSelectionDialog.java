package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.reflection.ArrayUtil;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BeanFieldSelectionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable tablePanel;

    private String[] tableHeaders = new String[]{"Name","Type","Not Null", "Min Length", "Max Length",  "Comment"};

    public enum TableHeaderIndex{
        NAME,
        TYPE,
        NOT_NULL,
        MIN_LEN,
        MAX_LEN,
        COMMENT;
    }
    private List<CodeCfg.FieldCfg> fields;

    public List<CodeCfg.FieldCfg> getFields() {
        return fields;
    }

    public void setFields(List<CodeCfg.FieldCfg> fields) {
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
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
    public void refresh(){
        DefaultTableModel tableModel = new DefaultTableModel();
        Object[][] data = new String[fields.size()][tableHeaders.length];
        for (int i = 0; i < data.length; i++) {
            CodeCfg.FieldCfg fieldCfg = fields.get(i);
            data[i][TableHeaderIndex.NAME.ordinal()] = fieldCfg.getName();
            data[i][TableHeaderIndex.NOT_NULL.ordinal()] = "true";
        }
        tableModel.setDataVector(data, tableHeaders);
        this.tablePanel.setModel(tableModel);
        this.tablePanel.getTableHeader().setDefaultRenderer(new TableHeaderCheckBoxRender(this.tablePanel));
        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.NOT_NULL.ordinal()).setCellEditor(new DefaultCellEditor(new JCheckBox("x")));
//        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.MIN_LEN.ordinal()).setCellEditor(new DefaultCellEditor(new JTextField()));
//        this.tablePanel.getColumnModel().getColumn(TableHeaderIndex.MIN_LEN.ordinal()).setCellEditor(new DefaultCellEditor(new JTextField()));
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
}
