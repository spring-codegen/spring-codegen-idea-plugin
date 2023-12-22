package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.ModelResult;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.CtrlClass;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.DaoClass;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.SvcClass;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author zhangyinghui
 * @date 2023/11/24
 */
public class MethodContainerPane {
    private JScrollPane scrollPane;
    private MethodContainerBackgroundPane container;
    private Map<MethodCfgPane.ClassType, Map<String, MethodItemHolder>> allMethods = new LinkedHashMap<>();
    private static int COLUMN_NUM = 3;
    private static int ITEM_MARGIN_H = 50;
    private static int ITEM_MARGIN_V = 20;
    private static int CONTAINER_PADDING_RIGHT = 30;
    private static int ITEM_HEIGHT = 200;
    private CodeCfg codeCfg;
    private List<DBTableField> dbTableFields;
    private CtrlClass ctrlClass = null;
    private SvcClass svcClass =  null;
    private DaoClass daoClass =  null;

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

    public MethodContainerPane(){
//        content.setLayout(null);
//        content.setBackground(Color.red);
        GridBagConstraints constraints = new GridBagConstraints();
        container = new MethodContainerBackgroundPane();
//        container.setBackground(Color.blue);
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
    public CodeCfg.MethodCfg getMethodCfg(MethodCfgPane.ClassType classType, String methodName){
        for(CodeCfg.MethodCfg m : codeCfg.getMethods()){
            if (MethodCfgPane.ClassType.valueOf(m.getType()) == classType && methodName.indexOf(m.getName()) >= 0){
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

            if (StringUtils.isNotEmpty(includes)){
                boolean isInclude = Arrays.stream(includes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
                if (isInclude){
                    ClassModel.Field f = new ClassModel.Field(field.getName(), field.getType(), field.getComment(), field.getNotNull(),null, null);
                    f.setColumn(field.getName());
                    allowFields.add(f);
                    return;
                }
                return;
            }
            ClassModel.Field f = new ClassModel.Field(field.getName(), field.getType(), field.getComment(), field.getNotNull(),null, null);
            f.setColumn(field.getName());
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
    public SvcMethodCfgPane.MethodCfgModel getDefaultMethodCfgModel(MethodCfgPane.ClassType classType, String methodName){
        String className = classType == MethodCfgPane.ClassType.CTRL ? ctrlClass.getClassName() : classType == MethodCfgPane.ClassType.SVC ? svcClass.getClassName() : daoClass.getClassName();
        CodeCfg.MethodCfg methodCfg = getMethodCfg(classType, methodName);
        SvcMethodCfgPane.MethodCfgModel model = new SvcMethodCfgPane.MethodCfgModel();
        model.setClassName(className);
        model.setMethodName(methodName);
        model.setClassType(classType);
        model.setMethodType(methodCfg.getName());
        model.setDbTableFields(getDbTableFields());
        model.setInputClassName(getHandledVar(methodCfg.getInputClassName(), AppCtx.INSTANCE.getENV()));
        model.setInputFields(getDefaultFields(methodCfg.getInputFieldExcludes(), methodCfg.getInputFieldIncludes()));
        model.setInputListTypeFlag(methodCfg.getInputListTypeFlag());

        model.setOutputClassName(getHandledVar(methodCfg.getOutputClassName(), AppCtx.INSTANCE.getENV()));
        model.setOutputListTypeFlag(methodCfg.getOutputListTypeFlag());
        model.setOutputPaged(methodCfg.getOutputPaged());
        model.setOutputFields(getDefaultFields(methodCfg.getOutputFieldExcludes(), methodCfg.getOutputFieldIncludes()));



        model.setSqlDataFields(getDefaultFields(methodCfg.getSqlDataFieldExcludes(), methodCfg.getSqlDataFieldIncludes()));
        model.setSqlConditionFields(getDefaultFields(methodCfg.getSqlConditionFieldExcludes(), methodCfg.getSqlConditionFieldIncludes()));

        return model;
    }
    public MethodItemHolder addClassMethod(MethodCfgPane.ClassType classType, String methodName){
        MethodCfgPane.ClassType k = classType;
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
        if ( classType == MethodCfgPane.ClassType.DAO  ){
            holder.panel = new DaoMethodCfgPane();
        }else{
            holder.panel =new SvcMethodCfgPane();
        }
        holder.panel.getContent().setSize(w, ITEM_HEIGHT);
        holder.panel.getContent().setLocation(x,y);
        holder.panel.setModel(getDefaultMethodCfgModel(classType, methodName));
        this.container.add(holder.panel.getContent());
        return holder;
    }
    public void createMethod(String methodName, Boolean ctrlChecked, Boolean svcChecked, Boolean daoChecked){
        MethodItemHolder ctrlMethodHolder = ctrlChecked ?  addClassMethod(MethodCfgPane.ClassType.CTRL, methodName) : null;
        MethodItemHolder svcMethodHolder = svcChecked ?  addClassMethod(MethodCfgPane.ClassType.SVC, methodName) : null;
        MethodItemHolder daoMethodHolder = daoChecked ?  addClassMethod(MethodCfgPane.ClassType.DAO, methodName) : null;
        if (ctrlMethodHolder != null && svcMethodHolder != null){
            ctrlMethodHolder.dependency = svcMethodHolder;
        }
        if (daoMethodHolder != null && svcMethodHolder != null){
            svcMethodHolder.dependency = daoMethodHolder;
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
        for (MethodCfgPane.ClassType t : MethodCfgPane.ClassType.values()){
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
        for (MethodCfgPane.ClassType t : MethodCfgPane.ClassType.values()){
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
        result.setCtrlClass(ctrlClass);
        result.setSvcClass(svcClass);
        result.setDaoClass(daoClass);
        List<ClassModel.Method> ctrlMethods = new ArrayList<>();
        List<ClassModel.Method> svcMethods = new ArrayList<>();
        List<ClassModel.Method> daoMethods = new ArrayList<>();
        List<ClassModel> dtos = new ArrayList<>();
        List<ClassModel> bos = new ArrayList<>();
        for (Map.Entry<MethodCfgPane.ClassType, Map<String, MethodItemHolder>> m: allMethods.entrySet()){
            for (Map.Entry<String, MethodItemHolder> e: m.getValue().entrySet()){
                ClassModel.Method method = null;
                MethodCfgPane.MethodCfgModel methodCfgModel = e.getValue().panel.getModel();
                ClassModel dto = new ClassModel(methodCfgModel.getInputClassName());
                dto.setFields( methodCfgModel.getInputFields() );
                ClassModel bo = new ClassModel(methodCfgModel.getOutputClassName());
                bo.setFields(methodCfgModel.getOutputFields());
                dtos.add(dto);
                bos.add(bo);

                if (m.getKey() == MethodCfgPane.ClassType.CTRL){
                    dto.setFields( methodCfgModel.getInputFields() );
                    method = new CtrlClass.Method(methodCfgModel.getMethodName(), dto, bo, methodCfgModel.getOutputListTypeFlag() );
                    method.setPaged(method.getPaged());
                    method.setInputListFlag(methodCfgModel.getInputListTypeFlag());
                    ctrlMethods.add(method);
                }
                if (m.getKey() == MethodCfgPane.ClassType.SVC){
                    dto.setFields( methodCfgModel.getInputFields() );
                    method = new SvcClass.Method(methodCfgModel.getMethodName(), dto, bo, methodCfgModel.getOutputListTypeFlag() );
                    svcMethods.add(method);
                }
                if (m.getKey() == MethodCfgPane.ClassType.DAO){
                    dto.setFields( methodCfgModel.getInputFields() );
                    method = new DaoClass.Method(methodCfgModel.getMethodName(), dto, bo, methodCfgModel.getOutputListTypeFlag() );
                    daoMethods.add(method);
                }
            }
        }
        ctrlClass.setMethods(ctrlMethods);
        svcClass.setMethods(svcMethods);
        daoClass.setMethods(daoMethods);

        return result;
    }
    static class MethodItemHolder {
        public MethodCfgPane panel;
        public ClassModel.Method method;
        public MethodItemHolder dependency;
    }
}
