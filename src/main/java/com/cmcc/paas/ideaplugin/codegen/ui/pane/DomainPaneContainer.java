package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DomainClassPane classPane = new DomainClassPane();
        classPane.setClassModel(classModel);

        if ( domainType == DomainType.ARG){
            argDomainContainer.add(classPane.getContent());
        }
        if ( domainType == DomainType.ENTITY){
            entityDomainContainer.add(classPane.getContent());
        }
        if ( domainType == DomainType.RESULT){
            resultDomainContainer.add(classPane.getContent());
        }
    }

    public JPanel getContent() {
        return content;
    }
}
