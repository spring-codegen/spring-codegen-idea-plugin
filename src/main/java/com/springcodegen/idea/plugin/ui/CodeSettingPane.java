package com.springcodegen.idea.plugin.ui;

import com.springcodegen.idea.plugin.ctx.CodeSettingCtx;
import com.springcodegen.idea.plugin.ctx.AppCtx;
import com.springcodegen.idea.plugin.swing.util.SwingUtils;
import com.springcodegen.idea.plugin.swing.util.TextFieldUtils;
import com.springcodegen.idea.plugin.ui.tookit.MessageBoxUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

/**
 * @author zhangyinghui
 * @date 2023/8/16
 */
public class CodeSettingPane {
    private JTextField basePkgTextField;
    private JTextField authorTextField;
    private JTextField modelBaseClsTextField;
    private JTextField ctrlBaseClsTextField;
    private JPanel content;
    private JButton saveSettingBtn;
    private JButton ctrlSourceFileButton;
    private JTextField ctrlDirTextField;
    private JTextField mybatisMapperDirTextField;
    private JButton resourceFileBtn;
    private JTextField svcDirTextField;
    private JButton svcSourceFileButton;
    private JTextField modelDirTextField;
    private JButton domainSourceFileButton;
    private JTextField svcBaseClsTextField;
    private JTextField apiPrefixTextField;
    private JTextField searchArgBaseClsTextField;
    private JTextField responseClsTextField;
    private JTextField daoBaseClsTextField;
    private JTextField argModelSuffix;
    private JTextField resultModelSuffix;
    private JTextField entityModelSuffix;
    private JTextArea innerTextArea;
    private JScrollPane scrollPane;
    private CodeSettingCtx model;

    public CodeSettingPane(){
        scrollPane.setBorder(null);
        for (Component component : content.getComponents()) {
            if (component instanceof JTextField){
                TextFieldUtils.INSTANCE.addTextChangedEvent((JTextField) component, new TextFieldUtils.TextChangedEvent() {
                    @Override
                    public void onTextChanged(@NotNull JTextField textField) {
                    }
                });
            }
        }
        saveSettingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getModel().save();
                MessageBoxUtils.showMessageAndFadeout("保存成功！");
            }
        });
        SwingUtils.addSelectDirEvent(ctrlSourceFileButton, ctrlDirTextField, content, null);
        SwingUtils.addSelectDirEvent(svcSourceFileButton, svcDirTextField, content, null);
        SwingUtils.addSelectDirEvent(domainSourceFileButton, modelDirTextField, content, null);
        SwingUtils.addSelectDirEvent(resourceFileBtn, mybatisMapperDirTextField, content, null);
//        ctrlSourceFileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                fileChooser.setCurrentDirectory(new File(AppCtx.getProject().getBasePath()));
//                int result = fileChooser.showDialog(content, "选择");
//                if (result == JFileChooser.APPROVE_OPTION){
//                    File selectedFile = fileChooser.getSelectedFile();
//                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
//                    ctrlDirTextField.setText(dir.getAbsolutePath());
//                }
//            }
//        });
//        svcSourceFileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                fileChooser.setCurrentDirectory(new File(AppCtx.getProject().getBasePath()));
//                int result = fileChooser.showDialog(content, "选择");
//                if (result == JFileChooser.APPROVE_OPTION){
//                    File selectedFile = fileChooser.getSelectedFile();
//                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
//                    svcDirTextField.setText(dir.getAbsolutePath());
//                }
//            }
//        });
//        domainSourceFileButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                fileChooser.setCurrentDirectory(new File(AppCtx.getProject().getBasePath()));
//                int result = fileChooser.showDialog(content, "选择");
//                if (result == JFileChooser.APPROVE_OPTION){
//                    File selectedFile = fileChooser.getSelectedFile();
//                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
//                    modelDirTextField.setText(dir.getAbsolutePath());
//                }
//            }
//        });
//        resourceFileBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JFileChooser fileChooser = new JFileChooser();
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//                fileChooser.setCurrentDirectory(new File(AppCtx.getProject().getBasePath()));
//                int result = fileChooser.showDialog(content, "选择");
//                if (result == JFileChooser.APPROVE_OPTION){
//                    File selectedFile = fileChooser.getSelectedFile();
//                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
//                    mybatisMapperDirTextField.setText(dir.getAbsolutePath());
//                }
//            }
//        });
    }
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

    public CodeSettingCtx getModel() {
        model.setAuthor(authorTextField.getText());
        model.setBasePkg(basePkgTextField.getText());
        model.setModelBaseCls(modelBaseClsTextField.getText());
        model.setSearchArgBaseCls(searchArgBaseClsTextField.getText());
        model.setModelSourceDir(modelDirTextField.getText());

        model.setCtrlBaseCls(ctrlBaseClsTextField.getText());
        model.setCtrlSourceDir(ctrlDirTextField.getText());

        model.setSvcBaseCls(svcBaseClsTextField.getText());
        model.setSvcSourceDir(svcDirTextField.getText());
        model.setMybatisMapperDir(mybatisMapperDirTextField.getText());
        model.setResponseCls(responseClsTextField.getText());
        model.setDaoBaseCls(daoBaseClsTextField.getText());

        model.setArgModelSuffix(argModelSuffix.getText());
        model.setResultModelSuffix(resultModelSuffix.getText());
        model.setEntityModelSuffix(entityModelSuffix.getText());
        model.setInnerModels(innerTextArea.getText());
        return model;
    }

    public void setModel(CodeSettingCtx model){
        this.model = model;
        basePkgTextField.setText(model.getBasePkg());
        authorTextField.setText(model.getAuthor());

        modelBaseClsTextField.setText(model.getModelBaseCls());
        searchArgBaseClsTextField.setText(model.getSearchArgBaseCls());
        modelDirTextField.setText(model.getModelSourceDir());

        ctrlBaseClsTextField.setText(model.getCtrlBaseCls());
        ctrlDirTextField.setText(model.getCtrlSourceDir());


        svcBaseClsTextField.setText(model.getSvcBaseCls());
        svcDirTextField.setText(model.getSvcSourceDir());
        responseClsTextField.setText(model.getResponseCls());

        mybatisMapperDirTextField.setText(model.getMybatisMapperDir());
        daoBaseClsTextField.setText(model.getDaoBaseCls());
        argModelSuffix.setText(model.getArgModelSuffix());
        resultModelSuffix.setText(model.getResultModelSuffix());
        entityModelSuffix.setText(model.getEntityModelSuffix());
        innerTextArea.setText(model.getInnerModels());
    }
}
