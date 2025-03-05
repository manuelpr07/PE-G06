package algoritmo;

import java.util.Random;

/**
 * Clase que representa un individuo.
 * Se utiliza codificación binaria para funciones 1-4 y codificación real para la función 5.
 * La precisión es usada en la representación binaria para determinar el número de bits.
 */
public class Individuo {
    // Para codificación binaria
    public Boolean[] cromosoma;
    public int[] tamGenes;
    public double precision;  // La precisión es configurable

    // Para codificación real (Función 5)
    public double[] genes;

    // Límites de cada variable
    public double[] min;
    public double[] max;

    private Random rand = new Random();

    /**
     * Constructor que recibe el número de variables, límites y precisión.
     * Dependiendo de la función seleccionada (opción 5 => real), se inicializa con genes reales,
     * de lo contrario, se usa codificación binaria.
     */
    public Individuo(int numVariables, double[] min, double[] max, double precision) {
        this.min = min;
        this.max = max;
        this.precision = precision;
        int func = AlgoritmoGenetico.opcionFuncionGlobal;
        if (func == 5) {
            // Inicialización para codificación real
            this.genes = new double[numVariables];
            for (int i = 0; i < numVariables; i++) {
                this.genes[i] = min[i] + rand.nextDouble() * (max[i] - min[i]);
            }
        } else {
            // Inicialización para codificación binaria
            this.tamGenes = new int[numVariables];
            int tamTotal = 0;
            for (int i = 0; i < numVariables; i++) {
                this.tamGenes[i] = calcularTamGen(min[i], max[i]);
                tamTotal += this.tamGenes[i];
            }
            this.cromosoma = new Boolean[tamTotal];
            for (int i = 0; i < tamTotal; i++) {
                this.cromosoma[i] = rand.nextBoolean();
            }
        }
    }

    // Calcula el número de bits necesarios para representar el rango [min, max] con la precisión dada
    private int calcularTamGen(double min, double max) {
        return (int) (Math.log10(((max - min) / precision) + 1) / Math.log10(2));
    }

    /**
     * Devuelve el fenotipo (valor real) de la variable en la posición 'index'.
     * Si es función 5 se devuelve directamente el gen real; en caso contrario, se decodifica el cromosoma.
     */
    public double getFenotipo(int index) {
        int func = AlgoritmoGenetico.opcionFuncionGlobal;
        if (func == 5) {
            return genes[index];
        } else {
            if (index >= min.length || index >= tamGenes.length) {
                throw new IndexOutOfBoundsException("Índice fuera del rango del fenotipo.");
            }
            int inicio = 0;
            for (int i = 0; i < index; i++) {
                inicio += tamGenes[i];
            }
            int fin = inicio + tamGenes[index];
            int valor = 0;
            for (int i = inicio; i < fin; i++) {
                valor = (valor << 1) | (cromosoma[i] ? 1 : 0);
            }
            return min[index] + valor * (max[index] - min[index]) / (Math.pow(2, tamGenes[index]) - 1);
        }
    }

    /**
     * Calcula el fitness del individuo según la función seleccionada.
     * Se usan fórmulas diferentes para cada función.
     */
    public double getFitness() {
        double[] fenotipos = new double[min.length];
        for (int i = 0; i < min.length; i++) {
            fenotipos[i] = getFenotipo(i);
        }
        double fitness = 0.0;
        int op = AlgoritmoGenetico.opcionFuncionGlobal;
        double m;
        switch (op) {
            case 1:
                // Función 1: Maximización
                fitness = 21.5 + fenotipos[0] * Math.sin(4 * Math.PI * fenotipos[0]);
                if (fenotipos.length > 1) {
                    fitness += fenotipos[1] * Math.sin(20 * Math.PI * fenotipos[1]);
                }
                break;
            case 2:
                // Mishra Bird (Minimización)
                fitness = Math.sin(fenotipos[1]) * Math.exp(Math.pow(1 - Math.cos(fenotipos[0]), 2))
                        + Math.cos(fenotipos[0]) * Math.exp(Math.pow(1 - Math.sin(fenotipos[1]), 2))
                        + Math.pow(fenotipos[0] - fenotipos[1], 2);
                break;
            case 3:
                // Schubert (Minimización)
                double sum1 = 0, sum2 = 0;
                for (int i = 1; i <= 5; i++) {
                    sum1 += i * Math.cos((i + 1) * fenotipos[0] + i);
                    sum2 += i * Math.cos((i + 1) * fenotipos[1] + i);
                }
                fitness = sum1 * sum2;
                break;
            case 4:
                // Michalewicz (Binario) Minimización
                m = 10;
                for (int i = 0; i < fenotipos.length; i++) {
                    int j = i + 1;
                    fitness -= Math.sin(fenotipos[i]) * Math.pow(Math.sin((j * Math.pow(fenotipos[i], 2)) / Math.PI), 2* m);
                }
                break;
            case 5:
                // Michalewicz (Real) Minimización
                m = 10;
                for (int i = 0; i < fenotipos.length; i++) {
                    int j = i + 1;
                    fitness -= Math.sin(fenotipos[i]) * Math.pow(Math.sin((j * Math.pow(fenotipos[i], 2)) / Math.PI), 2 * m);
                }
                break;
            default:
                throw new IllegalArgumentException("Función no soportada");
        }
        return fitness;
    }

    /**
     * Imprime por consola los datos del individuo, incluyendo cromosoma (o genes),
     * los fenotipos y el fitness.
     */
    public void imprimir() {
        int func = AlgoritmoGenetico.opcionFuncionGlobal;
        System.out.println("Función elegida: " + func);
        if (func == 5) {
            System.out.print("Genes (reales): ");
            for (double g : genes) {
                System.out.printf("%.4f ", g);
            }
            System.out.println();
        } else {
            System.out.print("Cromosoma binario: ");
            for (Boolean bit : cromosoma) {
                System.out.print(bit ? "1" : "0");
            }
            System.out.println();
        }
        System.out.println("Fenotipos:");
        for (int i = 0; i < min.length; i++) {
            System.out.println("  x" + (i + 1) + " = " + getFenotipo(i));
        }
        System.out.println("Fitness: " + getFitness());
        System.out.println("---------------------");
    }
}
