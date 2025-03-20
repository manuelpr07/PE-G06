package Algoritmo;

import java.awt.Point;
import java.util.*;

public class AStar {
    private char[][] grid;
    private int rows, cols;
    private boolean allowDiagonal = false;
    private final double DIAG_COST = Math.sqrt(2);
    
    // Caché estático para almacenar rutas calculadas: 
    // La clave es un String formado a partir de las coordenadas de inicio y meta.
    private static Map<String, List<Point>> routeCache = new HashMap<>();
    
    public AStar(char[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }
    
    // Genera una clave única para el par de puntos (inicio y meta)
    private String getCacheKey(int startR, int startC, int goalR, int goalC) {
        return startR + "," + startC + "-" + goalR + "," + goalC;
    }
    
    // Verifica si la celda es transitable (dentro de límites y no es obstáculo)
    private boolean isWalkable(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }
        return grid[r][c] != '■'; 
    }
    
    // Calcula la heurística (Manhattan o combinada si se permiten diagonales)
    private double heuristic(int r, int c, int goalR, int goalC) {
        int dr = Math.abs(r - goalR);
        int dc = Math.abs(c - goalC);
        if (!allowDiagonal) {
            return dr + dc;
        } else {
            double minD = Math.min(dr, dc);
            double maxD = Math.max(dr, dc);
            return minD * DIAG_COST + (maxD - minD);
        }
    }
    
    /**
     * Calcula la ruta más corta entre (startR, startC) y (goalR, goalC) usando A*.
     * Si la ruta ya fue calculada previamente, se devuelve desde el caché.
     */
    public List<Point> calcularRuta(int startR, int startC, int goalR, int goalC) {
        String key = getCacheKey(startR, startC, goalR, goalC);
        if (routeCache.containsKey(key)) {
            return routeCache.get(key);
        }
        
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        boolean[][] closedSet = new boolean[rows][cols];
        double[][] gCost = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            Arrays.fill(gCost[i], Double.POSITIVE_INFINITY);
        }
        
        Node startNode = new Node(startR, startC, 0.0, heuristic(startR, startC, goalR, goalC), null);
        gCost[startR][startC] = 0.0;
        openSet.add(startNode);
        
        int[][] directionsCardinal = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        int[][] directionsDiagonal = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
        
        List<Point> path = null;
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (closedSet[current.r][current.c]) {
                continue;
            }
            closedSet[current.r][current.c] = true;
            
            if (current.r == goalR && current.c == goalC) {
                path = new ArrayList<>();
                Node node = current;
                while (node != null) {
                    path.add(new Point(node.r, node.c));
                    node = node.parent;
                }
                Collections.reverse(path);
                break;
            }
            
            // Explorar vecinos en direcciones cardinales
            for (int[] dir : directionsCardinal) {
                int nr = current.r + dir[0];
                int nc = current.c + dir[1];
                if (!isWalkable(nr, nc) || closedSet[nr][nc]) {
                    continue;
                }
                double stepCost = 1.0;
                double newG = current.g + stepCost;
                if (newG < gCost[nr][nc]) {
                    gCost[nr][nc] = newG;
                    double hValue = heuristic(nr, nc, goalR, goalC);
                    Node neighbor = new Node(nr, nc, newG, hValue, current);
                    openSet.add(neighbor);
                }
            }
            
            // Si se permiten diagonales, explorar también en esas direcciones
            if (allowDiagonal) {
                for (int[] dir : directionsDiagonal) {
                    int nr = current.r + dir[0];
                    int nc = current.c + dir[1];
                    if (!isWalkable(nr, nc) || closedSet[nr][nc]) {
                        continue;
                    }
                    double stepCost = DIAG_COST;
                    double newG = current.g + stepCost;
                    if (newG < gCost[nr][nc]) {
                        gCost[nr][nc] = newG;
                        double hValue = heuristic(nr, nc, goalR, goalC);
                        Node neighbor = new Node(nr, nc, newG, hValue, current);
                        openSet.add(neighbor);
                    }
                }
            }
        }
        
        // Almacenar la ruta (o null) en el caché y devolverla
        routeCache.put(key, path);
        return path;
    }
    
    // Calcula la distancia (número de pasos) entre dos celdas usando la ruta calculada
    public double calcularDistancia(int startR, int startC, int goalR, int goalC) {
        List<Point> ruta = calcularRuta(startR, startC, goalR, goalC);
        return (ruta == null) ? Double.POSITIVE_INFINITY : ruta.size() - 1;
    }
    
    // Clase interna para representar un nodo en la búsqueda
    private class Node {
        int r, c;
        double g, h;
        Node parent;
        
        Node(int r, int c, double g, double h, Node parent) {
            this.r = r;
            this.c = c;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }
        
        double f() {
            return g + h;
        }
    }
}
