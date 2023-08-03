package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CtrlConfig;
import com.github.baboy.ideaplugincodegen.config.CtrlSetting;
import com.github.baboy.ideaplugincodegen.db.DBContext;
import com.github.baboy.ideaplugincodegen.model.DBTable;
import com.github.baboy.ideaplugincodegen.db.model.DBTableField;
import com.github.baboy.ideaplugincodegen.config.DataSourceSetting;
import com.github.baboy.ideaplugincodegen.services.ResourceService;
import org.apache.commons.beanutils.BeanUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
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
    private CtrlSetting ctrlSetting;
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
                    ResourceService.INSTANCE.readYaml("ctrl.yaml");
                }
            }
        });
    }

    /**
     *
     */
    private void init(){
        dbTable = new DBTable();

        ctrlSetting = new CtrlSetting();
        List tables = new ArrayList();
        tables.add("t_table1");
        tables.add("t_api");
        setDbTableItems(tables);

        CtrlConfig

        String[] ctrlMethods = new String[]{"add", "remove", "update", "get", "search"};
        List<CtrlSetting.CtrlMethod> methods = new ArrayList<>();
        for (String m: ctrlMethods){
            CtrlSetting.CtrlMethod method = new CtrlSetting.CtrlMethod();
            method.setName(m);
            method.setPath(String.format("/%s", m));
            method.setRequestMethod("POST");
            methods.add(method);
        }
        ctrlSetting.setMethods(methods);
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
        ctrlSetting.setBaseURI(String.format("/api/v1/%s", ctrlSetting.getDir()));
        baseUriTextField.setText(ctrlSetting.getBaseURI());
    }

    /**
     * db table updated
     */
    private void tableUpdated(){
        String tableName = tableComboBox.getSelectedItem().toString();
        String tableSymbol = tableName;
        ctrlSetting.setClsName(String.format("%sController", tableSymbol));

        var p = new HashMap <String, String>();
        p.put("tableName", tableName);
        DBContext.INSTANCE.refresh();
        List<DBTableField> fields = DBContext.INSTANCE.queryFields( p);
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
        String[][] data = new String[ctrlSetting.getMethods().size()][headers.length];
        for (int i = 0; i < data.length; i++) {
            CtrlSetting.CtrlMethod method = ctrlSetting.getMethods().get(i);
            for (int j = 0; j < headers.length; j++) {
                data[i][j] = "id";
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
            final MultiComboBox multiComboBox = new MultiComboBox(vector, true);
            //下拉框监听
            multiComboBox.setItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    int index = Integer.parseInt(checkBox.getName());
                    System.out.println("index:" + index);
                }
            });
            //表格编辑器
            ctrlTable.getColumnModel().getColumn(CTRL_TABLE_INDEX_VO_FIELD).setCellEditor(new MultiComboBoxCellEditor(multiComboBox));
        }

    }
    private DataSourceSetting getDataSourceConfig(){
        DataSourceSetting dataSourceSetting = new DataSourceSetting();
        dataSourceSetting.setUrl(dbUrlTextField.getText());
        dataSourceSetting.setUsername(usernameTextField.getText());
        dataSourceSetting.setPwd(pwdTextField.getText());
        return dataSourceSetting;
    }
    private void test(){
        DataSourceSetting dataSourceSetting = getDataSourceConfig();
        System.out.println("url:"+ dataSourceSetting.getUrl());
        System.out.println("username:"+ dataSourceSetting.getUsername());
        System.out.println("password:"+ dataSourceSetting.getPwd());
    }

    public JPanel getContent()  {
        return this.mainPanel;
    }

}
