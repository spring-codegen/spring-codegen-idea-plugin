package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.cmcc.paas.ideaplugin.codegen.ui.consts.NotificationType.MODEL_UPDATED;


/**
 * @author zhangyinghui
 * @date 2023/12/22
 */
public abstract class MethodSettingPane {
    private Map<DomainType, List<ClassModel>> modelMaps;
    public abstract JPanel getContent();
    public abstract void setModel(MethodSettingModel model);
    public abstract JComboBox getResultParamComboBox();
    public abstract  ArgsSettingPane getArgsSettingPane();
    public abstract MethodSettingModel getModel();
    public void init(){
        NotificationCenter.INSTANCE.register(MODEL_UPDATED, new NotificationCenter.Handler() {
            @Override
            public void handleMessage(@NotNull Object msg) {
                setModelMaps((Map<DomainType, List<ClassModel>>) msg);
                resetResultParams();
            }
        });
    }

    private MethodCfgPaneActionListener methodCfgPaneActionListener;

    public MethodCfgPaneActionListener getMethodCfgPaneActionListener() {
        return methodCfgPaneActionListener;
    }

    public Map<DomainType, List<ClassModel>> getModelMaps() {
        return modelMaps;
    }


    public void setModelMaps(Map<DomainType, List<ClassModel>> modelMaps) {
        this.modelMaps = modelMaps;
        getArgsSettingPane().setModelMaps(modelMaps);
    }
    public void resetResultParams(){
        if (modelMaps == null){
            return;
        }
        JComboBox comboBox = getResultParamComboBox();
        List<ClassModel> classes = new ArrayList<>();
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement("-");
        modelMaps.entrySet().forEach(a -> {
            if(a.getKey() == DomainType.ARG){
                return;
            }
            a.getValue().forEach( cls -> comboBoxModel.addElement(cls.getClassName()));
        });
        comboBox.setModel(comboBoxModel);
        if (getModel() != null && getModel().getResult() != null) {
            comboBox.setSelectedItem(getModel().getResult().className);
        }
    }
    public void setMethodCfgPaneActionListener(MethodCfgPaneActionListener methodCfgPaneActionListener) {
        this.methodCfgPaneActionListener = methodCfgPaneActionListener;
    }
    public void setCloseBtnAction(JButton btn){

        MethodSettingPane self = this;
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (methodCfgPaneActionListener != null){
                    methodCfgPaneActionListener.onClose(self);
                }
            }
        });
    }

    public static enum ClassType {
        CTRL,
        SVC,
        DAO;
    }
    public static interface MethodCfgPaneActionListener{
        void onClose(MethodSettingPane methodSettingPane);
    }
    public static class MethodSettingModel {
        private String methodName;
        private String className;
        private String path;
        /**
         * add/remove/update
         */
        private String methodType;
        private String httpMethod;
        private String comment;
        private List<DBTableField> dbTableFields;
        private ClassType classType;
        private List<MethodArgModel> args;
        private MethodResultModel result;

        private List<ClassModel.Field> sqlDataFields;
        private List<ClassModel.Field> sqlCondFields;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodType() {
            return methodType;
        }

        public void setMethodType(String methodType) {
            this.methodType = methodType;
        }

        public List<MethodArgModel> getArgs() {
            return args;
        }

        public void setArgs(List<MethodArgModel> args) {
            this.args = args;
        }

        public List<DBTableField> getDbTableFields() {
            return dbTableFields;
        }

        public void setDbTableFields(List<DBTableField> dbTableFields) {
            this.dbTableFields = dbTableFields;
        }

        public ClassType getClassType() {
            return classType;
        }

        public void setClassType(ClassType classType) {
            this.classType = classType;
        }

        public MethodResultModel getResult() {
            return result;
        }

        public void setResult(MethodResultModel result) {
            this.result = result;
        }

        public List<ClassModel.Field> getSqlDataFields() {
            return sqlDataFields;
        }

        public void setSqlDataFields(List<ClassModel.Field> sqlDataFields) {
            this.sqlDataFields = sqlDataFields;
        }

        public List<ClassModel.Field> getSqlCondFields() {
            return sqlCondFields;
        }

        public void setSqlCondFields(List<ClassModel.Field> sqlCondFields) {
            this.sqlCondFields = sqlCondFields;
        }

        public static class MethodArgModel{
            private String className;
            private String refName;
            private Boolean isPathVar = false;
            private Boolean listTypeFlag = false;

            public String getClassName() {
                return className;
            }

            public MethodArgModel setClassName(String className) {
                this.className = className;
                return this;
            }

            public String getRefName() {
                return refName;
            }

            public MethodArgModel setRefName(String refName) {
                this.refName = refName;
                return this;
            }

            public Boolean getPathVar() {
                return isPathVar;
            }

            public MethodArgModel setPathVar(Boolean pathVar) {
                isPathVar = pathVar;
                return this;
            }

            public Boolean getListTypeFlag() {
                return listTypeFlag;
            }

            public MethodArgModel setListTypeFlag(Boolean listTypeFlag) {
                this.listTypeFlag = listTypeFlag;
                return this;
            }

            public static MethodArgModel of(String className, String refName, Boolean listTypeFlag, Boolean isPathVar){
                return new MethodArgModel()
                        .setClassName(className)
                        .setRefName(refName)
                        .setListTypeFlag(listTypeFlag)
                        .setPathVar(isPathVar);
            }
        }
        public static class MethodResultModel {
            private String className;
            private String refName;
            private Boolean outputPaged = false;
            private Boolean listTypeFlag = false;

            public String getClassName() {
                return className;
            }

            public MethodResultModel setClassName(String className) {
                this.className = className;
                return this;
            }

            public String getRefName() {
                return refName;
            }

            public MethodResultModel setRefName(String refName) {
                this.refName = refName;
                return this;
            }

            public Boolean getOutputPaged() {
                return outputPaged;
            }

            public MethodResultModel setOutputPaged(Boolean outputPaged) {
                this.outputPaged = outputPaged;
                return this;
            }

            public Boolean getListTypeFlag() {
                return listTypeFlag;
            }

            public MethodResultModel setListTypeFlag(Boolean listTypeFlag) {
                this.listTypeFlag = listTypeFlag;
                return this;
            }

            public static MethodResultModel of(String className, String refName, Boolean listTypeFlag, Boolean outputPaged){
                return new MethodResultModel()
                        .setClassName(className)
                        .setRefName(refName)
                        .setListTypeFlag(listTypeFlag)
                        .setOutputPaged(outputPaged);
            }
        }
    }
}
