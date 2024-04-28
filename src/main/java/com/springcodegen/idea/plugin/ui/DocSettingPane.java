package com.springcodegen.idea.plugin.ui;

import com.intellij.ide.actions.OpenFileAction;
import com.springcodegen.idea.plugin.ctx.AppCtx;
import com.springcodegen.idea.plugin.ctx.DocSettingCtx;
import com.springcodegen.idea.plugin.gen.DocGenerator;
import com.springcodegen.idea.plugin.ui.tookit.MessageBoxUtils;
import com.springcodegen.idea.plugin.util.StringUtils;
import com.springcodegen.idea.plugin.swing.util.SwingUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhangyinghui
 * @date 2024/4/19
 */
public class DocSettingPane {
    private JPanel content;
    private JButton outputDirChooseBtn;
    private JTextField outputDirTextField;
    private JButton genDocBtn;
    private JTextField moduleDirTextField;
    private JButton moduleDirChooseBtn;
    private JCheckBox htmlCheckBox;
    private JCheckBox wordCheckBox;
    private JCheckBox markDownCheckBox;
    private JCheckBox openApiCheckBox;
    private JCheckBox postmanCheckBox;
    private JButton mvnHomeDirChooseBtn;
    private JTextField mvnHomeDirTextField;
    private JPanel docTypePane;
    private JTextPane logTextPane;
    private JScrollPane logScrollPane;
    private JEditorPane noteEditPane;
    private JCheckBox openDocCheckBox;
    private SimpleAttributeSet logAttrSet = new SimpleAttributeSet();
    private SimpleAttributeSet errorAttrSet = new SimpleAttributeSet();

    public DocSettingPane() {
        logScrollPane.setBorder(null);
        Arrays.stream((new JButton[]{outputDirChooseBtn, moduleDirChooseBtn, mvnHomeDirChooseBtn})).forEach( btn -> {
            btn.addActionListener( actionEvent -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String currentDirectory = null;
                if (actionEvent.getSource() == moduleDirChooseBtn) {
                    currentDirectory = moduleDirTextField.getText();
                }
                if (actionEvent.getSource() == outputDirChooseBtn) {
                    currentDirectory = outputDirTextField.getText();
                }
                if (actionEvent.getSource() == mvnHomeDirChooseBtn) {
                    currentDirectory = mvnHomeDirTextField.getText();
                }
                if ( org.apache.commons.lang3.StringUtils.isEmpty(currentDirectory) ) {
                    currentDirectory = AppCtx.getProject().getBasePath();
                }
                fileChooser.setCurrentDirectory(new File(currentDirectory));
                int result = fileChooser.showDialog(content, "选择");
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    File dir = selectedFile.isDirectory() ? selectedFile : selectedFile.getParentFile();
                    String fp = StringUtils.INSTANCE.linuxPath(dir.getAbsolutePath());
                    if (actionEvent.getSource() == moduleDirChooseBtn) {
                        moduleDirTextField.setText(fp);
                        DocSettingCtx.INSTANCE.setModuleDir(moduleDirTextField.getText());
                    }
                    if (actionEvent.getSource() == outputDirChooseBtn) {
                        outputDirTextField.setText(fp);
                        DocSettingCtx.INSTANCE.setOutputDir(outputDirTextField.getText());
                    }
                    if (actionEvent.getSource() == mvnHomeDirChooseBtn) {
                        mvnHomeDirTextField.setText(fp);
                        DocSettingCtx.INSTANCE.setMvnHomeDir(mvnHomeDirTextField.getText());
                    }
                    DocSettingCtx.save();
                }
            });
        });
        genDocBtn.addActionListener(actionEvent -> {
            genDoc();
        });

        DocSettingCtx.load();
        mvnHomeDirTextField.setText(DocSettingCtx.INSTANCE.getMvnHomeDir());
        moduleDirTextField.setText(DocSettingCtx.INSTANCE.getModuleDir());
        outputDirTextField.setText(DocSettingCtx.INSTANCE.getOutputDir());
        Arrays.stream(new JCheckBox[] {htmlCheckBox, wordCheckBox, markDownCheckBox,openApiCheckBox,postmanCheckBox})
                .forEach(box -> {
                    box.addActionListener( e -> {
                        List<String> docTypes = getSelectedDocTypes();
                        DocSettingCtx.INSTANCE.setDocTypes(String.join(",", docTypes));
                        DocSettingCtx.save();
                    });
                });
        Arrays.stream(DocSettingCtx.INSTANCE.getDocTypes().split(","))
                .forEach(docType -> {
                    SwingUtils.searchComponentsByName(content, docType).forEach( box ->( (JCheckBox)box).setSelected(true));
                });

        StyleConstants.setForeground(errorAttrSet, Color.RED);//设置文本颜色
//        StyleConstants.setFontSize(errorAttrSet, 14);//设置文本大小
//        StyleConstants.setAlignment(errorAttrSet, StyleConstants.ALIGN_LEFT);//设置文本对齐方式
        noteEditPane.addHyperlinkListener( e ->{
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    URL url = e.getURL();
                    if (url != null) {
                        if (url.getHost().equals("codegen")) {
                            String docCfgFile = Objects.requireNonNull(AppCtx.getProject()).getBasePath() + url.getPath();
                            OpenFileAction.openFile(docCfgFile, Objects.requireNonNull(AppCtx.getProject()));
                            return;
                        }
                        Desktop.getDesktop().browse(url.toURI());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }
    private List<String> getSelectedDocTypes(){
        List<String> docTypes = Arrays.stream(new JCheckBox[] {htmlCheckBox, wordCheckBox, markDownCheckBox,openApiCheckBox,postmanCheckBox})
                .filter(AbstractButton::isSelected)
                .map(Component::getName).toList();
        return docTypes;
    }
    private void genDoc(){
        StyledDocument doc = logTextPane.getStyledDocument();
        List<String> docTypes = getSelectedDocTypes();
        if (docTypes.isEmpty()){
            MessageBoxUtils.showMessageAndFadeout("请选择要输出的文档类型");
        }
        logScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        SwingWorker<Boolean, LogMessage> swingWorker = new SwingWorker<Boolean, LogMessage>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                AtomicReference<Boolean> success = new AtomicReference<>(true);
                logTextPane.setText("");
                DocGenerator.gen( docTypes, s ->{
                    publish(new LogMessage(LogStatus.NORMAL, s));
                }, s ->{
                    publish(new LogMessage(LogStatus.ERROR, s));
                    if (s.startsWith("Error")) {
                        success.set(false);
                    }
                });
                return success.get();
            }
            @Override
            protected void process(List<LogMessage> chunks){
                chunks.forEach(chunk -> {
                    try {
                        doc.insertString(doc.getLength(), chunk.msg+"\n", chunk.status == LogStatus.ERROR ? errorAttrSet : logAttrSet);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    MessageBoxUtils.showMessageAndFadeout( success? "文档输出完成！" : "文档输出出错！");
                    if (success){
                        File docFile = new File(outputDirTextField.getText()+"/index.html");
                        if (htmlCheckBox.isSelected() && docFile.exists()) {
                            Desktop.getDesktop().open(docFile);
                            return;
                        }

                        File dir = new File(outputDirTextField.getText());
                        if (dir.exists()){
                            Desktop.getDesktop().open(dir);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        swingWorker.execute();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
    class LogMessage{
        public String msg;
        public LogStatus status;
        public LogMessage(LogStatus status, String msg){
            this.status = status;
            this.msg = msg;
        }
    }
    enum LogStatus{
        NORMAL,
        ERROR
    }
}
