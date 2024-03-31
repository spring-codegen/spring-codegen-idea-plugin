package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.gen.ModelResult;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MethodFactory;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.model.CtrlClass;
import com.cmcc.paas.ideaplugin.codegen.gen.model.DaoClass;
import com.cmcc.paas.ideaplugin.codegen.gen.model.SvcClass;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.CtrlMethodSettingPane;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.DaoMethodSettingPane;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.MethodSettingPane;
import com.cmcc.paas.ideaplugin.codegen.ui.pane.SvcMethodSettingPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.cmcc.paas.ideaplugin.codegen.notify.NotificationType.MODEL_ADDED;
import static com.cmcc.paas.ideaplugin.codegen.notify.NotificationType.MODEL_UPDATED;

/**
 * @author zhangyinghui
 * @date 2023/11/24
 */
public class MethodContainerPane {
    private JScrollPane scrollPane;
    private MethodContainerBackgroundPane container;
    private Map<MvcClassType, Map<String, MethodItemHolder>> allMethods = new LinkedHashMap<>();
    private static int COLUMN_NUM = 3;
    private static int ITEM_MARGIN_H = 50;
    private static int ITEM_MARGIN_V = 20;
    private static int CONTAINER_PADDING_RIGHT = 30;
    private static int ITEM_HEIGHT = 240;


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
        NotificationCenter.Handler h = msg ->{
            allMethods.entrySet().forEach(e->{
                e.getValue().entrySet().forEach(e2 ->{
                    e2.getValue().panel.resetArgComboBox();
                    e2.getValue().panel.resetReturnComboBox();
                });
            });
        };
        NotificationCenter.INSTANCE.register(MODEL_ADDED, h);
        NotificationCenter.INSTANCE.register(MODEL_UPDATED, h);
    }
    public CodeCfg.MethodCfg getMethodCfg(MvcClassType classType, String methodName){
        for(CodeCfg.MethodCfg m : CodeCfg.getInstance().getMethods()){
            if (
                    MvcClassType.valueOf(m.getType()) == classType
                    && methodName.indexOf(m.getName()) >= 0
            ){
                return m;
            }
        }
        return null;
    }

