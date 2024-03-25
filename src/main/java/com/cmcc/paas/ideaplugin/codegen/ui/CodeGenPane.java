package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.*;
import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.constants.EnvKey;
import com.cmcc.paas.ideaplugin.codegen.db.DBCtx;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.CodeGenerator;
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils;
import com.cmcc.paas.ideaplugin.codegen.gen.ModelResult;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass;
import com.cmcc.paas.ideaplugin.codegen.services.ResourceService;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.DomainPaneContainer;
import com.cmcc.paas.ideaplugin.codegen.util.StringUtils;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.List;

public class CodeGenPane {
    private JTabbedPane tabbedPanel;
    private JPanel mainPanel;
    private JComboBox tableComboBox;
    private JTextField baseUriTextField;
    private JTextField moduleTextField;
    private JPanel methodCfgPanel;
    private JTextField tablePrefixTextField;
    private JButton genBtn;
    private JPanel codePanel;
    private JButton saveBtn;
    private JPanel dataSourcePanel;
    private JPanel settingsPanel;
    private JScrollPane rootScrollPanel;
//    private JScrollPane clsTableScrollView;
    private JButton addMethodButton;
    private CodeSettingPane codeSettingPane;
    private DBSettingPane dbSettingPane;
    private MethodContainerPane methodContainerPane;
    private JTextField ctrlClassNameTextField;
    private JTextField svcClassNameTextField;
    private JTextField daoClassNameTextField;
    private JTextField resourceNameTextField;
    private DomainPaneContainer domainContainer;
    private JLabel pathPrefixLabel;

    private DBTable dbTable;
    private List<DBTable> dbTables;

    private CodeCfg codeCfg;
    private final ProjectCfg projectCfg = new ProjectCfg();
    private final DBCfg dbCfg = new DBCfg();
    private MethodSelectionPopupMenu methodSelectionPopupMenu;
    public CodeGenPane() {
        rootScrollPanel.setBorder(null);
//        clsTableScrollView.setBorder(null);
//        clsTableScrollView.setViewportBorder(null);

        ProjectCfg.load();
        dbCfg.load();
        dbSettingPane.setModel(dbCfg);
        codeSettingPane.setModel(AppCtx.INSTANCE.getProjectCfg());

        tableComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // 选择的下拉框选项
                    System.out.println(e.getItem());
                    ResourceService.INSTANCE.readYaml("code-cfg.yaml");
                    String tableName = tableComboBox.getSelectedItem().toString();
                    dbTables.forEach( x ->{
                        if (tableName.equals(x.getName())){
                            selectTable(x);
                        }
                    });
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
                showCreateMethodDialog();
            }
        });
        tabbedPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPanel.getSelectedIndex() == 0){
//                    refreshDBCtx();
                }
            }
        });
        dbSettingPane.setValueChangedListener(new DBSettingPane.ValueChangedListener() {
            @Override
            public void onValueChanged(DBSettingPane dbSettingPanel) {
                DBCtx.INSTANCE.refresh();
                refreshDBCtx();
            }
        });
        Arrays.stream((new JTextField[]{moduleTextField, resourceNameTextField})).forEach( e -> {
            TextFieldUtils.INSTANCE.addTextChangedEvent(e,  textField -> {
                pathPrefixLabel.setText(getPathSuffix());
            });
        });
        codeCfg = ResourceService.INSTANCE.getCodeCfg();
