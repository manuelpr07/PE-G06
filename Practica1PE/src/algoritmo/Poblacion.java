package algoritmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Esta clase representa la población de individuos.
 * Se encarga de inicializar la población, seleccionar individuos para cruce,
 * aplicar operadores genéticos (cruce y mutación) y evolucionar la población.
 */
public class Poblacion {
    private Individuo[] individuos;
    private int tamPoblacion;
    private boolean esMaximizacion;
    private double precision;  // Precisión para la representación en codificación binaria
    private Random rand = new Random();

    /**
     * Constructor de la población. Recibe el tamaño, número de variables, límites, 
     * el flag de maximización/minimización y la precisión.
     */
    public Poblacion(int tamPoblacion, int numVariables, double[] min, double[] max, boolean esMaximizacion, double precision) {
        this.tamPoblacion = tamPoblacion;
        this.esMaximizacion = esMaximizacion;
        this.precision = precision;
        this.individuos = new Individuo[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            this.individuos[i] = new Individuo(numVariables, min, max, precision);
        }
    }

    // Retorna el mejor individuo de la población (dependiendo del criterio de maximización o minimización)
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

    // Métodos de selección

    public Individuo seleccionTorneo(int tamTorneo) {
        Individuo mejor = null;
        for (int i = 0; i < tamTorneo; i++) {
            Individuo candidato = individuos[rand.nextInt(tamPoblacion)];
            if (mejor == null || (esMaximizacion && candidato.getFitness() > mejor.getFitness())
                    || (!esMaximizacion && candidato.getFitness() < mejor.getFitness())) {
                mejor = candidato;
            }
        }
        return mejor;
    }

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

    public Individuo seleccionTorneoProbabilistico(int tamTorneo) {
        Individuo[] candidatos = new Individuo[tamTorneo];
        for (int i = 0; i < tamTorneo; i++) {
            candidatos[i] = individuos[rand.nextInt(tamPoblacion)];
        }
        Arrays.sort(candidatos, (i1, i2) -> esMaximizacion 
                ? Double.compare(i2.getFitness(), i1.getFitness())
                : Double.compare(i1.getFitness(), i2.getFitness()));
        double p = 0.75;
        return Math.random() < p ? candidatos[0] : candidatos[rand.nextInt(tamTorneo)];
    }

    public Individuo seleccionEstocasticoUniversal() {
        double totalFitness = 0.0;
        for (Individuo ind : individuos) {
            totalFitness += ind.getFitness();
        }
        double spacing = totalFitness / tamPoblacion;
        double start = Math.random() * spacing;
        double cumulativeSum = 0.0;
        Individuo[] pool = new Individuo[tamPoblacion];
        int count = 0;
        for (int i = 0; i < tamPoblacion; i++) {
            cumulativeSum += individuos[i].getFitness();
            while (count < tamPoblacion && cumulativeSum >= start + count * spacing) {
                pool[count] = individuos[i];
                count++;
            }
        }
        return pool[rand.nextInt(tamPoblacion)];
    }

    public Individuo seleccionTruncamiento() {
        int cutoff = (int)(0.5 * tamPoblacion);
        Individuo[] sorted = individuos.clone();
        if (esMaximizacion) {
            Arrays.sort(sorted, (i1, i2) -> Double.compare(i2.getFitness(), i1.getFitness()));
        } else {
            Arrays.sort(sorted, (i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()));
        }
        return sorted[rand.nextInt(cutoff)];
    } 

    public Individuo seleccionRestos() {
        double sumFitness = 0.0;
        for (Individuo ind : individuos) {
            sumFitness += ind.getFitness();
        }
        double averageFitness = sumFitness / tamPoblacion;
        ArrayList<Individuo> pool = new ArrayList<>();
        double[] fractional = new double[tamPoblacion];
        for (int i = 0; i < tamPoblacion; i++) {
            double expected = individuos[i].getFitness() / averageFitness;
            int copies = (int) Math.floor(expected);
            for (int j = 0; j < copies; j++) {
                pool.add(individuos[i]);
            }
            fractional[i] = expected - Math.floor(expected);
        }
        if (!pool.isEmpty()) {
            return pool.get(rand.nextInt(pool.size()));
        } else {
            double totalFraction = 0.0;
            for (double f : fractional) {
                totalFraction += f;
            }
            double randVal = Math.random() * totalFraction;
            double acc = 0.0;
            for (int i = 0; i < tamPoblacion; i++) {
                acc += fractional[i];
                if (acc >= randVal) {
                    return individuos[i];
                }
            }
        }
        return individuos[tamPoblacion - 1];
    }

