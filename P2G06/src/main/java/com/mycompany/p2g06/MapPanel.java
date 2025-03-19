package com.mycompany.p2g06;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.geom.Path2D;
import Model.CasaMap;
import Model.Room;
import java.awt.Point;

public class MapPanel extends JPanel {
    private CasaMap map;
    private List<Point> route;  // Cada Point: (row, col)
     
   private int cellSize = 40;
    
    // Colores para cada tipo de celda
    private static final Color COLOR_OBSTACLE = new Color(64, 64, 64);
    private static final Color COLOR_FREE = Color.WHITE;
    private static final Color COLOR_BASE = Color.RED;
    private static final Color COLOR_ROOM = new Color(255, 255, 0);
    private static final Color COLOR_GRID = Color.BLACK;
    private static final Color COLOR_ROUTE = Color.BLUE;
    
    public MapPanel(CasaMap map) {
        this.map = map;
        this.route = new ArrayList<>();
        
        // Ajusta el tamaño preferido
        setPreferredSize(new Dimension(
                CasaMap.COLS * cellSize, 
                CasaMap.ROWS * cellSize));
    }
    
    // Asignamos una nueva ruta y forzamos repintado
    public void setRoute(List<Point> route) {
        this.route = route;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Dibujamos la cuadrícula
        for (int row = 0; row < CasaMap.ROWS; row++) {
            for (int col = 0; col < CasaMap.COLS; col++) {
                int x = col * cellSize;
                int y = row * cellSize;
                
                char cell = map.getGrid()[row][col];
                
                // Determinamos color de fondo
                if (cell == '■') {
                    g2d.setColor(COLOR_OBSTACLE); // Obstáculo
                } else if (cell == 'B') {
                    g2d.setColor(COLOR_BASE); // Base
                } else if (Character.isDigit(cell) && cell != ' ') {
                    g2d.setColor(COLOR_ROOM); // Habitación
                } else {
                    g2d.setColor(COLOR_FREE); // Libre
                }
                
                // Pintamos la celda
                g2d.fillRect(x, y, cellSize, cellSize);
                
                // Borde de la celda
                g2d.setColor(COLOR_GRID);
                g2d.drawRect(x, y, cellSize, cellSize);
            }
        }
        
        // Dibujar el número completo de cada habitación sobre la celda
        for (Room room : map.getRooms()) {
            int x = room.getCol() * cellSize;
            int y = room.getRow() * cellSize;
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(room.getId()), x + cellSize/4, y + (3 * cellSize)/4);
        }
        
        // Dibujamos la ruta en azul con estrellas
        if (route != null && !route.isEmpty()) {
            g2d.setColor(COLOR_ROUTE);
            for (Point p : route) {
                int x = p.y * cellSize;
                int y = p.x * cellSize;
                // Dibujamos una estrella centrada en la celda
                drawStar(g2d, x + cellSize/2.0, y + cellSize/2.0, cellSize/2.5);
            }
        }
    }
    
    /**
     * Dibuja una estrella centrada en (cx, cy) con un "radio" r.
     */
    private void drawStar(Graphics2D g2d, double cx, double cy, double r) {
        int numRays = 5;         // 5 puntas
        double angle = Math.PI / numRays;  
        
        Path2D star = new Path2D.Double();
        for (int i = 0; i < 2 * numRays; i++) {
            double rad = (i % 2 == 0) ? r : r / 2.5;
            double currAngle = i * angle;
            double px = cx + Math.cos(currAngle) * rad;
            double py = cy + Math.sin(currAngle) * rad;
            if (i == 0) {
                star.moveTo(px, py);
            } else {
                star.lineTo(px, py);
            }
        }
        star.closePath();
        
        g2d.fill(star);
    }
}
