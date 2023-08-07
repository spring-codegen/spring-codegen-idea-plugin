package com.github.baboy.ideaplugincodegen.ui;

import com.github.baboy.ideaplugincodegen.config.CodeCfg;
import com.github.baboy.ideaplugincodegen.config.CodeCfgModel;
import com.github.baboy.ideaplugincodegen.db.DBContext;
import com.github.baboy.ideaplugincodegen.db.model.DBTable;
import com.github.baboy.ideaplugincodegen.db.model.DBTableField;
import com.github.baboy.ideaplugincodegen.gen.FieldUtils;
import com.github.baboy.ideaplugincodegen.setting.DataSourceSetting;
import com.github.baboy.ideaplugincodegen.services.ResourceService;
import com.intellij.uiDesigner.core.GridConstraints;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

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
    private JTextField svcClassNameTextField;
    private JTextField daoClassNameTextField;
    private JTable ctrlTable;

    private String tableName;
    private DBTable dbTable;
    private List<DBTable> dbTables;

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
        Map p = new HashMap();
        p.put("schema", "computility_gateway");
        dbTables = DBContext.INSTANCE.queryTables(p);
        setDbTableItems(dbTables.stream().map(e->e.getName()).toList());
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
        tableComboBox.setSelectedItem(null);
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

        var p = new HashMap <String, String>();
        p.put("tableName", tableName);
        List<DBTableField> fields = DBContext.INSTANCE.queryFields( p);
        dbTable.setFields(fields);
        updateMethodUI();
    }
    private String getHandledVar(String v, Map<String, String> p){
        if (v == null){
            return v;
        }
        String r = v;
        for (String k : p.keySet()){
            r = r.replaceAll("\\{\\s*"+k+"\\s*\\}",  p.get(k));
        }
        return r;
    }
    private List<String> handleDefaultFields(String excludes, String includes){
        List<String> allowFields = new ArrayList<>();
        dbTable.getFields().forEach(field -> {
            if (StringUtils.isNotEmpty(excludes)){
                boolean isExclude = Arrays.stream(excludes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
                if (isExclude){
                    return;
                }
            }

            if (StringUtils.isNotEmpty(includes)){
                boolean isInclude = Arrays.stream(includes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
                if (isInclude){
                    allowFields.add(field.getName());
                    return;
                }
                return;
            }
            allowFields.add(field.getName());
        });
        return allowFields;
    }
    private void updateMethodUI(){
        String tableName = (String)tableComboBox.getSelectedItem();
        String TAB_PREFIX = "t_";
        String clsPrefix = tableName;
        if (clsPrefix.startsWith(TAB_PREFIX)){
            clsPrefix = FieldUtils.INSTANCE.className(clsPrefix.substring(TAB_PREFIX.length()));
        }

        codeCfgPanel.removeAll();
        GridLayout layout = new GridLayout(codeCfg.getMethods().size(), 1);
        codeCfgPanel.setLayout(layout);

        GridConstraints gridConstraints = new GridConstraints();
        List<String> tableFields = new ArrayList<>();
        for (int i = 0; i< dbTable.getFields().size(); i++){
            tableFields.add(dbTable.getFields().get(i).getName());
        }
        Map p = new HashMap();
        p.put("CLS_PREFIX", clsPrefix);
        baseUriTextField.setText(codeCfg.getCtrlClass().getBaseURI());
        ctrlClsNameTextField.setText(getHandledVar(codeCfg.getCtrlClass().getClassName(), p));
        svcClassNameTextField.setText(getHandledVar(codeCfg.getSvcClass().getClassName(),  p));
        daoClassNameTextField.setText(getHandledVar(codeCfg.getDaoClass().getClassName(), p));
        for (int i = 0; i< codeCfg.getMethods().size(); i++){
            CodeCfg.Method method = codeCfg.getMethods().get(i);
            CodeCfgModel model = new CodeCfgModel();
            model.setCtrl(new CodeCfgModel.CtrlModel());
            model.setSvc(new CodeCfgModel.SvcModel());
            model.setDao(new CodeCfgModel.DaoModel());

            model.getCtrl().setFields(tableFields);
            model.getSvc().setFields(tableFields);
            model.getDao().setFields(tableFields);

            gridConstraints.myPreferredSize.height = 500;
            gridConstraints.setRow(i);

            try {

                BeanUtils.copyProperties(model.getCtrl(), method.getCtrl() == null ? new CodeCfgModel.CtrlModel(): method.getCtrl());
                BeanUtils.copyProperties(model.getSvc(), method.getSvc() == null ? new CodeCfgModel.SvcModel() : method.getSvc());
                BeanUtils.copyProperties(model.getDao(), method.getDao() == null ? new CodeCfgModel.DaoModel() : method.getDao());
                model.getCtrl().setDtoClassName(getHandledVar(model.getCtrl().getDtoClassName(), p));
                model.getCtrl().setVoClassName(getHandledVar(model.getCtrl().getVoClassName(), p));
                model.getSvc().setBoClassName(getHandledVar(model.getSvc().getBoClassName(), p));
                model.getSvc().setBoResultClassName(getHandledVar(model.getSvc().getBoResultClassName(), p));
                model.getDao().setPoClassName(getHandledVar(model.getDao().getPoClassName(), p));
                model.getCtrl().setDtoFields(handleDefaultFields(model.getCtrl().getDtoFieldExcludes(), model.getCtrl().getDtoFieldIncludes()));
                model.getCtrl().setVoFields(handleDefaultFields(model.getCtrl().getVoFieldExcludes(), model.getCtrl().getVoFieldIncludes()));

                model.getSvc().setBoFields(handleDefaultFields(model.getSvc().getBoFieldExcludes(), model.getSvc().getBoFieldIncludes()));

                model.getSvc().setBoResultFields(handleDefaultFields(model.getSvc().getBoResultFieldExcludes(), model.getSvc().getBoResultFieldIncludes()));
                WorkflowItemCodePanel itemCodePanel = new WorkflowItemCodePanel();
                itemCodePanel.init();
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
