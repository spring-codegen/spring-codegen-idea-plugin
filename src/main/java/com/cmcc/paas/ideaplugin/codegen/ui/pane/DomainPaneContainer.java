package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.config.CodeCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.util.CodeGenUtils;
import com.cmcc.paas.ideaplugin.codegen.util.StringUtils;
import com.intellij.uiDesigner.core.GridConstraints;

import javax.swing.*;
import java.awt.*;
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
    private Map<DomainType, List<ClassModel>> modelMaps = new HashMap<>();
    private List<DBTableField> dbTableFields;
    private List<CodeCfg.ModelCfg> modelCfgs;
    private Color[] colors = new Color[]{Color.decode("#585C5F"),Color.decode("#4A4E50")};

    public DomainPaneContainer(){
    }
    public List<DBTableField> getDbTableFields() {
        return dbTableFields;
    }

    public Map<DomainType, List<ClassModel>> getModelMaps() {
        return modelMaps;
    }

    public void setDbTableFields(List<DBTableField> dbTableFields) {
        this.dbTableFields = dbTableFields;
        Map p = AppCtx.INSTANCE.getENV();
        modelMaps.clear();
        Arrays.stream(new JComponent[]{argDomainContainer, entityDomainContainer, resultDomainContainer}).forEach(e->e.removeAll());
        modelCfgs.forEach(e -> {
            ClassModel cls = new ClassModel(StringUtils.INSTANCE.replacePlaceholders(e.getClassName(), p));
            List<ClassModel.Field> fields = CodeGenUtils.INSTANCE.getDefaultFields(dbTableFields, e.getFieldIncludes(), e.getFieldExcludes());
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
        if ( !modelMaps.containsKey(domainType) ){
            modelMaps.put(domainType, new ArrayList<ClassModel>());
        }
        boolean isExists = modelMaps.get(domainType)
                .stream()
                .filter( e-> e.getClassName().equalsIgnoreCase(classModel.getClassName()))
                        .count() > 0;
        if (isExists){
            return;
        }
        modelMaps.get(domainType).add(classModel);
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
