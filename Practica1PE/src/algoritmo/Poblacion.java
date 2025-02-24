package algoritmo;

import java.util.Arrays;
import java.util.Random;

public class Poblacion {
    private Individuo[] individuos;
    private int tamPoblacion;
    private boolean esMaximizacion;

    private Random rand = new Random();

    public Poblacion(int tamPoblacion, int numVariables, double[] min, double[] max, boolean esMaximizacion) {
        this.tamPoblacion = tamPoblacion;
        this.individuos = new Individuo[tamPoblacion];
        this.esMaximizacion = esMaximizacion;

        // Inicializa la población con individuos aleatorios
        for (int i = 0; i < tamPoblacion; i++) {
            this.individuos[i] = new Individuo(numVariables, min, max);
        }
    }

    // Obtiene el mejor individuo de la población
    public Individuo getMejor() {
        if (esMaximizacion) {
            return Arrays.stream(individuos)
                         .max((i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()))
                         .orElse(null);
        } else {
            return Arrays.stream(individuos)
                         .min((i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()))
                         .orElse(null);
        }
    }

    // Método de selección por torneo
    public Individuo seleccionTorneo(int tamTorneo) {
        Individuo mejor = null;
        for (int i = 0; i < tamTorneo; i++) {
            Individuo candidato = individuos[rand.nextInt(tamPoblacion)];
            if (mejor == null || (esMaximizacion && candidato.getFitness() > mejor.getFitness()) || (!esMaximizacion && candidato.getFitness() < mejor.getFitness())) {
                mejor = candidato;
            }
        }
        return mejor;
    }

    // Método de selección por ruleta
    public Individuo seleccionRuleta() {
        double totalFitness = 0.0;
        for (Individuo ind : individuos) {
            totalFitness += ind.getFitness();
        }
        double randFitness = Math.random() * totalFitness;
        double acum = 0.0;
        for (Individuo ind : individuos) {
            acum += ind.getFitness();
            if (acum >= randFitness) {
                return ind;
            }
        }
        return individuos[individuos.length - 1];
    }

    // Selección según el método elegido
    public Individuo seleccionarPorMetodo(String metodo) {
        if (metodo.equals("Torneo")) {
            return seleccionTorneo(3);
        } else if (metodo.equals("Ruleta")) {
            return seleccionRuleta();
        } else {
            // Por defecto, usa torneo
            return seleccionTorneo(3);
        }
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

    // Método de cruce uniforme
    public Individuo[] cruceUniforme(Individuo p1, Individuo p2) {
        int tam = p1.cromosoma.length;
        Boolean[] hijo1 = new Boolean[tam];
        Boolean[] hijo2 = new Boolean[tam];

        for (int i = 0; i < tam; i++) {
            if (Math.random() < 0.5) {
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

    // Cruce según el método elegido
    public Individuo[] cruzar(String metodoCruce, Individuo p1, Individuo p2) {
        if (metodoCruce.equals("Monopunto")) {
            return cruceMonopunto(p1, p2);
        } else if (metodoCruce.equals("Uniforme")) {
            return cruceUniforme(p1, p2);
        } else {
            // Por defecto, usa monopunto
            return cruceMonopunto(p1, p2);
        }
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

    // Método para evolucionar la población (una generación) usando los nuevos parámetros
    public void evolucionar(double probMutacion, double probCruce, double porcentajeElitismo, String metodoSeleccion, String metodoCruce) {
        Individuo[] nuevaPoblacion = new Individuo[tamPoblacion];
        // Calcular número de individuos elite a copiar
        int numElite = (int) (porcentajeElitismo * tamPoblacion);
        // Ordenar la población según el tipo de optimización
        Individuo[] sorted = individuos.clone();
        if (esMaximizacion) {
            Arrays.sort(sorted, (i1, i2) -> Double.compare(i2.getFitness(), i1.getFitness()));
        } else {
            Arrays.sort(sorted, (i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()));
        }
        // Copiar los individuos elite sin modificarlos
        for (int i = 0; i < numElite; i++) {
            nuevaPoblacion[i] = sorted[i];
        }
        // Rellenar el resto de la población mediante reproducción
        int index = numElite;
        while (index < tamPoblacion) {
            Individuo padre1 = seleccionarPorMetodo(metodoSeleccion);
            Individuo padre2 = seleccionarPorMetodo(metodoSeleccion);
            Individuo[] hijos;
            if (Math.random() < probCruce) {
                hijos = cruzar(metodoCruce, padre1, padre2);
            } else {
                // Sin cruce: se copian los padres
                hijos = new Individuo[]{padre1, padre2};
            }
            nuevaPoblacion[index] = hijos[0];
            index++;
            if (index < tamPoblacion) {
                nuevaPoblacion[index] = hijos[1];
                index++;
            }
        }
        // Reemplazar la población antigua con la nueva
        this.individuos = nuevaPoblacion;
        // Aplicar mutación únicamente a los individuos no elite
        for (int i = numElite; i < tamPoblacion; i++) {
            Individuo ind = individuos[i];
            for (int j = 0; j < ind.cromosoma.length; j++) {
                if (rand.nextDouble() < probMutacion) {
                    ind.cromosoma[j] = !ind.cromosoma[j]; // Invierte el bit
                }
            }
        }
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
