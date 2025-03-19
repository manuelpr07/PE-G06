package Algoritmo;

import java.util.ArrayList;
import java.util.List;
import Model.CasaMap;

public class AlgorimoGeneticoRuta {
    private List<IndividuoRuta> poblacion;
    private int tamPoblacion;
    private int generaciones;
    private double probMutacion;
    private double probCruce;
    private CasaMap map;
    private int fitnessType; // 1 = Distancia Total, 2 = Distancia + Suavidad
    
    // Nuevos parámetros
    private int selectionMethod; // 0: Ruleta, 1: Torneo, etc.
    private int crossoverMethod; // 0: PMX, 1: OX, ...
    private int mutationMethod;  // 0: Inserción, 1: Intercambio, ...
    
    public AlgorimoGeneticoRuta(int tamPoblacion, int generaciones, double probMutacion, double probCruce, 
            CasaMap map, int fitnessType, int selectionMethod, int crossoverMethod, int mutationMethod) {
        this.tamPoblacion = tamPoblacion;
        this.generaciones = generaciones;
        this.probMutacion = probMutacion;
        this.probCruce = probCruce;
        this.map = map;
        this.fitnessType = fitnessType;
        this.selectionMethod = selectionMethod;
        this.crossoverMethod = crossoverMethod;
        this.mutationMethod = mutationMethod;
        inicializarPoblacion();
    }
    
    private void inicializarPoblacion() {
        poblacion = new ArrayList<>();
        for (int i = 0; i < tamPoblacion; i++) {
            IndividuoRuta ind = new IndividuoRuta();
            ind.calcularFitness(map, fitnessType);
            poblacion.add(ind);
        }
    }
    
    // Selección según el método: implementamos Ruleta (0) y Torneo (1); otros usan torneo
    private IndividuoRuta seleccionarPadre() {
        switch(selectionMethod) {
            case 0:
                return seleccionRuleta();
            case 1:
                return seleccionTorneo(3);
            default:
                return seleccionTorneo(3);
        }
    }
    
    private IndividuoRuta seleccionRuleta() {
        double total = 0;
        for (IndividuoRuta ind : poblacion) {
            total += 1.0 / ind.getFitness(); // para minimizar
        }
        double randVal = Math.random() * total;
        double acum = 0;
        for (IndividuoRuta ind : poblacion) {
            acum += 1.0 / ind.getFitness();
            if (acum >= randVal) return ind;
        }
        return poblacion.get(poblacion.size()-1);
    }
    
    private IndividuoRuta seleccionTorneo(int tamTorneo) {
        IndividuoRuta best = null;
        for (int i = 0; i < tamTorneo; i++) {
            IndividuoRuta candidate = poblacion.get((int)(Math.random() * tamPoblacion));
            if (best == null || candidate.getFitness() < best.getFitness())
                best = candidate;
        }
        return best;
    }
    
    public IndividuoRuta getMejor() {
        return poblacion.stream().min((a, b) -> Double.compare(a.getFitness(), b.getFitness())).orElse(null);
    }
    
    public void evolucionar() {
        for (int gen = 0; gen < generaciones; gen++) {
            List<IndividuoRuta> nuevaPoblacion = new ArrayList<>();
            while (nuevaPoblacion.size() < tamPoblacion) {
                IndividuoRuta padre1 = seleccionarPadre();
                IndividuoRuta padre2 = seleccionarPadre();
                IndividuoRuta[] hijos;
                if (Math.random() < probCruce) {
                    hijos = OperadoresGeneticos.crossover(padre1, padre2, crossoverMethod);
                } else {
                    hijos = new IndividuoRuta[]{ new IndividuoRuta(), new IndividuoRuta() };
                    System.arraycopy(padre1.getRuta(), 0, hijos[0].getRuta(), 0, padre1.getRuta().length);
                    System.arraycopy(padre2.getRuta(), 0, hijos[1].getRuta(), 0, padre2.getRuta().length);
                }
                for (IndividuoRuta hijo : hijos) {
                    if (Math.random() < probMutacion) {
                        OperadoresGeneticos.mutacion(hijo, mutationMethod);
                    }
                    hijo.calcularFitness(map, fitnessType);
                    nuevaPoblacion.add(hijo);
                    if (nuevaPoblacion.size() >= tamPoblacion) break;
                }
            }
            poblacion = nuevaPoblacion;
            System.out.println("Generación " + gen + " Mejor Fitness: " + getMejor().getFitness());
        }
    }
}