    public Individuo seleccionarPorMetodo(String metodo) {
        switch (metodo) {
            case "TorneoProbabilistico": return seleccionTorneoProbabilistico(3);
            case "EstocasticoUniversal": return seleccionEstocasticoUniversal();
            case "Truncamiento":         return seleccionTruncamiento();
            case "Restos":              return seleccionRestos();
            case "Torneo":              return seleccionTorneo(3);
            case "Ruleta":              return seleccionRuleta();
            default:                    return seleccionTorneo(3);
        }
    }

    // -----------------------------
    // Operadores de CRUCE para INDIVIDUOS BINARIOS
    // -----------------------------
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
        Individuo hijoA = new Individuo(p1.min.length, p1.min, p1.max, precision);
        Individuo hijoB = new Individuo(p2.min.length, p2.min, p2.max, precision);
        hijoA.cromosoma = hijo1;
        hijoB.cromosoma = hijo2;
        return new Individuo[]{hijoA, hijoB};
    }

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
        Individuo hijoA = new Individuo(p1.min.length, p1.min, p1.max, precision);
        Individuo hijoB = new Individuo(p2.min.length, p2.min, p2.max, precision);
        hijoA.cromosoma = hijo1;
        hijoB.cromosoma = hijo2;
        return new Individuo[]{hijoA, hijoB};
    }

    // -----------------------------
    // Operadores de CRUCE para INDIVIDUOS REALES (Función 5)
    // -----------------------------
    public Individuo[] cruceMonopuntoReal(Individuo p1, Individuo p2) {
        int dim = p1.genes.length;
        int punto = rand.nextInt(dim);
        Individuo hijoA = new Individuo(dim, p1.min, p1.max, precision);
        Individuo hijoB = new Individuo(dim, p1.min, p1.max, precision);
        for (int i = 0; i < dim; i++) {
            if (i < punto) {
                hijoA.genes[i] = p1.genes[i];
                hijoB.genes[i] = p2.genes[i];
            } else {
                hijoA.genes[i] = p2.genes[i];
                hijoB.genes[i] = p1.genes[i];
            }
        }
        return new Individuo[]{hijoA, hijoB};
    }

    public Individuo[] cruceUniformeReal(Individuo p1, Individuo p2) {
        int dim = p1.genes.length;
        Individuo hijoA = new Individuo(dim, p1.min, p1.max, precision);
        Individuo hijoB = new Individuo(dim, p1.min, p1.max, precision);
        for (int i = 0; i < dim; i++) {
            if (Math.random() < 0.5) {
                hijoA.genes[i] = p1.genes[i];
                hijoB.genes[i] = p2.genes[i];
            } else {
                hijoA.genes[i] = p2.genes[i];
                hijoB.genes[i] = p1.genes[i];
            }
        }
        return new Individuo[]{hijoA, hijoB};
    }

    public Individuo[] cruceAritmetico(Individuo p1, Individuo p2) {
        int dim = p1.genes.length;
        Individuo hijoA = new Individuo(dim, p1.min, p1.max, precision);
        Individuo hijoB = new Individuo(dim, p1.min, p1.max, precision);
        for (int i = 0; i < dim; i++) {
            double alfa = Math.random();
            hijoA.genes[i] = alfa * p1.genes[i] + (1 - alfa) * p2.genes[i];
            hijoB.genes[i] = alfa * p2.genes[i] + (1 - alfa) * p1.genes[i];
        }
        return new Individuo[]{hijoA, hijoB};
    }

    public Individuo[] cruceBLXAlpha(Individuo p1, Individuo p2, double alpha) {
        int dim = p1.genes.length;
        Individuo hijoA = new Individuo(dim, p1.min, p1.max, precision);
        Individuo hijoB = new Individuo(dim, p1.min, p1.max, precision);
        for (int i = 0; i < dim; i++) {
            double cMin = Math.min(p1.genes[i], p2.genes[i]);
            double cMax = Math.max(p1.genes[i], p2.genes[i]);
            double I = cMax - cMin;
            double lower = cMin - alpha * I;
            double upper = cMax + alpha * I;
            hijoA.genes[i] = lower + rand.nextDouble() * (upper - lower);
            hijoB.genes[i] = lower + rand.nextDouble() * (upper - lower);
            hijoA.genes[i] = Math.max(p1.min[i], Math.min(hijoA.genes[i], p1.max[i]));
            hijoB.genes[i] = Math.max(p1.min[i], Math.min(hijoB.genes[i], p1.max[i]));
        }
        return new Individuo[]{hijoA, hijoB};
    }

    /**
     * Método que selecciona el operador de cruce según el método escogido y la codificación.
     */
    public Individuo[] cruzar(String metodoCruce, Individuo p1, Individuo p2) {
        if (AlgoritmoGenetico.opcionFuncionGlobal == 5) { // Usar operadores reales
            switch(metodoCruce) {
                case "Monopunto": return cruceMonopuntoReal(p1, p2);
                case "Uniforme": return cruceUniformeReal(p1, p2);
                case "Aritmético": return cruceAritmetico(p1, p2);
                case "BLX-α": return cruceBLXAlpha(p1, p2, 0.5);
                default: return cruceBLXAlpha(p1, p2, 0.5);
            }
        } else { // Operadores binarios
            if (metodoCruce.equals("Uniforme")) {
                return cruceUniforme(p1, p2);
            } else {
                return cruceMonopunto(p1, p2);
            }
        }
    }

    // -----------------------------
    // Mutación
    // -----------------------------
    public void mutacion(double probMutacion, boolean usarMutacionReales) {
        if (AlgoritmoGenetico.opcionFuncionGlobal == 5) {
            if (usarMutacionReales) {
                for (Individuo ind : individuos) {
                    mutacionReal(ind, probMutacion);
                }
            }
        } else {
            for (Individuo ind : individuos) {
                for (int i = 0; i < ind.cromosoma.length; i++) {
                    if (rand.nextDouble() < probMutacion) {
                        ind.cromosoma[i] = !ind.cromosoma[i];
                    }
                }
            }
        }
    }

    // Mutación para individuos reales: se añade una pequeña perturbación
    public void mutacionReal(Individuo ind, double probMutacion) {
        for (int i = 0; i < ind.genes.length; i++) {
            if (rand.nextDouble() < probMutacion) {
                double perturbacion = (rand.nextDouble() - 0.5) * 0.2; // Puedes ajustar la magnitud
                double nuevo = ind.genes[i] + perturbacion;
                nuevo = Math.max(ind.min[i], Math.min(nuevo, ind.max[i]));
                ind.genes[i] = nuevo;
            }
        }
    }

    /**
     * Evoluciona la población una generación aplicando selección, cruce y mutación.
     */
    public void evolucionar(double probMutacion, double probCruce, double porcentajeElitismo, 
                            String metodoSeleccion, String metodoCruce, boolean usarMutacionReales) {
        Individuo[] nuevaPoblacion = new Individuo[tamPoblacion];
        Individuo[] sorted = individuos.clone();
        if (esMaximizacion) {
            Arrays.sort(sorted, (i1, i2) -> Double.compare(i2.getFitness(), i1.getFitness()));
        } else {
            Arrays.sort(sorted, (i1, i2) -> Double.compare(i1.getFitness(), i2.getFitness()));
        }
        int numElite = (int) Math.round(porcentajeElitismo * tamPoblacion);
        if (numElite > tamPoblacion) { numElite = tamPoblacion; }
        for (int i = 0; i < numElite; i++) {
            nuevaPoblacion[i] = sorted[i];
        }
        int index = numElite;
        while (index < tamPoblacion) {
            Individuo padre1 = seleccionarPorMetodo(metodoSeleccion);
            Individuo padre2 = seleccionarPorMetodo(metodoSeleccion);
            Individuo[] hijos;
            if (Math.random() < probCruce) {
                hijos = cruzar(metodoCruce, padre1, padre2);
            } else {
                hijos = new Individuo[]{padre1, padre2};
            }
            nuevaPoblacion[index] = hijos[0];
            index++;
            if (index < tamPoblacion) {
                nuevaPoblacion[index] = hijos[1];
                index++;
            }
        }
        // Aplicar mutación a los no élites
        mutacion(probMutacion, usarMutacionReales);
        this.individuos = nuevaPoblacion;
    }

    public Individuo[] getIndividuos() {
        return individuos;
    }

    public void imprimir() {
        for (Individuo ind : individuos) {
            ind.imprimir();
            System.out.println("---------------------");
        }
    }
}