//        clsCfgTable.setRowHeight(30);
        methodContainerPane.setCodeCfg(codeCfg);
        domainContainer.setModelCfgs(codeCfg.getModels());

        /**
         * 配置数据库
         */
        DBCtx.INSTANCE.setDbCfg(dbCfg);
        DBCtx.INSTANCE.refresh();
        /**
         * 刷新表格
         */
        refreshDBCtx();
    }
    private String getPathSuffix(){
        String moduleName = moduleTextField.getText();
        String resourceName = resourceNameTextField.getText();
        return String.format("%s/%s/%s",
                AppCtx.INSTANCE.getProjectCfg().getApiPrefix(),
                org.apache.commons.lang3.StringUtils.isEmpty(moduleName)?"{module}":moduleName,
                org.apache.commons.lang3.StringUtils.isEmpty(resourceName)?"resourceName":resourceName
        );
    }
    public void refreshDBCtx(){

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.setSelectedItem(null);
        tableComboBox.setModel(comboBoxModel);
        SwingWorker<List<DBTable>, List<DBTable>> swingWorker = new SwingWorker<List<DBTable>, List<DBTable>>() {
            @Override
            protected List<DBTable> doInBackground() throws Exception {
                List<DBTable> result = DBCtx.INSTANCE.queryTables();
                return result;
            }
            @Override
            protected void done() {
                try {
                    dbTables = get();
                    List<String> items = dbTables.stream().map(DBTable::getName).toList();
                    DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
                    for (String element : items) {
                        comboBoxModel.addElement(element);
                    }
                    tableComboBox.setModel(comboBoxModel);
                    tableComboBox.setSelectedItem(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        swingWorker.execute();
    }
    private void showCreateMethodDialog(){
        MethodCreateDialog dialog = MethodCreateDialog.create();
        dialog.setMethodTypes(codeCfg.getMethods().stream().filter(e -> e.getType().equals("SVC")).map( e -> e.getName()).toList());
        dialog.setListener(new MethodCreateDialog.MethodCreateDialogListener() {
            @Override
            public void onOK(MethodCreateDialog dialog1) {
                methodContainerPane.createMethod(dialog1.getMethodType(), dialog1.getMethodName(), dialog1.isCtrlChecked(), dialog1.isSvcChecked(), dialog1.isDaoChecked());
            }
        });
        dialog.setVisible(true);
    }
    private void showAddMethodPopupMenu(){
        if (dbTable == null){
           addMethodButton.setToolTipText("请选择表格");
            return;
        }
        addMethodButton.setToolTipText(null);
        if (methodSelectionPopupMenu == null){
            methodSelectionPopupMenu = new MethodSelectionPopupMenu();
            List<MethodSelectionPopupMenu.MenuItem> menuItems = codeCfg.getMethods().stream().map( e -> new MethodSelectionPopupMenu.MenuItem(e.getName(), e.getName())).toList();
            methodSelectionPopupMenu.setItems(menuItems);
        }
        methodSelectionPopupMenu.show(addMethodButton,0, addMethodButton.getHeight());
    }
    private void selectTable(DBTable dbTable){
        this.dbTable = dbTable;
        AppCtx.INSTANCE.setCurrentTable(dbTable);
        SwingWorker<List<DBTableField>, Object> swingWorker = new SwingWorker<List<DBTableField>, Object>(){

            @Override
            protected List<DBTableField> doInBackground() throws Exception {

                var p = new HashMap <String, String>();
                p.put("tableName", dbTable.getName());
                List<DBTableField> fields = DBCtx.INSTANCE.queryFields( p);
                return fields;
            }
            @Override
            protected void done(){
                List<DBTableField> fields = null;
                try {
                    fields = get();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                dbTable.setFields(fields);
                String clsPrefix = dbTable.getName();
                String tablePrefix = (String)AppCtx.INSTANCE.getENV().get(EnvKey.TABLE_PREFIX);
                tablePrefix = tablePrefix == null ? "t_":tablePrefix;
                if (clsPrefix.startsWith(tablePrefix)){
                    clsPrefix = clsPrefix.substring(tablePrefix.length());
                }
                resourceNameTextField.setText(clsPrefix.toLowerCase().replaceAll("_", "-"));
                clsPrefix = FieldUtils.INSTANCE.className(clsPrefix);
                AppCtx.INSTANCE.getENV().put(EnvKey.CLASS_PRERFIX, clsPrefix);

                updateClasses();
                updateMethods();
            }
        };
        swingWorker.execute();
    }
    /*
    private List<CodeCfg.FieldDefine> getDefaultFields(String excludes, String includes){
        List<CodeCfg.FieldDefine> allowFields = new ArrayList<>();
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
                    allowFields.add(new CodeCfg.FieldDefine(field.getName(), false, field.getType()));
                    return;
                }
                return;
            }
            allowFields.add(new CodeCfg.FieldDefine(field.getName(), false, field.getType()));
        });
        return allowFields;
    }
    private void handleFields(MethodGrpCfgModel.MethodCfgModel methodCfgModel){
        methodCfgModel.setInputFields(getDefaultFields(methodCfgModel.getInputFieldExcludes(), methodCfgModel.getInputFieldIncludes()));
        methodCfgModel.setOutputFields(getDefaultFields(methodCfgModel.getOutputFieldExcludes(), methodCfgModel.getOutputFieldIncludes()));
    }
    private void handleClassName(MethodGrpCfgModel.MethodCfgModel methodCfgModel, Map param){
        methodCfgModel.setInputClassName(StringUtils.replacePlaceholders(methodCfgModel.getInputClassName(), param));
        methodCfgModel.setOutputClassName(StringUtils.replacePlaceholders(methodCfgModel.getOutputClassName(), param));
    }
*/
    /**
     *选择完表执行
     */
    private void updateClasses(){
        resetModels();
        resetClasses();
    }
    private void resetClasses(){
        ctrlClassNameTextField.setText(StringUtils.INSTANCE.replacePlaceholders(codeCfg.getCtrlClass().getClassName(), AppCtx.INSTANCE.getENV()));
        svcClassNameTextField.setText(StringUtils.INSTANCE.replacePlaceholders(codeCfg.getSvcClass().getClassName(),  AppCtx.INSTANCE.getENV()));
        daoClassNameTextField.setText(StringUtils.INSTANCE.replacePlaceholders(codeCfg.getDaoClass().getClassName(), AppCtx.INSTANCE.getENV()));

        MvcClassCtx.INSTANCE.setClassName(MvcClassType.CTRL, ctrlClassNameTextField.getText());
        MvcClassCtx.INSTANCE.setClassName(MvcClassType.SVC,svcClassNameTextField.getText());
        MvcClassCtx.INSTANCE.setClassName(MvcClassType.DAO,daoClassNameTextField.getText());

        MvcClassCtx.INSTANCE.getCtrlClass().setComment(dbTable.getComment());
        MvcClassCtx.INSTANCE.getCtrlClass().setTableName(dbTable.getName());
        MvcClassCtx.INSTANCE.getSvcClass().setTableName(dbTable.getName());
        MvcClassCtx.INSTANCE.getSvcClass().setComment(dbTable.getComment());
        MvcClassCtx.INSTANCE.getDaoClass().setTableName(dbTable.getName());
        MvcClassCtx.INSTANCE.getDaoClass().setComment(dbTable.getComment());
    }
    private void resetModels(){
        domainContainer.reset();
    }
    private void updateClassCfg(){
        MvcClassCtx.INSTANCE.setClassName(MvcClassType.CTRL, ctrlClassNameTextField.getText());
        MvcClassCtx.INSTANCE.setClassName(MvcClassType.SVC, svcClassNameTextField.getText());
        MvcClassCtx.INSTANCE.setClassName(MvcClassType.DAO, daoClassNameTextField.getText());
    }

    /**
     * 选择完表执行
     */
    private void updateMethods(){
        methodContainerPane.reset();

        methodContainerPane.createMethod("add", "add", true, true, true);
        methodContainerPane.createMethod("update", "update", true, true, true);
        methodContainerPane.createMethod("remove", "remove", true, true, true);
        methodContainerPane.createMethod("get", "get", true, true, true);
        methodContainerPane.createMethod("search", "search", true, true, true);

    }
    public void generate(){
        System.out.println("generate 1");
        MessageBox.showMessage("生成中...");
        System.out.println("generate 2");
        //resource name for path
        String resourceName = resourceNameTextField.getText();
        if (resourceName != null){
            resourceName = resourceName.toLowerCase();
        }
        MvcClassCtx.INSTANCE.getCtrlClass().setRequest(new CtrlClass.Request(getPathSuffix(), null));
        ModelResult modelResult = methodContainerPane.getCfgResult();
        System.out.println("generate 3");
        new CodeGenerator().gen(moduleTextField.getText(), modelResult, projectCfg);
        System.out.println("generate 4");
        MessageBox.showMessageAndFadeout("代码生成完成！");
//        String module = moduleTextField.getText();
//        List<MethodGrpCfgModel> methodsGrps = methodGrpCfgPanels.stream().map(e -> e.getModel()).toList();
//        classGrp.getCtrl().setBaseURI(baseUriTextField.getText());
//        new CodeGenerator().gen(module, dbTable, classGrp, methodsGrps, projectCfg);
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
