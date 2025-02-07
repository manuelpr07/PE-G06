/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algoritmo;

/**
 *
 * @author dorje
 */

import java.util.Arrays;
import java.util.Random;

public class Poblacion {
    private Individuo[] individuos;
    private int tamPoblacion;
    private Random rand = new Random();

    public Poblacion(int tamPoblacion, int numVariables, double[] min, double[] max) {
        this.tamPoblacion = tamPoblacion;
        this.individuos = new Individuo[tamPoblacion];

        // Inicializa la población con individuos aleatorios
        for (int i = 0; i < tamPoblacion; i++) {
            this.individuos[i] = new Individuo(numVariables, min, max);
        }
    }

    // Obtiene el mejor individuo de la población (el de mayor fitness)
    public Individuo getMejor() {
        return Arrays.stream(individuos)
                     .max((i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()))
                     .orElse(null);
    }

    // Método de selección por torneo
    public Individuo seleccionTorneo(int tamTorneo) {
        Individuo mejor = null;
        for (int i = 0; i < tamTorneo; i++) {
            Individuo candidato = individuos[rand.nextInt(tamPoblacion)];
            if (mejor == null || candidato.getFitness() > mejor.getFitness()) {
                mejor = candidato;
            }
        }
        return mejor;
    }

    // Método de cruce monopunto
    public Individuo[] cruceMonopunto(Individuo p1, Individuo p2) {
        int tam = p1.cromosoma.length;
        int puntoCruce = rand.nextInt(tam);

        Boolean[] hijo1 = new Boolean[tam];
        Boolean[] hijo2 = new Boolean[tam];

        for (int i = 0; i < tam; i++) {
            if (i < puntoCruce) {
                hijo1[i] = p1.cromosoma[i];
                hijo2[i] = p2.cromosoma[i];
            } else {
                hijo1[i] = p2.cromosoma[i];
                hijo2[i] = p1.cromosoma[i];
            }
        }

        Individuo hijoA = new Individuo(p1.min.length, p1.min, p1.max);
        Individuo hijoB = new Individuo(p2.min.length, p2.min, p2.max);
        hijoA.cromosoma = hijo1;
        hijoB.cromosoma = hijo2;

        return new Individuo[]{hijoA, hijoB};
    }

    // Método de mutación con probabilidad
    public void mutacion(double probMutacion) {
        for (Individuo ind : individuos) {
            for (int i = 0; i < ind.cromosoma.length; i++) {
                if (rand.nextDouble() < probMutacion) {
                    ind.cromosoma[i] = !ind.cromosoma[i]; // Invierte el bit
                }
            }
        }
    }

    // Método para evolucionar la población (una generación)
    public void evolucionar(double probMutacion) {
        Individuo[] nuevaPoblacion = new Individuo[tamPoblacion];

        // Mantener el mejor (elitismo)
        nuevaPoblacion[0] = getMejor();

        for (int i = 1; i < tamPoblacion; i += 2) {
            Individuo padre1 = seleccionTorneo(3);
            Individuo padre2 = seleccionTorneo(3);
            Individuo[] hijos = cruceMonopunto(padre1, padre2);
            nuevaPoblacion[i] = hijos[0];
            if (i + 1 < tamPoblacion) {
                nuevaPoblacion[i + 1] = hijos[1];
            }
        }

        // Reemplazar la población antigua con la nueva
        this.individuos = nuevaPoblacion;

        // Aplicar mutación
        mutacion(probMutacion);
    }

    public Individuo[] getIndividuos() {
    return individuos;
}



    
    // Imprimir la población
    public void imprimir() {
        for (Individuo ind : individuos) {
            ind.imprimir();
            System.out.println("---------------------");
        }
    }
}
