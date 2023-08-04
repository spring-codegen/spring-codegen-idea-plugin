package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CodeCfg;
import com.github.baboy.ideaplugincodegen.config.CodeCfgModel;
import com.github.baboy.ideaplugincodegen.setting.CtrlSetting;
import com.github.baboy.ideaplugincodegen.db.DBContext;
import com.github.baboy.ideaplugincodegen.db.model.DBTable;
import com.github.baboy.ideaplugincodegen.db.model.DBTableField;
import com.github.baboy.ideaplugincodegen.setting.DataSourceSetting;
import com.github.baboy.ideaplugincodegen.services.ResourceService;
import com.github.baboy.ideaplugincodegen.util.ClassUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.commons.beanutils.BeanUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
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
    private JScrollPane methodScrollPanel;
    private JPanel codeCfgPanel;
    private JTable ctrlTable;

    private String tableName;
    private DBTable dbTable;

    private CodeCfg codeCfg;
    private CodeCfg codeSetting = new CodeCfg();
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
                    ResourceService.INSTANCE.readYaml("code-cfg.yaml");
                }
            }
        });
    }

    /**
     *
     */
    private void init(){
        dbTable = new DBTable();

        List tables = new ArrayList();
        tables.add("t_table1");
        tables.add("t_api");
        setDbTableItems(tables);

        codeCfg = ResourceService.INSTANCE.getCodeCfg();
        List<CodeCfg.Method> methods = new ArrayList<>();
        for (int i = 0; i< codeCfg.getMethods().size(); i++){
            CodeCfg.Method methodCfg = codeCfg.getMethods().get(i);
            CodeCfg.Method method = new CodeCfg.Method();
            method.setCtrl(new CodeCfgModel.CtrlModel());
            method.setSvc(new CodeCfgModel.SvcModel());
            method.setDao(new CodeCfgModel.DaoModel());
            try {
                BeanUtils.copyProperties(method, methodCfg);
                methods.add(method);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            methods.add(method);
        }
        codeSetting.setCtrlClass(codeCfg.getCtrlClass());
        codeSetting.setSvcClass(codeCfg.getSvcClass());
        codeSetting.setDaoClass(codeCfg.getDaoClass());
        codeSetting.setMethods(methods);
    }
    private void setDbTableItems(List<String> items){
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

        for (String element : items) {
            comboBoxModel.addElement(element);
        }
        tableComboBox.setModel(comboBoxModel);
    }
    private void ctrlDirUpdated(){
        codeSetting.getCtrlClass().setBaseURI(String.format("/api/v1/%s", codeSetting.getCtrlClass().getDir()));
        baseUriTextField.setText(codeSetting.getCtrlClass().getBaseURI());
    }

    /**
     * db table updated
     */
    private void tableUpdated(){
        String tableName = tableComboBox.getSelectedItem().toString();
        String tableSymbol = tableName;
//        codeSetting.setClsName(String.format("%sController", tableSymbol));

        var p = new HashMap <String, String>();
        p.put("tableName", tableName);
        DBContext.INSTANCE.refresh();
        List<DBTableField> fields = DBContext.INSTANCE.queryFields( p);
        dbTable.setFields(fields);
        updateMethodUI();
    }

    private MultiComboBox createFieldComboBox(Vector<String> items){

        final MultiComboBox multiComboBox = new MultiComboBox(items, true);
        multiComboBox.setItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                int index = Integer.parseInt(checkBox.getName());
                System.out.println("index:" + index);
            }
        });
        return multiComboBox;
    }
    private void updateMethodUI(){
        GridLayout layout = new GridLayout(codeCfg.getMethods().size(), 1);
        codeCfgPanel.setLayout(layout);
        GridConstraints gridConstraints = new GridConstraints();
        for (int i = 0; i< codeCfg.getMethods().size(); i++){
            CodeCfg.Method method = codeCfg.getMethods().get(i);
            CodeCfgModel model = new CodeCfgModel();
            model.setCtrl(new CodeCfgModel.CtrlModel());
            model.setSvc(new CodeCfgModel.SvcModel());
            model.setDao(new CodeCfgModel.DaoModel());
            gridConstraints.myPreferredSize.height = 500;
            gridConstraints.setRow(i);

            try {
                BeanUtils.copyProperties(model.getCtrl(), method.getCtrl());
                BeanUtils.copyProperties(model.getSvc(), method.getSvc());
                BeanUtils.copyProperties(model.getDao(), method.getDao());
                WorkflowItemCodePanel itemCodePanel = new WorkflowItemCodePanel();
                itemCodePanel.setModel(model);
                codeCfgPanel.add(itemCodePanel.getContent(), gridConstraints);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
//
//        DefaultTableModel tableModel = new DefaultTableModel();
//        String[][] data = new String[ctrlSetting.getMethods().size()][codeConfig.getRenderItems().size()];
//        String[] headers = new String[codeConfig.getRenderItems().size()];
//        List<Integer> fieldIndexs = new ArrayList<>();
//        for (int i = 0; i < data.length; i++) {
//            CtrlSetting.CtrlMethod method = ctrlSetting.getMethods().get(i);
//            for (int j = 0; j < codeConfig.getRenderItems().size(); j++) {
//                CodeCfg.RenderItem renderItem = codeConfig.getRenderItems().get(j);
//                data[i][j] = String.valueOf( ClassUtils.INSTANCE.fieldValue(method, renderItem.getField()) );
//                if (i == 0){
//                    headers[j] = renderItem.getTitle();
//                    if (renderItem.getField().endsWith("Fields")){
//                        fieldIndexs.add(j);
//                    }
//                }
//            }
//        }
//        tableModel.setDataVector(data, headers);
//        ctrlTable.setModel(tableModel);
//
//        Vector<String> fieldItems = new Vector<String>();
//        for (int j = 0; j < dbTable.getFields().size(); j++){
//            DBTableField field = dbTable.getFields().get(j);
//            fieldItems.add(field.getName());
//        }
//        for(Integer i : fieldIndexs){
//            final MultiComboBox dtoMultiComboBox = createFieldComboBox(fieldItems);
//            ctrlTable.getColumnModel().getColumn(i).setCellEditor(new MultiComboBoxCellEditor(dtoMultiComboBox));
//        }
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