//    private List<ClassModel.Field> getDefaultFields(String excludes, String includes){
//        List<ClassModel.Field> allowFields = new ArrayList<>();
//        AppCtx.INSTANCE.getCurrentTable().getFields().forEach(field -> {
//            if (StringUtils.isNotEmpty(excludes)){
//                boolean isExclude = Arrays.stream(excludes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
//                if (isExclude){
//                    return;
//                }
//            }
//
//            ClassModel.Field f = new ClassModel.Field(FieldUtils.INSTANCE.propertyName(field.getName()), FieldUtils.INSTANCE.javaType(field.getType()), field.getComment(), field.getNotNull(),null, null);
//            f.setColumn(field.getName());
//            if (field.getComment() != null && field.getComment().startsWith("JSON:")){
//                f.setJavaType("Map");
//            }
//            if (field.getMaxLen() != null && field.getMaxLen() > 4) {
//                f.setMaxLen(field.getMaxLen() - 4);
//            }
//
//            if (StringUtils.isNotEmpty(includes)){
//                boolean isInclude = Arrays.stream(includes.split(",")).filter(p -> Pattern.matches(p, field.getName())).findFirst().isPresent();
//                if (isInclude){
//                    allowFields.add(f);
//                    return;
//                }
//                return;
//            }
//            allowFields.add( f );
//        });
//        return allowFields;
//    }
//    private String getHandledVar(String v, Map<String, Object> p){
//        if (v == null){
//            return v;
//        }
//        String r = v;
//        for (String k : p.keySet()){
//            r = r.replaceAll("\\{\\s*"+k+"\\s*\\}",  p.get(k).toString());
//        }
//        return r;
//    }
//    public MethodSettingPane.MethodSettingModel getDefaultMethodCfgModel(MvcClassType classType, String methodType, String methodName){
//        Map p = AppCtx.INSTANCE.getENV();
//        DBTable dbTable = AppCtx.INSTANCE.getCurrentTable();
//        p.put("entityName", dbTable.getComment() == null ? dbTable.getName() : dbTable.getComment());
//        String className = MvcClassCtx.INSTANCE.getClassByType(classType).getClassName();
//        CodeCfg.MethodCfg methodCfg = getMethodCfg(classType, methodType);
//
//        MethodSettingPane.MethodSettingModel model = new MethodSettingPane.MethodSettingModel();
//        model.setClassName(className);
//        model.setMethodName(methodName);
//        model.setClassType(classType);
//        if (methodCfg.getRequest() != null) {
//            model.setPath(methodCfg.getRequest().getPath());
//            model.setHttpMethod(methodCfg.getRequest().getHttpMethod());
//        }
//        model.setMethodType(methodCfg.getName());
//        model.setComment(getHandledVar(methodCfg.getComment(), p));
//        model.setDbTableFields(dbTable.getFields());
//        List<MethodSettingPane.MethodSettingModel.MethodArgModel> args = new ArrayList<>();
//        for(CodeCfg.MethodArgCfg arg : methodCfg.getArgs()){
//            MethodSettingPane.MethodSettingModel.MethodArgModel methodArgModel = MethodSettingPane.MethodSettingModel.MethodArgModel.of(
//                    getHandledVar(arg.getClassName(), AppCtx.INSTANCE.getENV()),
//                    arg.getRefName(),
//                    arg.getListTypeFlag(),
//                    arg.isPathVar()
//                    );
//            methodArgModel.setRefName(arg.getRefName());
//            methodArgModel.setComment(arg.getComment());
//            methodArgModel.setClassModel(DomainModelCtx.INSTANCE.getClassModelByName(methodArgModel.getClassName()));;
//            args.add(methodArgModel);
//        }
//        model.setArgs(args);
//        if (methodCfg.getResult() != null){
//            MethodSettingPane.MethodSettingModel.MethodResultModel methodResultModel = MethodSettingPane.MethodSettingModel.MethodResultModel.of(
//                    getHandledVar(methodCfg.getResult().getClassName(), AppCtx.INSTANCE.getENV()),
//                    methodCfg.getResult().getRefName(),
//                    methodCfg.getResult().getListTypeFlag(),
//                    methodCfg.getResult().getOutputPaged());
//            methodResultModel.setRefName(methodCfg.getResult().getRefName());
//            methodResultModel.setComment(methodCfg.getResult().getComment());
//            methodResultModel.setClassModel(DomainModelCtx.INSTANCE.getClassModelByName(methodResultModel.getClassName()));
//            model.setResult(methodResultModel);
//        }
//
//
//
//        model.setSqlDataFields(getDefaultFields(methodCfg.getSqlDataFieldExcludes(), methodCfg.getSqlDataFieldIncludes()));
//        model.setSqlCondFields(getDefaultFields(methodCfg.getSqlConditionFieldExcludes(), methodCfg.getSqlConditionFieldIncludes()));
//
//        return model;
//    }
    public MethodItemHolder addClassMethod(MvcClassType classType,ClassModel.Method method){
        MvcClassType k = classType;
        if ( !allMethods.containsKey(k) ){
            allMethods.put(k, new LinkedHashMap<>());
        }
        MethodItemHolder holder = allMethods.get(k).get(method.getName());
        if (holder == null){
            holder = new MethodItemHolder();
            allMethods.get(k).put(method.getName(), holder);
        }

        //数据缓存

        int row = allMethods.get(k).size();

        int w = container.getWidth()/COLUMN_NUM - ITEM_MARGIN_H;
        int x = classType.ordinal() * (w + ITEM_MARGIN_H);
        int y = (row-1) * (ITEM_HEIGHT + ITEM_MARGIN_H);
        //界面
        if ( classType == MvcClassType.DAO  ){
            holder.panel = new DaoMethodSettingPane();
        }else if ( classType == MvcClassType.SVC  ){
            holder.panel = new SvcMethodSettingPane();
        }else{
            holder.panel =new CtrlMethodSettingPane();
        }
        holder.panel.getContent().setSize(w, ITEM_HEIGHT);
        holder.panel.getContent().setLocation(x,y);

        holder.panel.setMethod(method);
        this.container.add(holder.panel.getContent());
        holder.panel.setMethodCfgPaneActionListener(new MethodSettingPane.MethodCfgPaneActionListener() {
            @Override
            public void onClose(MethodSettingPane methodSettingPane) {
                removePane(methodSettingPane);
            }
        });
        MvcClassCtx.INSTANCE.addMethod(classType, method);
        return holder;
    }
    public void removePane(MethodSettingPane methodSettingPane){
        MvcClassType classType = methodSettingPane.getClassType();
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

            MvcClassCtx.INSTANCE.removeMethod(classType, holder.method.getName());
        }
    }
    public void createMethod(String methodType, String methodName, Boolean ctrlChecked, Boolean svcChecked, Boolean daoChecked){
        BiFunction<MvcClassType, String, ClassModel.Method> f = (classType, mName) ->{
          return   MethodFactory.INSTANCE.createMethod(mName, classType, getMethodCfg( classType, methodType));
        };
        ClassModel.Method ctrlMethod = ctrlChecked ? f.apply( MvcClassType.CTRL, methodName) : null;
        ClassModel.Method svcMethod = svcChecked ? f.apply( MvcClassType.SVC, methodName) : null;;
        ClassModel.Method daoMethod = daoChecked ? f.apply( MvcClassType.DAO, methodName) : null;
        if (ctrlMethod != null && svcMethod != null){
            ctrlMethod.setDependency(svcMethod);
        }
        if (  svcMethod != null && daoMethod != null){
            svcMethod.setDependency(daoMethod);
        }

        MethodItemHolder ctrlMethodHolder = ctrlChecked ?  addClassMethod(MvcClassType.CTRL, ctrlMethod) : null;
        MethodItemHolder svcMethodHolder = svcChecked ?  addClassMethod(MvcClassType.SVC,svcMethod) : null;
        MethodItemHolder daoMethodHolder = daoChecked ?  addClassMethod(MvcClassType.DAO, daoMethod) : null;
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
        for (MvcClassType t : MvcClassType.values()){
            if (allMethods.get(t) == null){
                continue;
            }
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
        for (MvcClassType t : MvcClassType.values()){
            if ( allMethods.get(t) == null){
                continue;
            }
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
    static class MethodItemHolder {
        public MethodSettingPane panel;
        public ClassModel.Method method;
        public MethodItemHolder dependency;
        public MethodItemHolder caller;
    }
}
