package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2023/12/22
 */
public abstract class MethodCfgPane {
    public abstract JPanel getContent();
    public abstract void setModel(MethodCfgModel model);
    public abstract MethodCfgModel getModel();

    private MethodCfgPaneActionListener methodCfgPaneActionListener;

    public MethodCfgPaneActionListener getMethodCfgPaneActionListener() {
        return methodCfgPaneActionListener;
    }

    public void setMethodCfgPaneActionListener(MethodCfgPaneActionListener methodCfgPaneActionListener) {
        this.methodCfgPaneActionListener = methodCfgPaneActionListener;
    }
    public void setCloseBtnAction(JButton btn){

        MethodCfgPane self = this;
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
        void onClose(MethodCfgPane methodCfgPane);
    }
    public static class MethodCfgModel{
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
        private String inputClassName;
        private List<ClassModel.Field> inputFields;
        private Boolean inputListTypeFlag = false;

        private String outputClassName;
        private List<ClassModel.Field> outputFields;
        private Boolean outputListTypeFlag = false;
        private Boolean outputPaged = false;

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

        public String getInputClassName() {
            return inputClassName;
        }

        public void setInputClassName(String inputClassName) {
            this.inputClassName = inputClassName;
        }

        public List<ClassModel.Field> getInputFields() {
            return inputFields;
        }

        public void setInputFields(List<ClassModel.Field> inputFields) {
            this.inputFields = inputFields;
        }

        public Boolean getInputListTypeFlag() {
            return inputListTypeFlag;
        }

        public void setInputListTypeFlag(Boolean inputListTypeFlag) {
            this.inputListTypeFlag = inputListTypeFlag;
        }

        public String getOutputClassName() {
            return outputClassName;
        }

        public void setOutputClassName(String outputClassName) {
            this.outputClassName = outputClassName;
        }

        public List<ClassModel.Field> getOutputFields() {
            return outputFields;
        }

        public void setOutputFields(List<ClassModel.Field> outputFields) {
            this.outputFields = outputFields;
        }

        public Boolean getOutputListTypeFlag() {
            return outputListTypeFlag;
        }

        public void setOutputListTypeFlag(Boolean outputListTypeFlag) {
            this.outputListTypeFlag = outputListTypeFlag;
        }

        public Boolean getOutputPaged() {
            return outputPaged;
        }

        public void setOutputPaged(Boolean outputPaged) {
            this.outputPaged = outputPaged;
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
    }
}
