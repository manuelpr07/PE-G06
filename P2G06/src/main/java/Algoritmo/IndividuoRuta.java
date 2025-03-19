package Algoritmo;

import java.util.Arrays;
import java.util.Random;
import Model.CasaMap;
import Model.Room;
import Algoritmo.AStar;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IndividuoRuta {
    private int[] ruta; // Permutación de 20 números (IDs de habitaciones)
    private double fitness; // Valor de fitness
    private static Random rand = new Random();
    
    public IndividuoRuta() {
        ruta = new int[20];
        for (int i = 0; i < 20; i++) {
            ruta[i] = i + 1;
        }
        // Mezclamos la permutación aleatoriamente
        for (int i = 0; i < 20; i++) {
            int j = rand.nextInt(20);
            int temp = ruta[i];
            ruta[i] = ruta[j];
            ruta[j] = temp;
        }
    }
    
    public int[] getRuta() {
        return ruta;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    // Calcula fitness usando solo la distancia total.
    private void calcularFitnessDistance(CasaMap map) {
        double totalDist = 0;
        List<Room> rooms = map.getRooms();
        AStar astar = new AStar(map.getGrid());
        // Trayecto: Base -> primera habitación
        Room firstRoom = rooms.get(ruta[0] - 1);
        totalDist += astar.calcularDistancia(map.getBaseRow(), map.getBaseCol(), firstRoom.getRow(), firstRoom.getCol());
        // Trayecto: entre habitaciones consecutivas
        for (int i = 0; i < ruta.length - 1; i++) {
            Room r1 = rooms.get(ruta[i] - 1);
            Room r2 = rooms.get(ruta[i+1] - 1);
            totalDist += astar.calcularDistancia(r1.getRow(), r1.getCol(), r2.getRow(), r2.getCol());
        }
        // Trayecto: última habitación -> Base
        Room lastRoom = rooms.get(ruta[ruta.length - 1] - 1);
        totalDist += astar.calcularDistancia(lastRoom.getRow(), lastRoom.getCol(), map.getBaseRow(), map.getBaseCol());
        
        fitness = totalDist;
    }
    
    // Calcula fitness que considera distancia total y penaliza curvas (suavidad)
    private void calcularFitnessDistanceSmooth(CasaMap map) {
        double totalDist = 0;
        List<Room> rooms = map.getRooms();
        AStar astar = new AStar(map.getGrid());
        List<Point> fullPath = new ArrayList<>();
        
        // Trayecto: Base -> primera habitación
        Room firstRoom = rooms.get(ruta[0] - 1);
        List<Point> segment = astar.calcularRuta(map.getBaseRow(), map.getBaseCol(), firstRoom.getRow(), firstRoom.getCol());
        if(segment != null) {
            fullPath.addAll(segment);
            totalDist += segment.size();
        } else {
            totalDist += 1e6;
        }
        
        // Trayectos entre habitaciones
        for (int i = 0; i < ruta.length - 1; i++) {
            Room r1 = rooms.get(ruta[i] - 1);
            Room r2 = rooms.get(ruta[i+1] - 1);
            segment = astar.calcularRuta(r1.getRow(), r1.getCol(), r2.getRow(), r2.getCol());
            if(segment != null && !segment.isEmpty()){
                if(!fullPath.isEmpty() && fullPath.get(fullPath.size()-1).equals(segment.get(0))){
                    segment.remove(0);
                }
                fullPath.addAll(segment);
                totalDist += segment.size();
            } else {
                totalDist += 1e6;
            }
        }
        
        // Trayecto: última habitación -> Base
        Room lastRoom = rooms.get(ruta[ruta.length - 1] - 1);
        segment = astar.calcularRuta(lastRoom.getRow(), lastRoom.getCol(), map.getBaseRow(), map.getBaseCol());
        if(segment != null && !segment.isEmpty()){
            if(!fullPath.isEmpty() && fullPath.get(fullPath.size()-1).equals(segment.get(0))){
                segment.remove(0);
            }
            fullPath.addAll(segment);
            totalDist += segment.size();
        } else {
            totalDist += 1e6;
        }
        
        // Calcular penalización por cambios de dirección (suavidad)
        double turningPenalty = 0;
        for (int i = 1; i < fullPath.size() - 1; i++) {
            Point p0 = fullPath.get(i - 1);
            Point p1 = fullPath.get(i);
            Point p2 = fullPath.get(i + 1);
            double vx1 = p1.x - p0.x;
            double vy1 = p1.y - p0.y;
            double vx2 = p2.x - p1.x;
            double vy2 = p2.y - p1.y;
            double norm1 = Math.sqrt(vx1 * vx1 + vy1 * vy1);
            double norm2 = Math.sqrt(vx2 * vx2 + vy2 * vy2);
            if(norm1 > 0 && norm2 > 0) {
                double cosTheta = (vx1 * vx2 + vy1 * vy2) / (norm1 * norm2);
                cosTheta = Math.max(-1, Math.min(1, cosTheta));
                double angle = Math.acos(cosTheta);
                turningPenalty += (1 - Math.cos(angle));
            }
        }
        // Peso para la penalización de curvas
        double weightTurn = 10.0;
        fitness = totalDist + weightTurn * turningPenalty;
    }
    
    // Método público: fitnessType 1 = Distancia Total, 2 = Distancia + Suavidad
    public void calcularFitness(CasaMap map, int fitnessType) {
        if(fitnessType == 1) {
            calcularFitnessDistance(map);
        } else if(fitnessType == 2) {
            calcularFitnessDistanceSmooth(map);
        } else {
            calcularFitnessDistance(map);
        }
    }
    
    @Override
    public String toString() {
        return Arrays.toString(ruta) + " Fitness: " + fitness;
    }
}
