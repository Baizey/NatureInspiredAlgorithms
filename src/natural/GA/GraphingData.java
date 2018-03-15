package natural.GA;

import lsm.helpers.IO.read.text.TextReader;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

public class GraphingData extends JPanel {

    private static double[][] data;

    static {
        ArrayList<String> input = null;
        try {
            input = TextReader.readFile("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert input != null;
        data = input.stream()
                .map(i -> i.split(" "))
                .map(i -> Arrays.stream(i).mapToDouble(Double::parseDouble).toArray())
                .toArray(double[][]::new);
    }

    private final static int PADDING = 20;

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(PADDING, PADDING, PADDING, height - PADDING));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PADDING, height - PADDING, width - PADDING, height - PADDING));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = "Generations taken";
        float sy = PADDING + ((height - 2 * PADDING) - s.length() * sh) / 2 + lm.getAscent();
        for (int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float) font.getStringBounds(letter, frc).getWidth();
            float sx = (PADDING - sw) / 2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        // Abcissa label.
        s = "DNA size";
        sy = height - PADDING + (PADDING - sh) / 2 + lm.getAscent();
        float sw = (float) font.getStringBounds(s, frc).getWidth();
        float sx = (width - sw) / 2;
        g2.drawString(s, sx, sy);
        double xInc = (double) (width - 2 * PADDING) / (data.length - 1);
        double scale = (double) (height - 2 * PADDING) / getMax();
        double[] xs = new double[data.length];
        for (int i = 0; i < xs.length; i++) xs[i] = PADDING + i * xInc;
        double[] nlogn = new double[data.length];
        for (int i = 0; i < nlogn.length; i++) nlogn[i] = data[i][0] * Math.log(data[i][0]);

        // Mark data points.
        double prevX;
        double prevY;

        int dotSize = 4;
        g2.setPaint(Color.blue);
        for (int i = 0; i < data.length; i++) {
            double y = (height - PADDING - scale * data[i][1]);
            double x = xs[i];
            g2.fill(new Ellipse2D.Double(x - 2, y - 2, dotSize, dotSize));
        }

        g2.setStroke(new BasicStroke(3));
        prevX = PADDING;
        prevY = height - PADDING;
        g2.setPaint(Color.green);
        for (int i = 0; i < data.length; i++) {
            double y = (height - PADDING - scale * nlogn[i] * 0.90);
            double x = xs[i];
            g2.draw(new Line2D.Double(x, y, prevX, prevY));
            prevX = x;
            prevY = y;
        }
        prevX = PADDING;
        prevY = height - PADDING;
        g2.setPaint(Color.green);
        for (int i = 0; i < data.length; i++) {
            double x = xs[i];
            double y = (height - PADDING - scale * data[i][0]);
            g2.draw(new Line2D.Double(x, y, prevX, prevY));
            prevX = x;
            prevY = y;
        }
        prevX = PADDING;
        prevY = height - PADDING;
        g2.setPaint(Color.red);
        for (int i = 0; i < data.length; i++) {
            double y = (height - PADDING - scale * nlogn[i] * 1.10);
            double x = xs[i];
            g2.draw(new Line2D.Double(x, y, prevX, prevY));
            prevX = x;
            prevY = y;
        }
        prevX = PADDING;
        prevY = height - PADDING;
        g2.setPaint(Color.red);
        for (int i = 0; i < data.length; i++) {
            double y = (height - PADDING - scale * data[i][0] * Math.sqrt(data[i][0]));
            double x = xs[i];
            g2.draw(new Line2D.Double(x, y, prevX, prevY));
            prevX = x;
            prevY = y;
        }

        prevX = PADDING;
        prevY = height - PADDING;
        g2.setPaint(Color.cyan);
        for (int i = 0; i < data.length; i++) {
            double y = (height - PADDING - scale * nlogn[i] * 1.00);
            double x = xs[i];
            g2.draw(new Line2D.Double(x, y, prevX, prevY));
            prevX = x;
            prevY = y;
        }

        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.BLACK);
        ArrayDeque<Double> lastPoints = new ArrayDeque<>();
        double meanPoint = 0;
        prevY = (height - PADDING - scale * data[0][1]);
        for (int i = 0; i < data.length; i++) {
            double y = (height - PADDING - scale * data[i][1]);
            if (lastPoints.size() >= 10)
                meanPoint -= lastPoints.pollLast();
            meanPoint += y;
            lastPoints.addFirst(y);
            if (i > 9)
                g2.draw(new Line2D.Double(xs[i - 5], meanPoint / lastPoints.size(), xs[i - 6], prevY / lastPoints.size()));
            prevY = meanPoint;
        }
    }

    private double getMax() {
        double max = Integer.MIN_VALUE;
        for (double[] aData : data)
            if (aData[1] > max)
                max = aData[1];
        return max;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new GraphingData());
        f.setSize(1800, 900);
        f.setLocation(0, 0);
        f.setVisible(true);
    }
}