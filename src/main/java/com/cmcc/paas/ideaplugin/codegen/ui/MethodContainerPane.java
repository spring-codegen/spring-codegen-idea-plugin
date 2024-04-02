package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MethodFactory;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.MvcClassCtx;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationType;
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

import static com.cmcc.paas.ideaplugin.codegen.notify.NotificationType.*;

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
                    e2.getValue().pane.resetArgComboBox();
                    e2.getValue().pane.resetReturnComboBox();
                });
            });
        };
        NotificationCenter.INSTANCE.register(MODEL_ADDED, h);
        NotificationCenter.INSTANCE.register(MODEL_UPDATED, h);
        NotificationCenter.INSTANCE.register(MODEL_REMOVED, h);
        NotificationCenter.INSTANCE.register(NotificationType.MVC_CLASS_UPDATED, msg -> {
            allMethods.entrySet().forEach(e->{
                e.getValue().entrySet().forEach(e2 ->{
                    e2.getValue().pane.onClassUpdated();
                });
            });
        });
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
    private MethodItemHolder createMethodPane(MvcClassType classType, ClassModel.Method method){
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
            holder.pane = new DaoMethodSettingPane();
        }else if ( classType == MvcClassType.SVC  ){
            holder.pane = new SvcMethodSettingPane();
        }else{
            holder.pane =new CtrlMethodSettingPane();
        }
        holder.pane.getContent().setSize(w, ITEM_HEIGHT);
        holder.pane.getContent().setLocation(x,y);

        holder.pane.setMethod(method);
        this.container.add(holder.pane.getContent());
        holder.pane.setMethodCfgPaneActionListener(methodSettingPane -> {
                removePane(methodSettingPane);
        });
        return holder;
    }
    public void removePane(MethodSettingPane methodSettingPane){
        MvcClassType classType = methodSettingPane.getClassType();
        Map<String, MethodItemHolder> m = allMethods.get(classType);
        Optional<Map.Entry<String, MethodItemHolder>> x =  m.entrySet().stream().filter(e -> e.getValue().pane == methodSettingPane).findFirst();
        if (x.isPresent()){
            Map.Entry<String, MethodItemHolder> e = x.get();
            MethodItemHolder holder = e.getValue();
            this.container.remove(holder.pane.getContent());
            if (holder.callee != null){
                holder.callee.caller = null;
            }
            if (holder.caller != null){
                holder.caller.callee = null;
                holder.caller.pane.getMethod().setDependency(null);
            }
            m.remove(e.getKey());
            this.resize();

            MvcClassCtx.INSTANCE.removeMethod(classType, holder.pane.getMethod().getName());
        }
    }
    public void createMethod(String methodType, String methodName, Boolean ctrlChecked, Boolean svcChecked, Boolean daoChecked){
        ClassModel.Method ctrlMethod = null, svcMethod = null, daoMethod = null;
        MethodItemHolder ctrlMethodHolder = null, svcMethodHolder = null, daoMethodHolder = null;
        if(ctrlChecked){
            ctrlMethod = MethodFactory.createMethod(methodName, MvcClassType.CTRL, methodType);
            ctrlMethodHolder = createMethodPane(MvcClassType.CTRL, ctrlMethod);
            MvcClassCtx.INSTANCE.addMethod(MvcClassType.CTRL, ctrlMethod);
        }
        if (svcChecked){
            svcMethod = MethodFactory.createMethod(methodName, MvcClassType.SVC, methodType);
            svcMethodHolder = createMethodPane(MvcClassType.SVC,svcMethod);
            MvcClassCtx.INSTANCE.addMethod(MvcClassType.SVC, svcMethod);
        }
        if (daoChecked){
            daoMethod = MethodFactory.createMethod(methodName, MvcClassType.DAO, methodType);
            daoMethodHolder = createMethodPane(MvcClassType.DAO, daoMethod);
            MvcClassCtx.INSTANCE.addMethod(MvcClassType.DAO, daoMethod);
        }
        if (ctrlChecked  && svcChecked){
            ctrlMethod.setDependency(svcMethod);
            ctrlMethodHolder.callee = svcMethodHolder;
            svcMethodHolder.caller = ctrlMethodHolder;
        }
        if (  svcChecked  && daoChecked){
            svcMethod.setDependency(daoMethod);
            svcMethodHolder.callee = daoMethodHolder;
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
                e.getValue().pane.getContent().setBounds(x, y, w, ITEM_HEIGHT);
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
                if (h.callee != null){
                    int offsetX = h.pane.getContent().getWidth();
                    int offsetY = h.callee.pane.getContent().getHeight()/2;
                    Point p1 = h.pane.getContent().getLocation();
                    Point p2 = h.callee.pane.getContent().getLocation();
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
        public MethodSettingPane pane;
        public MethodItemHolder callee;
        public MethodItemHolder caller;
    }
}
