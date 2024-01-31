package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.config.ProjectCfg;
import com.cmcc.paas.ideaplugin.codegen.constants.AppCtx;
import com.cmcc.paas.ideaplugin.codegen.swing.util.TextFieldUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author zhangyinghui
 * @date 2023/8/16
 */
public class CodeSettingPane {
    private JTextField tableSchemaTextField;
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
    private JTextField domainDirTextField;
    private JButton domainSourceFileButton;
    private ProjectCfg model;

    public CodeSettingPane(){
        System.out.println("CodeSettingPanel...");
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
            }
        });
        ctrlSourceFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File(AppCtx.INSTANCE.getProject().getBasePath()));
                int result = fileChooser.showDialog(content, "选择");
                if (result == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
                    ctrlDirTextField.setText(dir.getAbsolutePath());
                }
            }
        });
        svcSourceFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File(AppCtx.INSTANCE.getProject().getBasePath()));
                int result = fileChooser.showDialog(content, "选择");
                if (result == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
                    svcDirTextField.setText(dir.getAbsolutePath());
                }
            }
        });
        domainSourceFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File(AppCtx.INSTANCE.getProject().getBasePath()));
                int result = fileChooser.showDialog(content, "选择");
                if (result == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
                    domainDirTextField.setText(dir.getAbsolutePath());
                }
            }
        });
        resourceFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setCurrentDirectory(new File(AppCtx.INSTANCE.getProject().getBasePath()));
                int result = fileChooser.showDialog(content, "选择");
                if (result == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
                    mybatisMapperDirTextField.setText(dir.getAbsolutePath());
                }
            }
        });
    }
    public void createUIComponents(){
    }
    public JPanel getContent() {
        return content;
    }

    public ProjectCfg getModel() {
        model.setAuthor(authorTextField.getText());
        model.setBasePkg(basePkgTextField.getText());
        model.setCtrlBaseCls(ctrlBaseClsTextField.getText());
        model.setModelBaseCls(modelBaseClsTextField.getText());
        model.setCtrlSourceDir(ctrlDirTextField.getText());
        model.setSvcSourceDir(svcDirTextField.getText());
        model.setDomainSourceDir(domainDirTextField.getText());
        model.setMybatisMapperDir(mybatisMapperDirTextField.getText());
        return model;
    }

    public void setModel(ProjectCfg model) {
        this.model = model;
        basePkgTextField.setText(model.getBasePkg());
        authorTextField.setText(model.getAuthor());
        modelBaseClsTextField.setText(model.getModelBaseCls());
        ctrlBaseClsTextField.setText(model.getCtrlBaseCls());
        ctrlDirTextField.setText(model.getCtrlSourceDir());
        mybatisMapperDirTextField.setText(model.getMybatisMapperDir());
    }
}
