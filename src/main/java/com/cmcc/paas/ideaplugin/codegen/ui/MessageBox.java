package com.cmcc.paas.ideaplugin.codegen.ui;

import com.cmcc.paas.ideaplugin.codegen.db.DBCtx;
import com.cmcc.paas.ideaplugin.codegen.db.model.DBTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangyinghui
 * @date 2024/2/18
 */
public class MessageBox extends JFrame {
    private final static ReentrantLock lock = new ReentrantLock();
    private static MessageBox instance = null;

    private JLabel messageLabel;
    private String message = null;
    private Long fadeOutTime;
    private static Long DURATION_IN_MILLIS = 5000L;

    private MessageBox(String str) {
        this.message = str;
    }
    private void initGUI() {
        try {
            setUndecorated(true);
            setLocationRelativeTo(null);
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                messageLabel = new JLabel( message , JLabel.CENTER);
                getContentPane().add(messageLabel, BorderLayout.CENTER);
                messageLabel.setOpaque(true);
                messageLabel.setForeground(Color.black);
                messageLabel.setBackground(new Color(255,255,150));
                messageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
            pack();

            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension screenSize = tk.getScreenSize();
            int height = screenSize.height;
            int width = screenSize.width;
            setBounds(width / 2 - 150, height/2 - 150, 360, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void fadeout(){

        SwingWorker<MessageBox, MessageBox> swingWorker = new SwingWorker<MessageBox, MessageBox>() {
            @Override
            protected MessageBox doInBackground() throws Exception {
                while ( true){
                    System.out.println("fadeout lock start");
                    lock.lock();
                    System.out.println("fadeout locked");
                    try {
                        if (System.currentTimeMillis() > fadeOutTime){
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }finally {
                        lock.unlock();
                        System.out.println("fadeout unlock");
                    }

                }
                return null;
            }
            @Override
            protected void done() {
                dispose();
                System.out.println("dispose");
                instance = null;
            }
        };
        swingWorker.execute();
    }
    private void showAndFadeout(Long duration){
        initGUI();
        delay(duration);
        fadeout();
    }
    private void delay(Long duration){
        fadeOutTime = System.currentTimeMillis() + duration;
    }
    private void setMessage(String message){
        this.message = message;
        this.messageLabel.setText(message);
    }
    public static void showMessageAndFadeout(String message, Long duration){
        System.out.println("showMessageAndFadeout start lock:"+message+"|"+duration);
        lock.lock();
        System.out.println("showMessageAndFadeout locked");
        try {
            if (instance != null){
                System.out.println("instance != null");
                instance.setMessage(message);
                instance.delay(duration);
                return;
            }
            if (instance == null){
                System.out.println("instance == null");
                instance = new MessageBox(message);
                instance.showAndFadeout(duration);
            }
        }finally {
            lock.unlock();
            System.out.println("showMessageAndFadeout unlock");
        }
    }
    public static void showMessageAndFadeout(String message){
        showMessageAndFadeout(message, DURATION_IN_MILLIS);
    }
    public static void showMessage(String message){
        showMessageAndFadeout(message, 3600*1000L);
    }
}
