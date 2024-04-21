package com.springcodegen.idea.plugin.ui;

import com.springcodegen.idea.plugin.constants.MvcClassType;
import com.springcodegen.idea.plugin.ctx.*;
import com.springcodegen.idea.plugin.gen.*;
import com.springcodegen.idea.plugin.constants.EnvKey;
import com.springcodegen.idea.plugin.db.DBCtx;
import com.springcodegen.idea.plugin.db.model.DBTable;
import com.springcodegen.idea.plugin.db.model.DBTableField;
import com.springcodegen.idea.plugin.notify.NotificationCenter;
import com.springcodegen.idea.plugin.notify.NotificationType;
import com.springcodegen.idea.plugin.ui.pane.CodePreviewDialog;
import com.springcodegen.idea.plugin.ui.tookit.MessageBoxUtils;
import com.springcodegen.idea.plugin.util.FieldUtils;
import com.springcodegen.idea.plugin.gen.model.CtrlClass;
import com.springcodegen.idea.plugin.services.ResourceService;
import com.springcodegen.idea.plugin.swing.util.TextFieldUtils;
import com.springcodegen.idea.plugin.ui.pane.DomainPaneContainer;
import com.springcodegen.idea.plugin.util.StringUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.springcodegen.idea.plugin.config.CodeCfg;
import com.springcodegen.idea.plugin.swing.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class CodeGenPane {
    private JTabbedPane tabbedPane;
    private JPanel mainPane;
    private JComboBox tableComboBox;
    private JTextField baseUriTextField;
    private JTextField moduleTextField;
    private JPanel methodCfgPanel;
    private JTextField tablePrefixTextField;
    private JButton genBtn;
    private JPanel codePane;
    private JButton saveBtn;
    private JPanel dataSourcePane;
    private JPanel settingsPane;
    private JScrollPane rootScrollPane;
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
    private JButton ctrlClsPreviewButton;
    private JButton svcClsPreviewButton;
    private JButton daoClsPreviewButton;
    private JCheckBox relationCheckBox;
    private JComboBox relationTableCombox;
    private JTextField relationResourceNameTextField;
    private JLabel ctrlExistLabel;
    private JLabel svcExistLabel;
    private JLabel daoExistLabel;
    private JPanel settingPane;
    private DocSettingPane docSettingPane;

    private DBTable dbTable;
    private List<DBTable> dbTables;

    private MethodSelectionPopupMenu methodSelectionPopupMenu;
    private static String RELATION_COMP_NAME = "relation-group";
    public CodeGenPane() {
        rootScrollPane.setBorder(null);

        ResourceService.INSTANCE.prepareConfigFiles();
        CodeCfg.load();
        CodeSettingCtx.load();
        DBSettingCtx.load();
        dbSettingPane.setModel(DBSettingCtx.INSTANCE);
        codeSettingPane.setModel(CodeSettingCtx.INSTANCE);
        moduleTextField.setText(CodeSettingCtx.INSTANCE.getModule());

        tableComboBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    // 选择的下拉框选项
                    System.out.println(e.getItem());
                    String tableName = tableComboBox.getSelectedItem().toString();
                    dbTables.forEach( x ->{
                        if (tableName.equals(x.getName())){
                            selectTable(x);
                        }
                    });
                }
        });
        tablePrefixTextField.addActionListener( e -> {
                AppCtx.getENV().put(EnvKey.TABLE_PREFIX, ((JTextField)e.getSource()).getText());
        });
        genBtn.addActionListener( e -> {
                generate();
        });
        addMethodButton.addActionListener( e -> {
                showCreateMethodDialog();
        });
        dbSettingPane.setValueChangedListener( dbSettingPanel -> {
            try {
                DBCtx.refresh();
                reloadDbTables();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        Arrays.stream((new JTextField[]{moduleTextField, resourceNameTextField})).forEach( e -> {
            TextFieldUtils.INSTANCE.addTextChangedEvent(e, textField -> {
                pathPrefixLabel.setText(getPathSuffix());
                if (textField == moduleTextField) {
                    CodeSettingCtx.INSTANCE.setModule(moduleTextField.getText());
                    NotificationCenter.sendMessage(NotificationType.CODE_SETTING_UPDATED, null);
                }
                CtrlClass.Request request = MvcClassCtx.INSTANCE.getCtrlClass().getRequest();
                if (request == null){
                    MvcClassCtx.INSTANCE.getCtrlClass().setRequest( new CtrlClass.Request(getPathSuffix(), null));
                }else{
                    request.setPath(getPathSuffix());
                }
            });
        });
        Arrays.stream((new JTextField[]{ctrlClassNameTextField, svcClassNameTextField, daoClassNameTextField})).forEach( e -> {
            TextFieldUtils.INSTANCE.addTextChangedEvent(e, textField -> {
                mvcClassUpdated();
            });
        });
        ctrlClsPreviewButton.addActionListener(  actionEvent -> {
            String code = CtrlClassGenerator.createClass().toString();
            CodePreviewDialog.preview(code);
        });
        svcClsPreviewButton.addActionListener(  actionEvent -> {
            String code = SvcClassGenerator.createClass().toString();
            CodePreviewDialog.preview(code);
        });
        daoClsPreviewButton.addActionListener(  actionEvent -> {
            String code = DaoInterfaceGenerator.createClass().toString();
            String mapperXml = DaoMapperGenerator.createMapper();
            CodePreviewDialog.preview(code + "\n---\n" + mapperXml);
        });
        relationCheckBox.addItemListener( e ->{
            refreshRelationComponents();
        });
        relationTableCombox.addItemListener( e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // 选择的下拉框选项
                System.out.println(e.getItem());
                String tableName = relationTableCombox.getSelectedItem().toString();
                dbTables.forEach( x ->{
                    if (tableName.equals(x.getName())){
                        selectRelationTable(x);
                    }
                });
            }
        });
        refreshRelationComponents();
        NotificationCenter.register(NotificationType.DB_CONN_EXCEPTION, msg -> {
            String errorMsg = (String)msg.getData();
            if ( !org.apache.commons.lang3.StringUtils.isEmpty(errorMsg) ){
                MessageBoxUtils.showMessageAndFadeout(errorMsg);
            }
        });
        /**
         * 配置数据库
         */
        DBCtx.refresh();
        /**
         * 刷新表格
         */
        reloadDbTables();
    }
    private void refreshTableCombox(JComboBox combox){
        List<String> items = dbTables.stream().map(DBTable::getName).toList();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for (String element : items) {
            comboBoxModel.addElement(element);
        }
        combox.setModel(comboBoxModel);
        combox.setSelectedItem(null);
    }
