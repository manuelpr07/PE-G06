package com.mycompany.p2g06;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FitnessChartPanel extends JPanel {
    private List<Double> bestFitness;
    private List<Double> avgFitness;
    private List<Double> absBestFitness;
    
    public FitnessChartPanel(List<Double> bestFitness, List<Double> avgFitness, List<Double> absBestFitness) {
        this.bestFitness = bestFitness;
        this.avgFitness = avgFitness;
        this.absBestFitness = absBestFitness;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        
        // Márgenes para el gráfico
        int marginLeft = 60, marginRight = 20, marginTop = 20, marginBottom = 60;
        int plotWidth = width - marginLeft - marginRight;
        int plotHeight = height - marginTop - marginBottom;
        
        int generations = bestFitness.size();
        
        // Determinar el rango de valores para escalar
        double maxValue = Math.max(Collections.max(bestFitness), Math.max(Collections.max(avgFitness), Collections.max(absBestFitness)));
        double minValue = Math.min(Collections.min(bestFitness), Math.min(Collections.min(avgFitness), Collections.min(absBestFitness)));
        double marginValue = (maxValue - minValue) * 0.1;
        maxValue += marginValue;
        minValue -= marginValue;
        
        double xStep = (double) plotWidth / (generations - 1);
        
        // Dibujar ejes
        g2d.setColor(Color.BLACK);
        // Eje X
        g2d.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom);
        // Eje Y
        g2d.drawLine(marginLeft, marginTop, marginLeft, height - marginBottom);
        
        // Etiquetas de los ejes
        // Eje X: "Generaciones" centrado debajo del eje
        String xLabel = "Generaciones";
        FontMetrics fm = g2d.getFontMetrics();
        int xLabelWidth = fm.stringWidth(xLabel);
        g2d.drawString(xLabel, marginLeft + (plotWidth - xLabelWidth) / 2, height - marginBottom + 40);
        
        // Eje Y: "Fitness" en vertical
        String yLabel = "Fitness";
        // Rotamos 90 grados para dibujar verticalmente
        Graphics2D g2dRot = (Graphics2D) g2d.create();
        g2dRot.rotate(-Math.PI / 2);
        int yLabelWidth = fm.stringWidth(yLabel);
        g2dRot.drawString(yLabel, -marginTop - (plotHeight + yLabelWidth) / 2, marginLeft - 40);
        g2dRot.dispose();
        
        // Dibujar las tres líneas de evolución usando el área de dibujo (plot)
        // Línea azul: Mejor Fitness por generación
        g2d.setColor(Color.BLUE);
        for (int i = 0; i < generations - 1; i++) {
            int x1 = marginLeft + (int) (i * xStep);
            int x2 = marginLeft + (int) ((i + 1) * xStep);
            int y1 = marginTop + (int) (plotHeight - ((bestFitness.get(i) - minValue) / (maxValue - minValue) * plotHeight));
            int y2 = marginTop + (int) (plotHeight - ((bestFitness.get(i + 1) - minValue) / (maxValue - minValue) * plotHeight));
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Línea verde: Fitness Promedio
        g2d.setColor(Color.GREEN);
        for (int i = 0; i < generations - 1; i++) {
            int x1 = marginLeft + (int) (i * xStep);
            int x2 = marginLeft + (int) ((i + 1) * xStep);
            int y1 = marginTop + (int) (plotHeight - ((avgFitness.get(i) - minValue) / (maxValue - minValue) * plotHeight));
            int y2 = marginTop + (int) (plotHeight - ((avgFitness.get(i + 1) - minValue) / (maxValue - minValue) * plotHeight));
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Línea roja: Mejor Fitness Global
        g2d.setColor(Color.RED);
        for (int i = 0; i < generations - 1; i++) {
            int x1 = marginLeft + (int) (i * xStep);
            int x2 = marginLeft + (int) ((i + 1) * xStep);
            int y1 = marginTop + (int) (plotHeight - ((absBestFitness.get(i) - minValue) / (maxValue - minValue) * plotHeight));
            int y2 = marginTop + (int) (plotHeight - ((absBestFitness.get(i + 1) - minValue) / (maxValue - minValue) * plotHeight));
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Opcional: dibujar tick marks y valores en el eje Y
        g2d.setColor(Color.BLACK);
        int numTicks = 5;
        for (int i = 0; i <= numTicks; i++) {
            int y = marginTop + (int) (i * (double) plotHeight / numTicks);
            int value = (int) (maxValue - i * (maxValue - minValue) / numTicks);
            g2d.drawLine(marginLeft - 5, y, marginLeft, y);
            g2d.drawString(String.valueOf(value), 5, y + 5);
        }
    }
}
