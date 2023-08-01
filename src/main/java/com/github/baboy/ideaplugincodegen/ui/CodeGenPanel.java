package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.model.CtrlConfig;
import com.github.baboy.ideaplugincodegen.model.DBTable;
import com.github.baboy.ideaplugincodegen.model.DBTableField;
import com.github.baboy.ideaplugincodegen.model.DataSourceConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CodeGenPanel {
    private JTabbedPane tabbedPanel;
    private JPanel mainPanel;
    private JPanel codePanel;
    private JPanel sourcePanel;
    private JPanel settingPanel;
    private JTextField usernameTextField;
    private JTextField pwdTextField;
    private JTextField dbUrlTextField;
    private JButton testButton;
    private JComboBox tableComboBox;
    private JTextField ctrlDirTextField;
    private JTextField ctrlClsNameTextField;
    private JTextField baseUriTextField;
    private JTable ctrlTable;

    private String tableName;
    private CtrlConfig ctrlConfig;
    private DBTable dbTable;

    final String[] CTRL_TABLE_HEADERS = {"方法名", "Path", "Http Method", "请求类", "请求字段", "返回类", "返回字段"};
    final int CTRL_TABLE_INDEX_METHOD_NAME = 0;
    final int CTRL_TABLE_INDEX_PATH = 1;
    final int CTRL_TABLE_INDEX_HTTP_METHOD = 2;
    final int CTRL_TABLE_INDEX_DTO_CLS = 3;
    final int CTRL_TABLE_INDEX_DTO_FIELD = 4;
    final int CTRL_TABLE_INDEX_VO_CLS = 5;
    final int CTRL_TABLE_INDEX_VO_FIELD = 6;
    public CodeGenPanel() {
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                test();
            }
        });
        init();

        tableComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // 选择的下拉框选项
                    System.out.println(e.getItem());
                    tableUpdated();
                }
            }
        });
    }

    /**
     *
     */
    private void init(){
        dbTable = new DBTable();

        ctrlConfig = new CtrlConfig();
        List tables = new ArrayList();
        tables.add("t_table1");
        tables.add("t_tables2");
        setDbTableItems(tables);


        String[] ctrlMethods = new String[]{"add", "remove", "update", "get", "search"};
        List<CtrlConfig.CtrlMethod> methods = new ArrayList<>();
        for (String m: ctrlMethods){
            CtrlConfig.CtrlMethod method = new CtrlConfig.CtrlMethod();
            method.setName(m);
            method.setPath(String.format("/%s", m));
            method.setRequestMethod("POST");
            methods.add(method);
        }
        ctrlConfig.setMethods(methods);
    }
    private void setDbTableItems(List<String> items){
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        // elements 下拉框中的选项
        for (String element : items) {
            comboBoxModel.addElement(element);
        }
        tableComboBox.setModel(comboBoxModel);
    }
    private void ctrlDirUpdated(){
        ctrlConfig.setBaseURI(String.format("/api/v1/%s", ctrlConfig.getDir()));
        baseUriTextField.setText(ctrlConfig.getBaseURI());
    }

    /**
     * db table updated
     */
    private void tableUpdated(){
        String tableName = tableComboBox.getSelectedItem().toString();
        String tableSymbol = tableName;
        ctrlConfig.setClsName(String.format("%sController", tableSymbol));

        List<DBTableField> fields = new ArrayList<>();
        for (int i = 0; i< 5; i++ ){
            DBTableField field = new DBTableField();
            field.setName("field"+i);
            field.setComment("comment"+i);
            field.setType(new String[]{"int","string","boolean","long","string"}[i]);
            fields.add(field);
        }
        dbTable.setFields(fields);
        updateCtrlMethodTable();
    }

    /**
     *
     var name: String? = null
     var path: String? = null
     var requestMethod: String? = null
     var dtoClsName: String? = null
     var dtoFields: List<String>? = null
     var voClassName:String? = null
     var voFields: List<String>? = null
     */
    private void updateCtrlMethodTable(){
        DefaultTableModel tableModel = new DefaultTableModel();
        String[] headers = CTRL_TABLE_HEADERS;
        String[][] data = new String[ctrlConfig.getMethods().size()][headers.length];
        for (int i = 0; i < data.length; i++) {
            CtrlConfig.CtrlMethod method = ctrlConfig.getMethods().get(i);
            for (int j = 0; j < headers.length; j++) {
                data[i][j] = i+","+j;
                switch (j){
                    case CTRL_TABLE_INDEX_METHOD_NAME:{
                        data[i][j] = method.getName();
                        break;
                    }
                    case CTRL_TABLE_INDEX_PATH:{
                        data[i][j] = method.getPath();
                        break;
                    }
                    case CTRL_TABLE_INDEX_HTTP_METHOD:{
                        data[i][j] = method.getRequestMethod();
                        break;
                    }
                    case CTRL_TABLE_INDEX_DTO_CLS:{
                        data[i][j] = method.getDtoClsName();
                        break;
                    }
                    case CTRL_TABLE_INDEX_DTO_FIELD:{
                        break;
                    }
                    case CTRL_TABLE_INDEX_VO_CLS:{
                        data[i][j] = method.getVoClassName();
                        break;
                    }
                    case CTRL_TABLE_INDEX_VO_FIELD:{
                        break;
                    }
                }
            }
        }
        tableModel.setDataVector(data, headers);
        ctrlTable.setModel(tableModel);

        for (int i = 0; i < data.length; i++) {
            Vector<String> vector = new Vector<String>();
            for (int j = 0; j < dbTable.getFields().size(); j++){
                DBTableField field = dbTable.getFields().get(j);
                vector.add(field.getName());
            }
            final JComboBox<String> comboBox = new JComboBox<String>(vector);
            //下拉框监听
            comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        System.out.println(comboBox.getSelectedItem());
                    }
                }
            });
            //表格编辑器
            ctrlTable.getColumnModel().getColumn(CTRL_TABLE_INDEX_VO_FIELD).setCellEditor(new DefaultCellEditor(comboBox));
        }

    }
    private DataSourceConfig getDataSourceConfig(){
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(dbUrlTextField.getText());
        dataSourceConfig.setUsername(usernameTextField.getText());
        dataSourceConfig.setPwd(pwdTextField.getText());
        return dataSourceConfig;
    }
    private void test(){
        DataSourceConfig dataSourceConfig = getDataSourceConfig();
        System.out.println("url:"+ dataSourceConfig.getUrl());
        System.out.println("username:"+ dataSourceConfig.getUsername());
        System.out.println("password:"+ dataSourceConfig.getPwd());
    }

    public JPanel getContent()  {
        return this.mainPanel;
    }

}