//    private List<Component> searchComponentsByName(Component c, String name){
//        List<Component> a = new ArrayList<>();
//        if ( RELATION_COMP_NAME.equals( c.getName() ) ){
//            a.add(c);
//        }
//        if (c instanceof Container){
//            Arrays.stream(((Container) c).getComponents()).forEach( e -> {
//                List<Component> a2 = searchComponentsByName(e, name);
//                a.addAll(a2);
//            });
//        }
//        return a;
//    }
    private void refreshRelationComponents(){
        SwingUtils.searchComponentsByName(relationTableCombox.getParent(), RELATION_COMP_NAME).forEach(comp -> {
            comp.setVisible( relationCheckBox.isSelected() );
            if ( relationCheckBox.isSelected() ){
                refreshTableCombox(relationTableCombox);
            }
        });
    }
    private String getPathSuffix(){
        String moduleName = moduleTextField.getText();
        String resourceName = resourceNameTextField.getText();
        return String.format("%s/%s/%s",
                CodeSettingCtx.INSTANCE.getApiPrefix(),
                org.apache.commons.lang3.StringUtils.isEmpty(moduleName)?"{module}":moduleName,
                org.apache.commons.lang3.StringUtils.isEmpty(resourceName)?"resourceName":resourceName
        );
    }
    public void reloadDbTables(){

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.setSelectedItem(null);
        tableComboBox.setModel(comboBoxModel);
        SwingWorker<List<DBTable>, List<DBTable>> swingWorker = new SwingWorker<List<DBTable>, List<DBTable>>() {
            @Override
            protected List<DBTable> doInBackground() throws Exception {
                List<DBTable> result = DBCtx.queryTables();
                return result;
            }
            @Override
            protected void done() {
                try {
                    dbTables = get();
                    refreshTableCombox(tableComboBox);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        swingWorker.execute();
    }
    private void showCreateMethodDialog(){
        MethodCreateDialog dialog = MethodCreateDialog.create();
        dialog.setMethodTypes(CodeCfg.getInstance().getMethods().stream().filter(e -> e.getType().equals("SVC")).map( e -> e.getName()).toList());
        dialog.setListener(new MethodCreateDialog.MethodCreateDialogListener() {
            @Override
            public void onOK(MethodCreateDialog dialog1) {
                methodContainerPane.createMethod(dialog1.getMethodType(), dialog1.getMethodName(), dialog1.isCtrlChecked(), dialog1.isSvcChecked(), dialog1.isDaoChecked());
            }
        });
        dialog.setVisible(true);
    }
    private void queryTableFields(String tableName, Consumer<List<DBTableField>> e){
        SwingWorker<List<DBTableField>, Object> swingWorker = new SwingWorker<List<DBTableField>, Object>(){

            @Override
            protected List<DBTableField> doInBackground() throws Exception {

                var p = new HashMap <String, String>();
                p.put("tableName", tableName );
                List<DBTableField> fields = DBCtx.queryFields( p);
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
                e.accept(fields);
            }
        };
        swingWorker.execute();
    }
    private void selectTable(DBTable dbTable){
        this.dbTable = dbTable;
        AppCtx.setCurrentTable(dbTable);
        AppCtx.getENV().put("entityName", dbTable.getComment() == null ? dbTable.getName() : dbTable.getComment());
        queryTableFields(dbTable.getName(), fields -> {
            dbTable.setFields(fields);
            String clsPrefix = dbTable.getName();
            String tablePrefix = (String)AppCtx.getENV().get(EnvKey.TABLE_PREFIX);
            tablePrefix = tablePrefix == null ? "t_":tablePrefix;
            if (clsPrefix.startsWith(tablePrefix)){
                clsPrefix = clsPrefix.substring(tablePrefix.length());
            }
            resourceNameTextField.setText(clsPrefix.toLowerCase().replaceAll("_", "-"));
            clsPrefix = FieldUtils.INSTANCE.className(clsPrefix);
            AppCtx.getENV().put(EnvKey.CLASS_PRERFIX, clsPrefix);

            updateClasses();
            updateMethods();
        });
    }
    private void selectRelationTable(DBTable relationTable){
        dbTable.setRelationTable(relationTable);
        String tablePrefix = (String)AppCtx.getENV().get(EnvKey.TABLE_PREFIX);
        tablePrefix = tablePrefix == null ? "t_":tablePrefix;
        String relationResourceName = relationTable.getName().replaceAll("^"+tablePrefix, "");
        relationResourceNameTextField.setText(relationResourceName);
        queryTableFields(relationTable.getName(), fields -> {
            relationTable.setFields(fields);
        });
    }
    /**
     *选择完表执行
     */
    private void updateClasses(){
        resetModels();
        resetClasses();
    }
    private void resetClasses(){
        Map p = AppCtx.getEnvParams();
        ctrlClassNameTextField.setText(StringUtils.INSTANCE.replacePlaceholders(CodeCfg.getInstance().getCtrlClass().getClassName(), p));
        svcClassNameTextField.setText(StringUtils.INSTANCE.replacePlaceholders(CodeCfg.getInstance().getSvcClass().getClassName(),  p));
        daoClassNameTextField.setText(StringUtils.INSTANCE.replacePlaceholders(CodeCfg.getInstance().getDaoClass().getClassName(), p));

        mvcClassUpdated();

        MvcClassCtx.INSTANCE.getCtrlClass().setComment(dbTable.getComment());
        MvcClassCtx.INSTANCE.getCtrlClass().setTableName(dbTable.getName());
        MvcClassCtx.INSTANCE.getSvcClass().setTableName(dbTable.getName());
        MvcClassCtx.INSTANCE.getSvcClass().setComment(dbTable.getComment());
        MvcClassCtx.INSTANCE.getDaoClass().setTableName(dbTable.getName());
        MvcClassCtx.INSTANCE.getDaoClass().setComment(dbTable.getComment());
    }
    private void mvcClassUpdated(){

        MvcClassCtx.INSTANCE.resetClass(MvcClassType.CTRL, ctrlClassNameTextField.getText());
        MvcClassCtx.INSTANCE.resetClass(MvcClassType.SVC,svcClassNameTextField.getText());
        MvcClassCtx.INSTANCE.resetClass(MvcClassType.DAO,daoClassNameTextField.getText());
        ctrlExistLabel.setText(CtrlClassGenerator.fileExists() ? "!" : "");
        svcExistLabel.setText(SvcClassGenerator.fileExists() ? "!" : "");
        daoExistLabel.setText(DaoInterfaceGenerator.fileExists() ? "!" : "");
    }
    private void resetModels(){
        domainContainer.reset();
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
        if ( !CodeSettingCtx.hasReady() ){
            MessageBoxUtils.showMessageAndFadeout("请先设置选项卡中配置相关参数...");
            tabbedPane.setSelectedIndex(2);
            return;
        }
        if (dbTables == null || dbTables.size() == 0){
            MessageBoxUtils.showMessageAndFadeout("当前没有查询到数据库表，请检查数据库配置...");
            tabbedPane.setSelectedIndex(1);
            return;
        }
        if ( AppCtx.getCurrentTable() == null ){
            MessageBoxUtils.showMessageAndFadeout("请选择数据库表...");
            tableComboBox.requestFocus();
            return;
        }
        new CodeGenerator().gen( );
        MessageBoxUtils.showMessageAndFadeout("代码生成完成！");
    }
    public JPanel getContent()  {
        return this.mainPane;
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
