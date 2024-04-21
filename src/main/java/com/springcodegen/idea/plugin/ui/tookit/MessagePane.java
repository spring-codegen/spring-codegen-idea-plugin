package com.springcodegen.idea.plugin.ui.tookit;

import javax.swing.*;
import java.awt.*;

/**
 * @author zhangyinghui
 * @date 2024/4/8
 */
public class MessagePane extends JFrame{
    private JButton closeBtn;
    private JLabel messageTextArea;
    private Long fadeOutTime;
    private CloseListener closeListener;

    public CloseListener getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public MessagePane(String message) {
        this.requestFocus();
        this.setAlwaysOnTop(true);
        getContentPane().setBackground(new Color(255,255,150));


        setUndecorated(true);
        setLocationRelativeTo(null);
        setVisible(true);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createLineBorder(Color.gray));

        GridBagLayout layout = new GridBagLayout();
        getContentPane().setLayout( layout );
        //

        closeBtn = new JButton( "x" );
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setPreferredSize(new Dimension(30, 30));
        closeBtn.setHorizontalAlignment(SwingConstants.RIGHT);
        closeBtn.setVerticalAlignment(SwingConstants.TOP);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.gridx = 1;
        c.gridy = 0;
        getContentPane().add(closeBtn, c);


        messageTextArea = new JLabel(message);
//        messageTextArea.setLineWrap(true);
        messageTextArea.setBackground(null);
        messageTextArea.setForeground(Color.BLACK);
        messageTextArea.setMinimumSize(new Dimension(200,30));
        messageTextArea.setMaximumSize(new Dimension(200,150));
        messageTextArea.setPreferredSize(new Dimension(200,30));
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        getContentPane().add(messageTextArea, c);

//        getContentPane().setBackground(Color.RED);
//        messageTextArea.setBackground(Color.BLUE);

        closeBtn.addActionListener(e ->{
            dispose();
        });
    }

    public void setMessage(String msg){
        messageTextArea.setText(msg);

    }
    private void fadeout(){
        SwingWorker<MessagePane, MessagePane> swingWorker = new SwingWorker<MessagePane, MessagePane>() {
            @Override
            protected MessagePane doInBackground() throws Exception {
                while ( true){
                    try {
                        if (System.currentTimeMillis() > fadeOutTime){
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }finally {
                    }

                }
                return null;
            }
            @Override
            protected void done() {
                dispose();
            }
        };
        swingWorker.execute();
    }
    @Override
    public void dispose(){
        super.dispose();
        if (closeListener != null){
            closeListener.onClose(this);
        }
    }
    private void delay(Long duration){
        fadeOutTime = System.currentTimeMillis() + duration;
    }
    public void showMessageAndFadeout(String message, Long duration){
        try {
            setMessage(message);
            this.pack();
            this.setVisible(true);
            this.invalidate();
            this.repaint();

            delay(duration);
            fadeout();
        }finally {
        }
    }
    public interface CloseListener{
        public void onClose(MessagePane messagePane);
    }
}
