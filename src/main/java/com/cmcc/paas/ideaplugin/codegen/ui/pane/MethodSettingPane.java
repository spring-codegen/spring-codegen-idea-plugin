package com.cmcc.paas.ideaplugin.codegen.ui.pane;

import com.cmcc.paas.ideaplugin.codegen.constants.DomainType;
import com.cmcc.paas.ideaplugin.codegen.constants.MvcClassType;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTableField;
import com.cmcc.paas.ideaplugin.codegen.gen.model.ClassModel;
import com.cmcc.paas.ideaplugin.codegen.gen.ctx.DomainModelCtx;
import com.cmcc.paas.ideaplugin.codegen.notify.NotificationCenter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.cmcc.paas.ideaplugin.codegen.notify.NotificationType.MODEL_ADDED;
import static com.cmcc.paas.ideaplugin.codegen.notify.NotificationType.MODEL_UPDATED;


/**
 * @author zhangyinghui
 * @date 2023/12/22
 */
public abstract class MethodSettingPane {
    public abstract JPanel getContent();
//    public abstract void setModel(MethodSettingModel model);
    public abstract JComboBox getReturnComboBox();
    public abstract JComboBox getArgComboBox();
//    public abstract  ArgsSettingPane getArgsSettingPane();
//    public abstract MethodSettingModel getModel();
    public abstract ClassModel.Method getMethod();
    public abstract void setMethod(ClassModel.Method method);
    public abstract MvcClassType getClassType();
//    private NotificationCenter.Handler modelUpdateHandler = new NotificationCenter.Handler() {
//        @Override
//        public void handleMessage(@NotNull NotificationCenter.Message msg) {
//            resetReturnComboBox();
//            if (msg.getEnvent().equalsIgnoreCase(MODEL_UPDATED)){
////                    getArgsSettingPane().updateClassModel((ClassModel) msg.getData());
//                resetArgComboBox();
//                resetReturnComboBox();
//            }
//        }
//    };
    public void init(){
//        NotificationCenter.INSTANCE.register(MODEL_ADDED, modelUpdateHandler);
//        NotificationCenter.INSTANCE.register(MODEL_UPDATED, modelUpdateHandler);
    }

    private MethodCfgPaneActionListener methodCfgPaneActionListener;

    public MethodCfgPaneActionListener getMethodCfgPaneActionListener() {
        return methodCfgPaneActionListener;
    }

    private void resetComboBoxWithDomainTypes(JComboBox comboBox, DomainType... type){
        List<ClassModel> classModels = DomainModelCtx.INSTANCE.getModesByTypes(type);
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addElement("-");
        classModels.forEach( e -> comboBoxModel.addElement(e.getClassName()) );
        comboBox.setModel(comboBoxModel);
    }
    public void resetReturnComboBox(){
        resetComboBoxWithDomainTypes(getReturnComboBox(), DomainType.ENTITY, DomainType.RESULT);
        if (getMethod() != null && getMethod().getResult() != null) {
            ClassModel classModel = getMethod().getResult().getClassModel();
            getReturnComboBox().setSelectedItem( classModel != null ? classModel.getClassName() : getMethod().getResult().getClassModel().getClassName());
        }
    }
    public void resetArgComboBox(){
        resetComboBoxWithDomainTypes(getArgComboBox(), DomainType.ARG, DomainType.ENTITY);
        if (getMethod() != null && getMethod().getArgs() != null && getMethod().getArgs().size() > 0) {
            ClassModel.MethodArg arg = getMethod().getArgs().get(0);
            getArgComboBox().setSelectedItem(arg.getClassModel() != null ? arg.getClassModel().getClassName() : arg.getClassModel().getClassName());
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
        private MvcClassType classType;
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

        public MvcClassType getClassType() {
            return classType;
        }

        public void setClassType(MvcClassType classType) {
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
            private String comment;
            private ClassModel classModel;
            private String className;
            private String refName;
            private Boolean isPathVar = false;
            private Boolean listTypeFlag = false;

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public ClassModel getClassModel() {
                return classModel;
            }

            public void setClassModel(ClassModel classModel) {
                this.classModel = classModel;
            }

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
            private ClassModel classModel;
            private String className;
            private String refName;
            private String comment;
            private Boolean outputPaged = false;
            private Boolean listTypeFlag = false;

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public ClassModel getClassModel() {
                return classModel;
            }

            public void setClassModel(ClassModel classModel) {
                this.classModel = classModel;
            }

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
