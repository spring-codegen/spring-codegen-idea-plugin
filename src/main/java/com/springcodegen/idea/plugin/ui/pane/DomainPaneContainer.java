package com.springcodegen.idea.plugin.ui.pane;

import com.springcodegen.idea.plugin.config.CodeCfg;
import com.springcodegen.idea.plugin.ctx.AppCtx;
import com.springcodegen.idea.plugin.constants.DomainType;
import com.springcodegen.idea.plugin.db.model.DBTableField;
import com.springcodegen.idea.plugin.gen.model.ClassModel;
import com.springcodegen.idea.plugin.ctx.DomainModelCtx;
import com.springcodegen.idea.plugin.notify.NotificationCenter;
import com.springcodegen.idea.plugin.ui.BeanFieldSelectionDialog;
import com.springcodegen.idea.plugin.notify.NotificationType;
import com.springcodegen.idea.plugin.util.CodeGenUtils;
import com.springcodegen.idea.plugin.util.StringUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.springcodegen.idea.plugin.constants.DomainType;
import com.springcodegen.idea.plugin.gen.model.ClassModel;

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
public class DomainPaneContainer implements DomainClassPane.DomainModelActionListener {
    private JPanel content;
    private JPanel argDomainContainer;
    private JPanel entityDomainContainer;
    private JPanel resultDomainContainer;
    private JButton addArgButton;
    private JButton addEntityButton;
    private JButton addResultButton;
    private Color[] colors = new Color[]{Color.decode("#585C5F"),Color.decode("#4A4E50")};

    public DomainPaneContainer(){
        ActionListener listener = actionEvent -> {
                DomainType domainType = DomainType.ARG;
                if (actionEvent.getSource() == addEntityButton){
                    domainType = DomainType.ENTITY;
                }
                if (actionEvent.getSource() == addResultButton){
                    domainType = DomainType.RESULT;
                }
                BeanFieldSelectionDialog dialog = new BeanFieldSelectionDialog();
                dialog.setUserInfo(domainType);
                dialog.setFields( AppCtx.getCurrentTable().getFields() );
                dialog.setActionListener(new BeanFieldSelectionDialog.BeanFieldSelectionActionListener() {
                    @Override
                    public void onFieldSelected(BeanFieldSelectionDialog dialog) {
                        ClassModel cls = DomainModelCtx.INSTANCE.createModel(dialog.getClassName());
                        cls.setFields(dialog.getSelectedFields());
                        DomainType type = (DomainType) dialog.getUserInfo();
                        DomainModelCtx.INSTANCE.addModel(type, cls);
                        addClassModel(type, cls);
                    }
                });
                dialog.setVisible(true);
        };
        addArgButton.addActionListener(listener);
        addEntityButton.addActionListener(listener);
        addResultButton.addActionListener(listener);
    }
    public void reset(){
        DomainModelCtx.INSTANCE.reset();
        Arrays.stream(new JComponent[]{argDomainContainer, entityDomainContainer, resultDomainContainer}).forEach(e->e.removeAll());

        DomainModelCtx.INSTANCE.getAllTypes().forEach(domainType ->{
            DomainModelCtx.INSTANCE.getModesByType(domainType).forEach(cls ->{
                addClassModel(domainType, cls);
            });
        });

        SwingUtilities.invokeLater(()->{
            SwingUtilities.invokeLater(() -> {
                content.revalidate();
                content.repaint();
            });
        });
    }
    public void addClassModel(DomainType domainType, ClassModel classModel){
        if (DomainModelCtx.INSTANCE.isInnerClass(classModel.getClassName())){
            return;
        }
        DomainClassPane classPane = new DomainClassPane(domainType, classModel);
        JPanel domainContainer = domainType == DomainType.ARG ?  argDomainContainer :  (domainType == DomainType.ENTITY) ? entityDomainContainer : resultDomainContainer;
        int n = domainContainer.getComponentCount() ;
        domainContainer.setLayout(new GridLayout(n+1, 1, 2, 3));
        GridConstraints c = new GridConstraints();
        c.setColumn(0);
        c.setRow( n);
        domainContainer.add( classPane.getContent(), c );
        classPane.getContent().setBackground(colors[n%2]);
        classPane.setActionListener(this);
    }

    public JPanel getContent() {
        return content;
    }

    /**
     * @param domainClassPane
     */
    @Override
    public void onDomainClassRemove(DomainClassPane domainClassPane) {
        DomainModelCtx.INSTANCE.removeModel(domainClassPane.getClassModel().getClassName());
        domainClassPane.getContent().getParent().remove(domainClassPane.getContent());
        NotificationCenter.sendMessage(NotificationType.MODEL_REMOVED, domainClassPane.getClassModel());
    }

    /**
     * @param domainClassPane
     */
    @Override
    public void onDomainClassAlter(DomainClassPane domainClassPane) {

    }
}
