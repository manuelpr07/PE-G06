package Algoritmo;

import java.awt.Point;
import java.util.*;

public class AStar {
    private char[][] grid;
    private int rows, cols;
    
    public AStar(char[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }
    
    private class Node {
        int row, col;
        double g;   // costo acumulado
        double h;   // heurística
        Node parent;
        
        Node(int row, int col, double g, double h, Node parent) {
            this.row = row;
            this.col = col;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }
        
        double f() {
            return g + h;
        }
    }
    
    private boolean isWalkable(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return false;
        return grid[r][c] != '■';
    }
    
    private List<Node> getNeighbors(Node current) {
        List<Node> neighbors = new ArrayList<>();
        int[][] dirs = { {-1,0}, {1,0}, {0,-1}, {0,1} };
        for (int[] d : dirs) {
            int nr = current.row + d[0];
            int nc = current.col + d[1];
            if (isWalkable(nr, nc)) {
                neighbors.add(new Node(nr, nc, 0, 0, null));
            }
        }
        return neighbors;
    }
    
    private double heuristic(int r, int c, int gr, int gc) {
        // Distancia Manhattan
        return Math.abs(r - gr) + Math.abs(c - gc);
    }
    
    // Devuelve la lista de celdas (Point) que conforman la ruta
    public List<Point> calcularRuta(int startR, int startC, int goalR, int goalC) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        boolean[][] closedSet = new boolean[rows][cols];
        
        Node start = new Node(startR, startC, 0, heuristic(startR, startC, goalR, goalC), null);
        openSet.add(start);
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.row == goalR && current.col == goalC) {
                // Reconstruimos la ruta
                List<Point> path = new ArrayList<>();
                while (current != null) {
                    path.add(new Point(current.row, current.col));
                    current = current.parent;
                }
                Collections.reverse(path);
                return path;
            }
            closedSet[current.row][current.col] = true;
            
            for (Node neigh : getNeighbors(current)) {
                if (closedSet[neigh.row][neigh.col]) continue;
                
                double tentativeG = current.g + 1; // costo unitario
                boolean inOpen = false;
                
                for (Node node : openSet) {
                    if (node.row == neigh.row && node.col == neigh.col) {
                        inOpen = true;
                        if (tentativeG < node.g) {
                            node.g = tentativeG;
                            node.parent = current;
                        }
                        break;
                    }
                }
                
                if (!inOpen) {
                    neigh.g = tentativeG;
                    neigh.h = heuristic(neigh.row, neigh.col, goalR, goalC);
                    neigh.parent = current;
                    openSet.add(neigh);
                }
            }
        }
        
        // Si no hay ruta
        return null;
    }
    
    // Si solo quieres la distancia:
    public double calcularDistancia(int startR, int startC, int goalR, int goalC) {
        List<Point> ruta = calcularRuta(startR, startC, goalR, goalC);
        return (ruta == null) ? 1e6 : ruta.size();
    }
}
