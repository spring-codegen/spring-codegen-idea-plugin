package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.FieldUtils;
import com.cmcc.paas.ideaplugin.codegen.gen.ModelResult;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.DaoClass;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.SvcClass;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.CtrlMethodSettingPane;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.DaoMethodSettingPane;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.MethodSettingPane;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.SvcMethodSettingPane;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangyinghui
 * @date 2023/11/24
 */
public class MethodContainerPane {
    private JScrollPane scrollPane;
    private MethodContainerBackgroundPane container;
    private Map<MethodSettingPane.ClassType, Map<String, MethodItemHolder>> allMethods = new LinkedHashMap<>();
    private Map<DomainType, List<ClassModel>> modelMaps;
    private static int COLUMN_NUM = 3;
    private static int ITEM_MARGIN_H = 50;
    private static int ITEM_MARGIN_V = 20;
    private static int CONTAINER_PADDING_RIGHT = 30;
    private static int ITEM_HEIGHT = 220;
    private CodeCfg codeCfg;
    private List<DBTableField> dbTableFields;
    private CtrlClass ctrlClass = null;
    private SvcClass svcClass =  null;
    private DaoClass daoClass =  null;
    private DBTable dbTable;

    public Map<DomainType, List<ClassModel>> getModelMaps() {
        return modelMaps;
    }

    public void setModelMaps(Map<DomainType, List<ClassModel>> modelMaps) {
        this.modelMaps = modelMaps;
    }

    public CtrlClass getCtrlClass() {
        return ctrlClass;
    }

    public void setCtrlClass(CtrlClass ctrlClass) {
        this.ctrlClass = ctrlClass;
    }

    public SvcClass getSvcClass() {
        return svcClass;
    }

    public void setSvcClass(SvcClass svcClass) {
        this.svcClass = svcClass;
    }

    public DaoClass getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(DaoClass daoClass) {
        this.daoClass = daoClass;
    }

    public DBTable getDbTable() {
        return dbTable;
    }

    public void setDbTable(DBTable dbTable) {
        this.dbTable = dbTable;
    }

