package algoritmo;

import java.util.Random;

public class Individuo {
    public Boolean[] cromosoma;
    public double[] min;
    public double[] max;
    public int[] tamGenes;
    public double precision = 0.001;
    private Random rand = new Random();

    public Individuo(int numVariables, double[] min, double[] max) {
        this.min = min;
        this.max = max;
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

    private int calcularTamGen(double min, double max) {
        return (int) (Math.log10(((max - min) / precision) + 1) / Math.log10(2));
    }

    public double getFenotipo(int index) {
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

    public double getFitness() {
        double[] fenotipos = new double[min.length];
        for (int i = 0; i < fenotipos.length; i++) {
            fenotipos[i] = getFenotipo(i);
        }

        return 21.5 + fenotipos[0] * Math.sin(4 * Math.PI * fenotipos[0])
                + (fenotipos.length > 1 ? fenotipos[1] * Math.sin(20 * Math.PI * fenotipos[1]) : 0);
    }

    public void imprimir() {
        System.out.print("Cromosoma: ");
        for (Boolean bit : cromosoma) {
            System.out.print(bit ? "1" : "0");
        }
        System.out.println("\nFenotipos: ");
        for (int i = 0; i < min.length; i++) {
            System.out.println("x" + (i + 1) + " = " + getFenotipo(i));
        }
        System.out.println("Fitness: " + getFitness());
    }
}
