package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.*;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.constants.EnvKey;
import com.cmcc.paas.ideaplugin.codegen.db.DBCtx;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.CodeGenerator;
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils;
import com.cmcc.paas.ideaplugin.codegen.gen.ModelResult;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.DaoClass;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.SvcClass;
import com.cmcc.paas.ideaplugin.codegen.services.ResourceService;
import com.intellij.uiDesigner.core.GridConstraints;

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
import java.util.*;
import java.util.List;

public class CodeGenPane {
    private JTabbedPane tabbedPanel;
    private JPanel mainPanel;
    private JComboBox tableComboBox;
    private JTextField baseUriTextField;
    private JTextField moduleTextField;
    private JTable clsCfgTable;
    private JPanel methodCfgPanel;
    private JTextField tablePrefixTextField;
    private JButton genBtn;
    private JPanel codePanel;
    private JButton saveBtn;
    private JPanel dataSourcePanel;
    private JPanel settingsPanel;
    private JScrollPane rootScrollPanel;
    private JScrollPane clsTableScrollView;
    private JButton addMethodButton;
    private CodeSettingPane codeSettingPane;
    private DBSettingPane dbSettingPanel;
    private MethodContainerPane methodContainerPane;

    private DBTable dbTable;
    private List<DBTable> dbTables;

    private CodeCfg codeCfg;
    private CodeCfg codeSetting = new CodeCfg();
    private ProjectCfg projectCfg = new ProjectCfg();
    private DBCfg dbCfg = new DBCfg();
    private CtrlClass ctrlClass = null;
    private SvcClass svcClass =  null;
    private DaoClass daoClass =  null;
    private MethodSelectionPopupMenu methodSelectionPopupMenu;
    public CodeGenPane() {
        rootScrollPanel.setBorder(null);
        clsTableScrollView.setBorder(null);
        clsTableScrollView.setViewportBorder(null);

        projectCfg.load();
        dbCfg.load();
        codeSettingPane.setModel(projectCfg);
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
                showCreateMethodDialog();
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
        refreshDBCtx();
    }
    /**
     *
     */
    private void init(){
        codeCfg = ResourceService.INSTANCE.getCodeCfg();
        clsCfgTable.setRowHeight(30);
        methodContainerPane.setCodeCfg(codeCfg);
    }
    public void refreshDBCtx(){
        DBCtx.INSTANCE.setDbCfg(dbCfg);
        DBCtx.INSTANCE.refresh();
        //表格下拉框
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
    private void showCreateMethodDialog(){
        MethodCreateDialog dialog = MethodCreateDialog.create();
        dialog.setListener(new MethodCreateDialog.MethodCreateDialogListener() {
            @Override
            public void onOK(String methodName, Boolean ctrlChecked, Boolean svcChecked, Boolean daoChecked) {
                methodContainerPane.createMethod(methodName, ctrlChecked, svcChecked, daoChecked);
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
        methodContainerPane.setDbTable(dbTable);
        methodContainerPane.setDbTableFields(fields);


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

//        List<CodeCfg.FieldCfg> fieldCfgs = fields.stream().map(f -> new CodeCfg.FieldCfg(f.getName(), false, f.getType(), f.getComment())).toList();

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
        methodCfgModel.setInputClassName(getHandledVar(methodCfgModel.getInputClassName(), param));
        methodCfgModel.setOutputClassName(getHandledVar(methodCfgModel.getOutputClassName(), param));
    }
*/
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
                updateClassCfg();
                updateMethods();
            }
        });
        clsCfgTable.setModel(tableModel);
        ctrlClass = new CtrlClass((String)clsCfgTable.getModel().getValueAt(0,0));
        ctrlClass.setComment(dbTable.getComment());
        svcClass = new SvcClass((String)clsCfgTable.getModel().getValueAt(0,1));
        daoClass = new DaoClass((String)clsCfgTable.getModel().getValueAt(0,2));
    }
    private void updateClassCfg(){
//        classGrp.getCtrl().setClassName((String)clsCfgTable.getModel().getValueAt(0,0));
//        classGrp.getSvc().setClassName((String)clsCfgTable.getModel().getValueAt(0,1));
//        classGrp.getDao().setClassName((String)clsCfgTable.getModel().getValueAt(0,2));
        ctrlClass.setClassName((String)clsCfgTable.getModel().getValueAt(0,0));
        svcClass.setClassName((String)clsCfgTable.getModel().getValueAt(0,1));
        daoClass.setClassName((String)clsCfgTable.getModel().getValueAt(0,2));
    }

    /**
     * 选择完表执行
     */
    private void updateMethods(){
        methodContainerPane.reset();
        methodContainerPane.setCtrlClass(ctrlClass);
        methodContainerPane.setSvcClass(svcClass);
        methodContainerPane.setDaoClass(daoClass);

        methodContainerPane.createMethod("add", true, true, true);
        methodContainerPane.createMethod("update", true, true, true);
        methodContainerPane.createMethod("remove", true, true, true);
        methodContainerPane.createMethod("get", true, true, true);
        methodContainerPane.createMethod("search", true, true, true);

    }
    public void generate(){
        ctrlClass.setRequest(new CtrlClass.Request(baseUriTextField.getText(), null));
        ModelResult modelResult = methodContainerPane.getCfgResult();
        new CodeGenerator().gen(moduleTextField.getText(), modelResult, projectCfg);
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