    public MethodContainerPane(){
        GridBagConstraints constraints = new GridBagConstraints();
        container = new MethodContainerBackgroundPane();
        scrollPane.setBorder(null);
        scrollPane.setViewportView(container);
        container.setLayout(null);
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                resize();
            }
        });
    }
    public CodeCfg.MethodCfg getMethodCfg(MethodSettingPane.ClassType classType, String methodName){
        for(CodeCfg.MethodCfg m : codeCfg.getMethods()){
            if (MethodSettingPane.ClassType.valueOf(m.getType()) == classType && methodName.indexOf(m.getName()) >= 0){
                return m;
            }
        }
        return null;
    }

    private List<ClassModel.Field> getDefaultFields(String excludes, String includes){
        List<ClassModel.Field> allowFields = new ArrayList<>();
        dbTableFields.forEach(field -> {
            if (StringUtils.isNotEmpty(excludes)){
                boolean isExclude = Arrays.stream(excludes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
                if (isExclude){
                    return;
                }
            }

            ClassModel.Field f = new ClassModel.Field(FieldUtils.INSTANCE.propertyName(field.getName()), FieldUtils.INSTANCE.javaType(field.getType()), field.getComment(), field.getNotNull(),null, null);
            f.setColumn(field.getName());
            if (field.getComment() != null && field.getComment().startsWith("JSON:")){
                f.setJavaType("Map");
            }
            if (field.getMaxLen() != null && field.getMaxLen() > 4) {
                f.setMaxLen(field.getMaxLen() - 4);
            }

            if (StringUtils.isNotEmpty(includes)){
                boolean isInclude = Arrays.stream(includes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
                if (isInclude){
                    allowFields.add(f);
                    return;
                }
                return;
            }
            allowFields.add( f );
        });
        return allowFields;
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
    public MethodSettingPane.MethodSettingModel getDefaultMethodCfgModel(MethodSettingPane.ClassType classType, String methodType, String methodName){
        Map p = AppCtx.INSTANCE.getENV();
        p.put("entityName", dbTable.getComment() == null ? dbTable.getName() : dbTable.getComment());
        String className = classType == MethodSettingPane.ClassType.CTRL ? ctrlClass.getClassName() : classType == MethodSettingPane.ClassType.SVC ? svcClass.getClassName() : daoClass.getClassName();
        CodeCfg.MethodCfg methodCfg = getMethodCfg(classType, methodType);
        MethodSettingPane.MethodSettingModel model = new MethodSettingPane.MethodSettingModel();
        model.setClassName(className);
        model.setMethodName(methodName);
        model.setClassType(classType);
        if (methodCfg.getRequest() != null) {
            model.setPath(methodCfg.getRequest().getPath());
            model.setHttpMethod(methodCfg.getRequest().getHttpMethod());
        }
        model.setMethodType(methodCfg.getName());
        model.setComment(getHandledVar(methodCfg.getComment(), p));
        model.setDbTableFields(getDbTableFields());
        List<MethodSettingPane.MethodSettingModel.MethodArgModel> args = new ArrayList<>();
        for(CodeCfg.MethodArg arg : methodCfg.getArgs()){
            MethodSettingPane.MethodSettingModel.MethodArgModel methodArgModel = MethodSettingPane.MethodSettingModel.MethodArgModel.of(
                    getHandledVar(arg.getClassName(), AppCtx.INSTANCE.getENV()),
                    arg.getRefName(),
                    arg.getListTypeFlag(),
                    arg.isPathVar()
                    );
            methodArgModel.setRefName(arg.getRefName());
            args.add(methodArgModel);
        }
        model.setArgs(args);
        if (methodCfg.getResult() != null){
            MethodSettingPane.MethodSettingModel.MethodResultModel methodResultModel = MethodSettingPane.MethodSettingModel.MethodResultModel.of(
                    getHandledVar(methodCfg.getResult().getClassName(), AppCtx.INSTANCE.getENV()),
                    methodCfg.getResult().getRefName(),
                    methodCfg.getResult().getListTypeFlag(),
                    methodCfg.getResult().getOutputPaged());
            methodResultModel.setRefName(methodCfg.getResult().getRefName());
            model.setResult(methodResultModel);
        }



        model.setSqlDataFields(getDefaultFields(methodCfg.getSqlDataFieldExcludes(), methodCfg.getSqlDataFieldIncludes()));
        model.setSqlCondFields(getDefaultFields(methodCfg.getSqlConditionFieldExcludes(), methodCfg.getSqlConditionFieldIncludes()));

        return model;
    }
    public MethodItemHolder addClassMethod(MethodSettingPane.ClassType classType, String methodType, String methodName){
        MethodSettingPane.ClassType k = classType;
        if ( !allMethods.containsKey(k) ){
            allMethods.put(k, new LinkedHashMap<>());
        }
        MethodItemHolder holder = allMethods.get(k).get(methodName);
        if (holder == null){
            holder = new MethodItemHolder();
            allMethods.get(k).put(methodName, holder);
        }

        //数据缓存

        int row = allMethods.get(k).size();

        int w = container.getWidth()/COLUMN_NUM - ITEM_MARGIN_H;
        int x = classType.ordinal() * (w + ITEM_MARGIN_H);
        int y = (row-1) * (ITEM_HEIGHT + ITEM_MARGIN_H);
        //界面
        if ( classType == MethodSettingPane.ClassType.DAO  ){
            holder.panel = new DaoMethodSettingPane();
        }else if ( classType == MethodSettingPane.ClassType.SVC  ){
            holder.panel = new SvcMethodSettingPane();
        }else{
            holder.panel =new CtrlMethodSettingPane();
        }
        holder.panel.setModelMaps(modelMaps);
        holder.panel.getContent().setSize(w, ITEM_HEIGHT);
        holder.panel.getContent().setLocation(x,y);
        holder.panel.setModel(getDefaultMethodCfgModel(classType, methodType, methodName));
        this.container.add(holder.panel.getContent());
        holder.panel.setMethodCfgPaneActionListener(new MethodSettingPane.MethodCfgPaneActionListener() {
            @Override
            public void onClose(MethodSettingPane methodSettingPane) {
                removePane(methodSettingPane);
            }
        });
        return holder;
    }
    public void removePane(MethodSettingPane methodSettingPane){
        MethodSettingPane.ClassType classType = methodSettingPane.getModel().getClassType();
        Map<String, MethodItemHolder> m = allMethods.get(classType);
        Optional<Map.Entry<String, MethodItemHolder>> x =  m.entrySet().stream().filter(e -> e.getValue().panel == methodSettingPane).findFirst();
        if (x.isPresent()){
            Map.Entry<String, MethodItemHolder> e = x.get();
            MethodItemHolder holder = e.getValue();
            this.container.remove(holder.panel.getContent());
            if (holder.dependency != null){
                holder.dependency.caller = null;
            }
            if (holder.caller != null){
                holder.caller.dependency = null;
            }
            m.remove(e.getKey());
            this.resize();
        }
    }
    public void createMethod(String methodType, String methodName, Boolean ctrlChecked, Boolean svcChecked, Boolean daoChecked){
        MethodItemHolder ctrlMethodHolder = ctrlChecked ?  addClassMethod(MethodSettingPane.ClassType.CTRL, methodType, methodName) : null;
        MethodItemHolder svcMethodHolder = svcChecked ?  addClassMethod(MethodSettingPane.ClassType.SVC, methodType, methodName) : null;
        MethodItemHolder daoMethodHolder = daoChecked ?  addClassMethod(MethodSettingPane.ClassType.DAO, methodType, methodName) : null;
        if (ctrlMethodHolder != null && svcMethodHolder != null){
            ctrlMethodHolder.dependency = svcMethodHolder;
            svcMethodHolder.caller = ctrlMethodHolder;
        }
        if (daoMethodHolder != null && svcMethodHolder != null){
            svcMethodHolder.dependency = daoMethodHolder;
            daoMethodHolder.caller = svcMethodHolder;
        }
        this.resize();
    }
    public void resize(){
        if (allMethods == null || allMethods.size() == 0 ){
            return;
        }
        int w = (scrollPane.getWidth() - (COLUMN_NUM - 1) * ITEM_MARGIN_H - CONTAINER_PADDING_RIGHT)/COLUMN_NUM;
        List<MethodContainerBackgroundPane.Line> lines = new ArrayList<>();
        int scrollViewHeight = 0;
        for (MethodSettingPane.ClassType t : MethodSettingPane.ClassType.values()){
            int row = 0;
            for ( Map.Entry<String, MethodItemHolder> e : allMethods.get(t).entrySet() ){
                int x = t.ordinal() * (w + ITEM_MARGIN_H);
                int y = row * (ITEM_HEIGHT + ITEM_MARGIN_V);
                e.getValue().panel.getContent().setBounds(x, y, w, ITEM_HEIGHT);
                scrollViewHeight = Integer.max(scrollViewHeight, y + ITEM_HEIGHT);
                row ++;
            }
        }

//        container.setSize(new Dimension(container.getWidth(), scrollViewHeight));
//        scrollPane.setPreferredSize();
        container.setPreferredSize(new Dimension(scrollPane.getWidth(), scrollViewHeight + ITEM_MARGIN_V));
        repaintRelations();
    }
    public void repaintRelations(){
        List<MethodContainerBackgroundPane.Line> lines = new ArrayList<>();
        for (MethodSettingPane.ClassType t : MethodSettingPane.ClassType.values()){
            for ( MethodItemHolder h : allMethods.get(t).values() ){
                if (h.dependency != null){
                    int offsetX = h.panel.getContent().getWidth();
                    int offsetY = h.dependency.panel.getContent().getHeight()/2;
                    Point p1 = h.panel.getContent().getLocation();
                    Point p2 = h.dependency.panel.getContent().getLocation();
                    lines.add(new MethodContainerBackgroundPane.Line(new Point(p1.x + offsetX, p1.y + offsetY), new Point(p2.x, p2.y + offsetY)));
                }
            }
        }
        container.setLines(lines);
    }
    public void reset(){
        if ( this.container == null ){
            return;
        }
        this.container.clear();
        allMethods.clear();
        this.container.updateUI();
    }

    public CodeCfg getCodeCfg() {
        return codeCfg;
    }

    public void setCodeCfg(CodeCfg codeCfg) {
        this.codeCfg = codeCfg;
    }

    public List<DBTableField> getDbTableFields() {
        return dbTableFields;
    }

    public void setDbTableFields(List<DBTableField> dbTableFields) {
        this.dbTableFields = dbTableFields;
    }
    public ModelResult getCfgResult(){
        ModelResult result = new ModelResult();
        ctrlClass.setRefName(FieldUtils.INSTANCE.getRefName(ctrlClass.getClassName()));
        svcClass.setRefName(FieldUtils.INSTANCE.getRefName(svcClass.getClassName()));
        daoClass.setRefName(FieldUtils.INSTANCE.getRefName(daoClass.getClassName()));
        result.setCtrlClass(ctrlClass);
        result.setSvcClass(svcClass);
        result.setDaoClass(daoClass);
        List<ClassModel.Method> ctrlMethods = new ArrayList<>();
        List<ClassModel.Method> svcMethods = new ArrayList<>();
        List<ClassModel.Method> daoMethods = new ArrayList<>();
        Map<String, ClassModel> args = new LinkedHashMap<>();
        Map<String, ClassModel> results = new LinkedHashMap<>();
        Map<String, ClassModel> entities = new LinkedHashMap<>();
        Map<String, ClassModel> daoArgs = new LinkedHashMap<>();
        Map<String, ClassModel> daoResults = new LinkedHashMap<>();
        List<String> filterResults = new ArrayList<>();
//        for (Map.Entry<MethodSettingPane.ClassType, Map<String, MethodItemHolder>> m: allMethods.entrySet()){
//            for (Map.Entry<String, MethodItemHolder> e: m.getValue().entrySet()){
//                ClassModel.Method method = null;
//                MethodSettingPane.MethodSettingModel methodSettingModel = e.getValue().panel.getModel();
//                ClassModel inputClass = new ClassModel(methodSettingModel.getInputClassName());
//                inputClass.setFields( methodSettingModel.getInputFields() );
//                inputClass.setRefName(FieldUtils.INSTANCE.getRefName(inputClass.getClassName()));
//                ClassModel outputClass = new ClassModel(methodSettingModel.getOutputClassName());
//                outputClass.setFields(methodSettingModel.getOutputFields());
//                outputClass.setRefName(FieldUtils.INSTANCE.getRefName(outputClass.getClassName()));
//
//                if (m.getKey() == MethodSettingPane.ClassType.CTRL){
//                    method = new CtrlClass.Method(methodSettingModel.getMethodName(), inputClass, outputClass, methodSettingModel.getOutputListTypeFlag() );
//                    method.setPaged(method.getPaged());
//                    ( (CtrlClass.Method)method).setRequest(new CtrlClass.Request(methodSettingModel.getPath(), methodSettingModel.getHttpMethod()));
//                    ctrlMethods.add(method);
//                    if ( !args.containsKey(inputClass.getClassName()) ) {
//                        args.put(inputClass.getClassName(), inputClass);
//                    }
//                    if ( !"-".equals(outputClass.getClassName()) && !results.containsKey(outputClass.getClassName()) && !entities.containsKey(outputClass.getClassName())) {
//                        results.put(outputClass.getClassName(), outputClass);
//                    }
//                }
//                if (m.getKey() == MethodSettingPane.ClassType.SVC){
//                    method = new SvcClass.Method(methodSettingModel.getMethodName(), inputClass, outputClass, methodSettingModel.getOutputListTypeFlag() );
//                    svcMethods.add(method);
//                    if ( !args.containsKey(inputClass.getClassName()) && !entities.containsKey(inputClass.getClassName())) {
//                        entities.put(inputClass.getClassName(), inputClass);
//                    }
//                    if (  !entities.containsKey(outputClass.getClassName())) {
//                        entities.put(outputClass.getClassName(), outputClass);
//                    }
//                    if (results.containsKey(outputClass.getClassName())){
//                        filterResults.add(outputClass.getClassName());
//
//                    }
//                }
//                if (m.getKey() == MethodSettingPane.ClassType.DAO){
//                    method = new DaoClass.Method(methodSettingModel.getMethodName(), inputClass, outputClass, methodSettingModel.getOutputListTypeFlag() );
//                    ((DaoClass.Method) method).setSqlDataFields(methodSettingModel.getSqlDataFields());
//                    ((DaoClass.Method) method).setSqlCondFields(methodSettingModel.getSqlCondFields());
//                    daoMethods.add(method);
//                    if ( !args.containsKey(inputClass.getClassName())
//                            && !entities.containsKey(inputClass.getClassName())
//                    && !daoArgs.containsKey(inputClass.getClassName())) {
//                        daoArgs.put(inputClass.getClassName(), inputClass);
//                    }
//                    if ( !results.containsKey(outputClass.getClassName())
//                            && !entities.containsKey(outputClass.getClassName()) && !daoResults.containsKey(outputClass.getClassName())) {
//                        daoResults.put(outputClass.getClassName(), outputClass);
//                    }
//                }
//                //确保results引用统一模型
//                if ( results.containsKey(outputClass.getClassName()) ) {
//                    method.setOutputClass(results.get(outputClass.getClassName()));
//                }
//                //确保entities引用统一模型
//                if ( entities.containsKey(inputClass.getClassName()) ) {
//                    method.setInputClass(entities.get(inputClass.getClassName()));
//                }
//                if ( args.containsKey(inputClass.getClassName()) ) {
//                    method.setInputClass(args.get(inputClass.getClassName()));
//                }
//                if ( entities.containsKey(outputClass.getClassName()) ) {
//                    method.setOutputClass(entities.get(outputClass.getClassName()));
//                }
//                method.setComment(methodSettingModel.getComment());
//                method.setType(methodSettingModel.getMethodType());
//                method.setInputListFlag(methodSettingModel.getInputListTypeFlag());
//                method.setPaged(methodSettingModel.getOutputPaged());
//                e.getValue().method = method;
//            }
//        }
//        for (Map.Entry<MethodSettingPane.ClassType, Map<String, MethodItemHolder>> m: allMethods.entrySet()){
//            for (Map.Entry<String, MethodItemHolder> e: m.getValue().entrySet()){
//                if (e.getValue().dependency != null) {
//                    e.getValue().method.setDependency(e.getValue().dependency.method);
//                }
//            }
//        }
//        filterResults.forEach(e -> results.remove(e));
//        result.setArgs(args.values().stream().toList());
//        result.setResults(results.values().stream().toList());
//        result.setEntities(entities.values().stream().collect(Collectors.toList()));
//        ctrlClass.setMethods(ctrlMethods);
//        svcClass.setMethods(svcMethods);
//        daoClass.setMethods(daoMethods);

        return result;
    }
    static class MethodItemHolder {
        public MethodSettingPane panel;
        public ClassModel.Method method;
        public MethodItemHolder dependency;
        public MethodItemHolder caller;
    }
}
