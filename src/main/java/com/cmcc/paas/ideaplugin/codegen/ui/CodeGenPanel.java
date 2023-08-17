package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.*;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.constants.EnvKey;
import com.cmcc.paas.ideaplugin.codegen.db.DBCtx;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.CodeGenerator;
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils;
import com.cmcc.paas.ideaplugin.codegen.setting.DataSourceSetting;
import com.cmcc.paas.ideaplugin.codegen.services.ResourceService;
import com.intellij.uiDesigner.core.GridConstraints;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
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
    private JPanel rootPanel;
    private JPanel sourcePanel;
    private JPanel settingPanel;
    private JTextField usernameTextField;
    private JTextField pwdTextField;
    private JTextField dbUrlTextField;
    private JButton testButton;
    private JComboBox tableComboBox;
    private JTextField baseUriTextField;
    private JTextField moduleTextField;
    private JTable clsCfgTable;
    private JPanel methodCfgPanel;
    private JTextField tablePrefixTextField;
    private JButton genBtn;
    private JPanel codePanel;
    private JButton saveBtn;
    private JScrollPane rootScrollPanel;
    private JScrollPane clsTableScrollView;
    private JButton addMethodButton;
    private CodeSettingPanel codeSettingPanel;
    private DBSettingPanel dbSettingPanel;
    private List<MethodGrpCfgPanel> methodGrpCfgPanels = new ArrayList<>();

    private DBTable dbTable;
    private List<DBTable> dbTables;

    private CodeCfg codeCfg;
    private CodeCfg codeSetting = new CodeCfg();
    private ClassGrpCfgModel classGrp = new ClassGrpCfgModel();
    private ProjectCfg projectCfg = new ProjectCfg();
    private DBCfg dbCfg = new DBCfg();
    private MethodSelectionPopupMenu methodSelectionPopupMenu;
    public CodeGenPanel() {
        rootScrollPanel.setBorder(null);
        clsTableScrollView.setBorder(null);

        projectCfg.load();
        dbCfg.load();
        codeSettingPanel.setModel(projectCfg);
        dbSettingPanel.setModel(dbCfg);

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
        tablePrefixTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AppCtx.INSTANCE.getENV().put(EnvKey.TABLE_PREFIX, ((JTextField)e.getSource()).getText());
            }
        });
        genBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generate();
            }
        });
        addMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMethodPopupMenu();
            }
        });
        tabbedPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPanel.getSelectedIndex() == 0){
                    refreshDBCtx();
                }
            }
        });
        init();
    }
    /**
     *
     */
    private void init(){
        //加载默认方法
        classGrp.setCtrl(new ClassGrpCfgModel.ClassCfgModel());
        classGrp.setSvc(new ClassGrpCfgModel.ClassCfgModel());
        classGrp.setDao(new ClassGrpCfgModel.ClassCfgModel());

        codeCfg = ResourceService.INSTANCE.getCodeCfg();
        List<CodeCfg.Method> methods = new ArrayList<>();
        for (int i = 0; i< codeCfg.getMethods().size(); i++){
            CodeCfg.Method methodCfg = codeCfg.getMethods().get(i);
            CodeCfg.Method method = new CodeCfg.Method();
            method.setCtrl(new CodeCfg.MethodCfg());
            method.setSvc(new CodeCfg.MethodCfg());
            method.setDao(new CodeCfg.MethodCfg());
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
//        clsCfgTable.setBorder(BorderFactory.createCompoundBorder());
        clsCfgTable.setRowHeight(30);
    }
    public void refreshDBCtx(){
        DBCtx.INSTANCE.setDbCfg(dbCfg);
        DBCtx.INSTANCE.refresh();

        dbTables = DBCtx.INSTANCE.queryTables();
        if (dbTables != null) {
            List<String> items = dbTables.stream().map(e -> e.getName()).toList();

            DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();

            for (String element : items) {
                comboBoxModel.addElement(element);
            }
            tableComboBox.setModel(comboBoxModel);
            tableComboBox.setSelectedItem(null);
        }
    }
    private void showAddMethodPopupMenu(){
        if (dbTable == null){
           addMethodButton.setToolTipText("请选择表格");
            return;
        }
        addMethodButton.setToolTipText(null);
        if (methodSelectionPopupMenu == null){
            methodSelectionPopupMenu = new MethodSelectionPopupMenu();
            List<MethodSelectionPopupMenu.MenuItem> menuItems = codeCfg.getMethods().stream().map( e -> new MethodSelectionPopupMenu.MenuItem(e.getCtrl().getName(), e.getCtrl().getName())).toList();
            methodSelectionPopupMenu.setItems(menuItems);
        }
        methodSelectionPopupMenu.show(addMethodButton,0, addMethodButton.getHeight());
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
        for (DBTable e: dbTables){
            if (tableName.equals(e.getName())){
                dbTable = e;
            }
        }
        var p = new HashMap <String, String>();
        p.put("tableName", tableName);
        List<DBTableField> fields = DBCtx.INSTANCE.queryFields( p);
        dbTable.setFields(fields);


        String clsPrefix = tableName;
        String tablePrefix = (String)AppCtx.INSTANCE.getENV().get(EnvKey.TABLE_PREFIX);
        tablePrefix = tablePrefix == null ? "t_":tablePrefix;
        if (clsPrefix.startsWith(tablePrefix)){
            clsPrefix = clsPrefix.substring(tablePrefix.length());
        }
        clsPrefix = FieldUtils.INSTANCE.className(clsPrefix);
        AppCtx.INSTANCE.getENV().put(EnvKey.CLASS_PRERFIX, clsPrefix);

        updateClasses();
        updateMethods();
    }
    private String getHandledVar(String v, Map<String, Object> p){
        if (v == null){
            return v;
        }
        String r = v;
        for (String k : p.keySet()){
            r = r.replaceAll("\\{\\s*"+k+"\\s*\\}",  p.get(k).toString());
        }
        return r;
    }
    private List<CodeCfg.FieldCfg> getDefaultFields(String excludes, String includes){
        List<CodeCfg.FieldCfg> allowFields = new ArrayList<>();
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
                    allowFields.add(new CodeCfg.FieldCfg(field.getName(), false));
                    return;
                }
                return;
            }
            allowFields.add(new CodeCfg.FieldCfg(field.getName(), false));
        });
        return allowFields;
    }
    private void handleFields(MethodGrpCfgModel.MethodCfgModel methodCfgModel){
        methodCfgModel.setInputFields(getDefaultFields(methodCfgModel.getInputFieldExcludes(), methodCfgModel.getInputFieldIncludes()));
        methodCfgModel.setOutputFields(getDefaultFields(methodCfgModel.getOutputFieldExcludes(), methodCfgModel.getOutputFieldIncludes()));
    }
    private void handleClassName(MethodGrpCfgModel.MethodCfgModel methodCfgModel, Map param){
        methodCfgModel.setInputClassName(getHandledVar(methodCfgModel.getInputClassName(), param));
        methodCfgModel.setOutputClassName(getHandledVar(methodCfgModel.getOutputClassName(), param));
    }

    /**
     *选择完表执行
     */
    private void updateClasses(){
        /**
         * 表格处理
         */
        String[][] data = new String[1][3];
        data[0][0]  = getHandledVar(codeCfg.getCtrlClass().getClassName(), AppCtx.INSTANCE.getENV());
        data[0][1] = getHandledVar(codeCfg.getSvcClass().getClassName(),  AppCtx.INSTANCE.getENV());
        data[0][2] = getHandledVar(codeCfg.getDaoClass().getClassName(), AppCtx.INSTANCE.getENV());
        String[] headers = new String[]{codeCfg.getCtrlClass().getTitle(),codeCfg.getSvcClass().getTitle(),codeCfg.getDaoClass().getTitle()};
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(data, headers);
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                updateClassGrpModel();
                updateMethods();
            }
        });
        clsCfgTable.setModel(tableModel);
        updateClassGrpModel();
    }
    private void updateClassGrpModel(){;
        classGrp.getCtrl().setClassName((String)clsCfgTable.getModel().getValueAt(0,0));
        classGrp.getSvc().setClassName((String)clsCfgTable.getModel().getValueAt(0,1));
        classGrp.getDao().setClassName((String)clsCfgTable.getModel().getValueAt(0,2));
    }
    /**
     * 选择完表执行
     */
    private void updateMethods(){


        methodCfgPanel.removeAll();
        methodGrpCfgPanels.clear();
        GridLayout layout = new GridLayout(codeCfg.getMethods().size(), 1);
        methodCfgPanel.setLayout(layout);

        DefaultTableModel tableModel = (DefaultTableModel)clsCfgTable.getModel();
        String ctrlClassName = (String)tableModel.getDataVector().get(0).get(0);
        String svcClassName = (String)tableModel.getDataVector().get(0).get(1);
        String daoClassName = (String)tableModel.getDataVector().get(0).get(2);


        GridConstraints gridConstraints = new GridConstraints();
        List<String> tableFields = new ArrayList<>();
        for (int i = 0; i< dbTable.getFields().size(); i++){
            tableFields.add(dbTable.getFields().get(i).getName());
        }
        baseUriTextField.setText(getHandledVar(codeCfg.getCtrlClass().getBaseURI(), AppCtx.INSTANCE.getENV()));
        for (int i = 0; i< codeCfg.getMethods().size(); i++){
            CodeCfg.Method method = codeCfg.getMethods().get(i);
            MethodGrpCfgModel model = new MethodGrpCfgModel();
            model.setCtrl(new MethodGrpCfgModel.MethodCfgModel());
            model.setSvc(new MethodGrpCfgModel.MethodCfgModel());
            model.setDao(new MethodGrpCfgModel.MethodCfgModel());
            model.setRequest(new CodeCfg.RequestCfg());


            gridConstraints.myPreferredSize.height = 500;
            gridConstraints.setRow(i);

            try {
                BeanUtils.copyProperties(model.getRequest(), method.getRequest() == null ? new CodeCfg.RequestCfg(): method.getRequest());
                BeanUtils.copyProperties(model.getCtrl(), method.getCtrl() == null ? new MethodGrpCfgModel.MethodCfgModel(): method.getCtrl());
                BeanUtils.copyProperties(model.getSvc(), method.getSvc() == null ? new MethodGrpCfgModel.MethodCfgModel() : method.getSvc());
                BeanUtils.copyProperties(model.getDao(), method.getDao() == null ? new MethodGrpCfgModel.MethodCfgModel() : method.getDao());
                model.getCtrl().setFields(tableFields);
                model.getSvc().setFields(tableFields);
                model.getDao().setFields(tableFields);
                model.getCtrl().setClassName(ctrlClassName);
                model.getSvc().setClassName(svcClassName);
                model.getDao().setClassName(daoClassName);
//
                /**
                 * 处理类名
                 */

                handleClassName(model.getCtrl(), AppCtx.INSTANCE.getENV());
                handleClassName(model.getSvc(), AppCtx.INSTANCE.getENV());
                handleClassName(model.getDao(), AppCtx.INSTANCE.getENV());
                /**
                 * 处理字段
                 */
                handleFields(model.getCtrl());
                handleFields(model.getSvc());
                handleFields(model.getDao());
                MethodGrpCfgPanel itemCfgPanel = new MethodGrpCfgPanel();
                itemCfgPanel.init();
                itemCfgPanel.setModel(model);
                methodCfgPanel.add(itemCfgPanel.getContent(), gridConstraints);
                methodGrpCfgPanels.add(itemCfgPanel);
                itemCfgPanel.setRemoveEvent(new MethodGrpCfgPanel.RemoveEvent() {
                    @Override
                    public void onRemove(MethodGrpCfgPanel grpCfgPanel) {
                        grpCfgPanel.getContent().getParent().remove(grpCfgPanel.getContent());
                        methodGrpCfgPanels.remove(grpCfgPanel);
                    }
                });

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
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
    public void generate(){

        String module = moduleTextField.getText();
        List<MethodGrpCfgModel> methodsGrps = methodGrpCfgPanels.stream().map(e -> e.getModel()).toList();
        classGrp.getCtrl().setBaseURI(baseUriTextField.getText());
        new CodeGenerator().gen(module, dbTable, classGrp, methodsGrps, projectCfg);
    }
    public JPanel getContent()  {
        return this.mainPanel;
    }



    public class MethodSelectionPopupMenu extends JPopupMenu{
        private List<MenuItem> items;
        public MethodSelectionPopupMenu(){
            GridLayout gridLayout = new GridLayout();
            this.setLayout(gridLayout);
        }

        public List<MenuItem> getItems() {
            return items;
        }

        public void setItems(List<MenuItem> items) {
            this.items = items;
            this.removeAll();
            GridLayout gridLayout = (GridLayout) this.getLayout();
            gridLayout.setRows(items.size());
            this.setLayout(gridLayout);
            GridConstraints c = new GridConstraints();
            for (int i = 0; i< items.size(); i++){
                c.setRow(i);
                JButton btn = new JButton(items.get(i).getTitle());
                btn.setBackground(null);
                btn.setBorder(null);
                this.add(btn, c);
            }
        }
        public static class MenuItem{
            private String title;
            private String value;
            public MenuItem(String title, String value){
                this.title = title;
                this.value = value;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

}
