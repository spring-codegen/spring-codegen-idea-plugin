package com.springcodegen.idea.plugin.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyinghui
 * @date 2023/12/20
 */
public class MethodContainerBackgroundPane extends JPanel {
    private List<Line> lines = new ArrayList<>();
    public void addLine(Point from, Point to){
        lines.add(new Line(from, to));
        this.repaint();
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
        this.repaint();
    }

    public void clear(){
        lines.clear();
        this.removeAll();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (lines == null || lines.size() == 0){
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2)); // 设置边框宽度
        g2d.setColor(Color.GRAY); // 设置线条颜色为灰色
        for (Line line: lines){
            g2d.drawLine(line.from.x, line.from.y, line.to.x, line.to.y);
        }

//        g2d.setColor(new Color(0xffd633)); // 设置填充颜色
//        g2d.setStroke(new BasicStroke(20)); // 设置边框宽度
//        g2d.fillOval(getWidth() - 600, getHeight() - 600, 450, 450); // 填充圆形
//        g2d.setColor(Color.BLACK); // 设置边框颜色
//        g2d.drawOval(getWidth() - 600, getHeight() - 600, 450, 450); // 绘制边框
//
//        g2d.drawOval(getWidth() - 350, getHeight() - 500, 100, 125); // 绘制边框
//        g2d.drawOval(getWidth() - 500, getHeight() - 500, 100, 125); // 绘制边框
//        g2d.setColor(Color.WHITE);
//        g2d.fillOval(getWidth() - 335, getHeight() - 490, 70, 95); // 填充圆形
//        g2d.fillOval(getWidth() - 485, getHeight() - 490, 70, 95); // 填充圆形
//
//        g2d.setColor(Color.RED);
//        g2d.fillOval(getWidth() - 550, getHeight() - 350, 100, 100); // 填充圆形
//        g2d.fillOval(getWidth() - 300, getHeight() - 350, 100, 100); // 填充圆形
//
//        g2d.setColor(Color.GRAY); // 设置线条颜色为灰色
//        g2d.drawLine(150, 350, 400, 110); // 画一条从左下角到右上角的线条
//        g2d.drawLine(650, 350, 400, 110); // 画一条从左下角到右上角的线条
    }
    public static class Line{
        private Point from;
        private Point to;
        public Line(Point from, Point to){
            this.from = from;
            this.to = to;
        }
    }
}
