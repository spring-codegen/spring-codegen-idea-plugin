package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.define.model.ClassModel;

import javax.swing.*;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2023/12/22
 */
public interface MethodCfgPane {
    public JPanel getContent();
    public void setModel(MethodCfgModel model);
    public MethodCfgModel getModel();

    public static enum ClassType {
        CTRL,
        SVC,
        DAO;
    }
    static class MethodCfgModel{
        private String methodName;
        private String className;
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
        private List<ClassModel.Field> sqlConditionFields;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
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

        public List<ClassModel.Field> getSqlConditionFields() {
            return sqlConditionFields;
        }

        public void setSqlConditionFields(List<ClassModel.Field> sqlConditionFields) {
            this.sqlConditionFields = sqlConditionFields;
        }
    }
}
