package com.springcodegen.idea.plugin.ui.tookit;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2024/4/8
 */
public class MessageBoxUtils {
    private static List<MessagePane> panes = new ArrayList();
    private static Long DURATION_IN_MILLIS = 10000L;
    private static int PANE_MIN_WEIGHT = 200;
    private static int PANE_MIN_HEIGHT = 50;
    public static void refresh(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int width = screenSize.width;
        int y = 200;
        int w = PANE_MIN_WEIGHT;
        for (MessagePane p : panes) {
            w = Integer.max(w, p.getWidth());
            int h = Integer.max(PANE_MIN_HEIGHT, p.getHeight());
            p.setSize( new Dimension( w, h ) );
            y = y + p.getHeight() + 3;
            p.setLocation(width - p.getWidth() - 50, y );

            System.out.println("showMessageAndFadeout:refresh"+p.getName() +"w="+p.getWidth()+ ",h=" +p.getHeight()+",y="+y);
        }
    }
    public static void showMessageAndFadeout(String message){

        MessagePane messagePane = new MessagePane(message);
        panes.add(messagePane);
        messagePane.setCloseListener( pane ->{
            panes.remove(pane);
            System.out.println("showMessageAndFadeout:remove"+pane.getName() + "," +panes.size());
            pane.invalidate();
            pane.repaint();
            refresh();
        });
        messagePane.setName(String.valueOf(panes.size()));
        System.out.println("showMessageAndFadeout:"+message+","+messagePane.getName() + "," +panes.size());
        messagePane.showMessageAndFadeout(message, DURATION_IN_MILLIS);
        refresh();
    }
}
