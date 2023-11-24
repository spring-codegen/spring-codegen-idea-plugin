package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;

import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyinghui
 * @date 2023/11/24
 */
public class MethodContainerPanel {
    private JPanel content;
    private Map<String, Map<String, ItemHolder>> allMethods = new LinkedHashMap<>();
    private static int COLUMN_NUM = 3;
    private static int PADDING_WIDTH = 20;
    public static int METHOD_TYPE_CTRL = 0;
    public static int METHOD_TYPE_SVC = 1;
    public static int METHOD_TYPE_DAO = 2;
    public MethodContainerPanel(){
        content.setLayout(null);
    }
    public void addClassMethod(String clsName, String methodName){
        if ( !allMethods.containsKey(clsName) ){
            allMethods.put(clsName, new LinkedHashMap<>());
        }
        ItemHolder holder = allMethods.get(clsName).get(methodName);

        //数据缓存
        CodeCfg.MethodDefine methodDefine = new CodeCfg.MethodDefine();
        holder.define = methodDefine;

        //界面
        int w = this.content.getWidth();
        holder.panel = new MethodCfgPanel();
        holder.panel.getContent().setLocation();
        this.content.add(holder.panel.getContent());
    }
    static class ItemHolder{
        public MethodCfgPanel panel;
        public CodeCfg.MethodDefine define;
    }
}
