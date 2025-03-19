package Algoritmo;

import java.awt.Point;
import java.util.*;

public class AStar {
    private char[][] grid;
    private int rows, cols;
    // Permitir movimientos diagonales (8 direcciones). Puede ajustarse según necesidad.
    private boolean allowDiagonal = false;
    // Costo para movimiento diagonal (si diagonales permitidas). 
    // Si los movimientos cardinales cuestan 1, la diagonal costará sqrt(2) (~1.414) para reflejar mayor distancia.
    private final double DIAG_COST = Math.sqrt(2);
    
    public AStar(char[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }
    
    // Clase interna para representar un nodo en la búsqueda (celda del grid).
    private class Node {
        int r, c;           // coordenadas de la celda
        double g;           // costo acumulado desde el inicio (g-cost)
        double h;           // heurística estimada al objetivo (h-cost)
        Node parent;        // nodo padre para reconstruir la ruta
        
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
    
    /**
     * Verifica si una celda es transitable (dentro de límites y no es obstáculo).
     */
    private boolean isWalkable(int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }
        return grid[r][c] != '■';  // suponemos '■' representa un obstáculo en el grid
    }
    
    /**
     * Cálculo de la heurística Manhattan (distancia Manhattan) entre una celda (r,c) y el objetivo (goalR, goalC).
     * Si se permiten movimientos diagonales, ajusta la heurística para mantenerla admisible 
     * (usando distancia octil/chebyshev según el costo diagonal).
     */
    private double heuristic(int r, int c, int goalR, int goalC) {
        int dr = Math.abs(r - goalR);
        int dc = Math.abs(c - goalC);
        if (!allowDiagonal) {
            // Distancia Manhattan estándar (4 direcciones)
            return dr + dc;
        } else {
            // Si las diagonales están permitidas, usamos una heurística consistente con movimientos diagonales.
            // Combinamos movimientos diagonales y rectos restantes:
            double minD = Math.min(dr, dc);
            double maxD = Math.max(dr, dc);
            // Pasos diagonales (minD) y pasos rectos restantes (maxD - minD).
            double diagonalSteps = minD;
            double straightSteps = maxD - minD;
            // Heurística = costo de pasos diagonales + costo de pasos rectos.
            return diagonalSteps * DIAG_COST + straightSteps;
        }
    }
    
    /**
     * Calcula la ruta más corta desde la celda de inicio (startR, startC) hasta la celda objetivo (goalR, goalC)
     * utilizando el algoritmo A* optimizado con heurística Manhattan.
     * Devuelve una lista de puntos (coordenadas) que representan el camino encontrado, o null si no hay ruta.
     */
    public List<Point> calcularRuta(int startR, int startC, int goalR, int goalC) {
        // Estructuras de datos eficientes para el algoritmo:
        // - PriorityQueue para los nodos abiertos (frontera), ordenada por el menor costo f = g + h.
        // - Matriz gCost para registrar el mejor costo g conocido por celda (inicializada con infinito).
        // - Matriz booleana closedSet para marcar nodos ya explorados (cerrados) y evitar reexplorarlos.
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        boolean[][] closedSet = new boolean[rows][cols];
        double[][] gCost = new double[rows][cols];
        
        // Inicializar los costos g con infinito.
        for (int i = 0; i < rows; i++) {
            Arrays.fill(gCost[i], Double.POSITIVE_INFINITY);
        }
        
        // Crear el nodo inicial con costo 0 y calcular su heurística.
        Node startNode = new Node(startR, startC, 0.0, heuristic(startR, startC, goalR, goalC), null);
        gCost[startR][startC] = 0.0;
        openSet.add(startNode);
        
        // Direcciones de movimiento (4 direcciones cardinales y 4 diagonales si se permiten).
        int[][] directionsCardinal = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        int[][] directionsDiagonal = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
        
        // Bucle principal de búsqueda.
        while (!openSet.isEmpty()) {
            // Extraer el nodo con menor f de la cola de prioridad.
            Node current = openSet.poll();
            
            // Si este nodo ya fue cerrado (ya se encontró un mejor camino hacia él), lo saltamos.
            if (closedSet[current.r][current.c]) {
                continue;
            }
            closedSet[current.r][current.c] = true;
            
            // Si llegamos al objetivo, reconstruimos la ruta leyendo los padres.
            if (current.r == goalR && current.c == goalC) {
                List<Point> path = new ArrayList<>();
                Node node = current;
                // Retroceder utilizando los punteros parent para armar la ruta.
                while (node != null) {
                    path.add(new Point(node.r, node.c));
                    node = node.parent;
                }
                Collections.reverse(path);  // invertir la lista para obtener de inicio a meta
                return path;
            }
            
            // Explorar los vecinos adyacentes (movimientos cardinales).
            for (int[] dir : directionsCardinal) {
                int nr = current.r + dir[0];
                int nc = current.c + dir[1];
                if (!isWalkable(nr, nc) || closedSet[nr][nc]) {
                    continue; // ignorar celdas fuera de rango, obstáculos o ya cerradas
                }
                // Costo para moverse al vecino (1 en movimientos horizontales/verticales por defecto).
                double stepCost = 1.0;
                // Si la celda tiene un peso/costo específico, se podría incorporar aquí:
                // e.g., stepCost *= peso[nr][nc];  (si tuviéramos una matriz de costos por celda)
                
                double newG = current.g + stepCost;
                // Si encontramos un camino más corto hacia el vecino, actualizamos y lo añadimos a la cola.
                if (newG < gCost[nr][nc]) {
                    gCost[nr][nc] = newG;
                    double hValue = heuristic(nr, nc, goalR, goalC);
                    Node neighbor = new Node(nr, nc, newG, hValue, current);
                    openSet.add(neighbor);
                }
            }
            // Si se permiten diagonales, explorar también los vecinos en diagonal.
            if (allowDiagonal) {
                for (int[] dir : directionsDiagonal) {
                    int nr = current.r + dir[0];
                    int nc = current.c + dir[1];
                    if (!isWalkable(nr, nc) || closedSet[nr][nc]) {
                        continue;
                    }
                    // Costo para moverse en diagonal (usamos DIAG_COST, típicamente ~1.414 si cost cardinal = 1).
                    double stepCost = DIAG_COST;
                    // Incluir peso de celda si aplica, similar al caso cardinal:
                    // stepCost *= peso[nr][nc];  (si hubiera costos variables por celda)
                    
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
        
        // Si se agotaron los nodos abiertos sin encontrar la meta, no existe ruta.
        return null;
    }
    
    /**
     * Devuelve la distancia (número de pasos) de la ruta más corta entre dos celdas.
     * Útil cuando solo interesa la distancia y no la ruta completa.
     */
    public double calcularDistancia(int startR, int startC, int goalR, int goalC) {
        List<Point> ruta = calcularRuta(startR, startC, goalR, goalC);
        return (ruta == null) ? Double.POSITIVE_INFINITY : ruta.size() - 1;
        // (ruta.size() - 1) da el número de movimientos, asumiendo costo unitario por paso.
    }
}
