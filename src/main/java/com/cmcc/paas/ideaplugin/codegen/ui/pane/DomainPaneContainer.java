package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.DomainModels;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter;
import com.cmcc.paas.ideaplugin.codegen.ui.BeanFieldSelectionDialog;
import com.cmcc.paas.ideaplugin.codegen.ui.consts.NotificationType;
import com.cmcc.paas.ideaplugin.codegen.util.CodeGenUtils;
import com.cmcc.paas.ideaplugin.codegen.util.StringUtils;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2024/3/18
 */
public class DomainPaneContainer {
    private JPanel content;
    private JPanel argDomainContainer;
    private JPanel entityDomainContainer;
    private JPanel resultDomainContainer;
    private JButton addArgButton;
    private JButton addEntityButton;
    private JButton addResultButton;
    private List<CodeCfg.ModelCfg> modelCfgs;
    private Color[] colors = new Color[]{Color.decode("#585C5F"),Color.decode("#4A4E50")};

    public DomainPaneContainer(){
        ActionListener listener = new ActionListener() {
            /**
             * @param actionEvent
             */
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                DomainType domainType = DomainType.ARG;
                if (actionEvent.getSource() == addEntityButton){
                    domainType = DomainType.ENTITY;
                }
                if (actionEvent.getSource() == addResultButton){
                    domainType = DomainType.RESULT;
                }
                BeanFieldSelectionDialog dialog = new BeanFieldSelectionDialog();
                dialog.setUserInfo(domainType);
                dialog.setFields( AppCtx.INSTANCE.getCurrentTable().getFields() );
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        ClassModel cls = new ClassModel(dialog.getClassName());
                        cls.setFields(dialog.getSelectedFields());
                        addClassModel((DomainType) dialog.getUserInfo(), cls);
                        NotificationCenter.INSTANCE.sendMessage(NotificationType.MODEL_ADDED, cls);
                    }
                });
                dialog.setVisible(true);
            }
        };
        addArgButton.addActionListener(listener);
        addEntityButton.addActionListener(listener);
        addResultButton.addActionListener(listener);
    }

    public void reset() {
        List<DBTableField> tableFields = AppCtx.INSTANCE.getCurrentTable().getFields();
        Map p = AppCtx.INSTANCE.getENV();
        DomainModels.INSTANCE.clear();
        Arrays.stream(new JComponent[]{argDomainContainer, entityDomainContainer, resultDomainContainer}).forEach(e->e.removeAll());
        modelCfgs.forEach(e -> {
            ClassModel cls = new ClassModel(StringUtils.INSTANCE.replacePlaceholders(e.getClassName(), p));
            List<ClassModel.Field> fields = CodeGenUtils.INSTANCE.getDefaultFields(tableFields, e.getFieldIncludes(), e.getFieldExcludes());
            cls.setFields(fields);
            addClassModel(DomainType.valueOf(e.getType()), cls);
        });
    }

    public List<CodeCfg.ModelCfg> getModelCfgs() {
        return modelCfgs;
    }

    public void setModelCfgs(List<CodeCfg.ModelCfg> modelCfgs) {
        this.modelCfgs = modelCfgs;
    }

    public void addClassModel(DomainType domainType, ClassModel classModel){
        DomainModels.INSTANCE.addModel(domainType, classModel);
        if (ClassModel.isInnerClass(classModel.getClassName())){
            return;
        }
        DomainClassPane classPane = new DomainClassPane();
        classPane.setClassModel(classModel);
        JPanel domainContainer = domainType == DomainType.ARG ?  argDomainContainer :  (domainType == DomainType.ENTITY) ? entityDomainContainer : resultDomainContainer;
        int n = domainContainer.getComponentCount() ;
        domainContainer.setLayout(new GridLayout(n+1, 1, 2, 3));
        GridConstraints c = new GridConstraints();
        c.setColumn(0);
        c.setRow( n);
        domainContainer.add( classPane.getContent(), c );
        classPane.getContent().setBackground(colors[n%2]);

    }

    public JPanel getContent() {
        return content;
    }
}
