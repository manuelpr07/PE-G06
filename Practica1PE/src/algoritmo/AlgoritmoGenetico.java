package algoritmo;

import java.util.stream.IntStream;

public class AlgoritmoGenetico {
    private Poblacion poblacion;
    private double probMutacion;
    private int opcionFuncion;
    private int dimension;

    public AlgoritmoGenetico(int tamPoblacion, int numVariables, double[] min, double[] max, double probMutacion, int opcionFuncion) {
        this.poblacion = new Poblacion(tamPoblacion, numVariables, min, max);
        this.probMutacion = probMutacion;
        this.opcionFuncion = opcionFuncion;
        this.dimension = numVariables;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public Individuo ejecutar() {
        this.poblacion.evolucionar(probMutacion);
        return this.poblacion.getMejor();
    }

    public double calcularFitness(double[] variables) {
        switch (opcionFuncion) {
            case 1:
                return 21.5 + variables[0] * Math.sin(4 * Math.PI * variables[0]) + variables[1] * Math.sin(20 * Math.PI * variables[1]);
            case 2:
                return Math.sin(variables[1]) * Math.pow(Math.E, Math.pow(1 - Math.cos(variables[0]), 2))
                        + Math.cos(variables[0]) * Math.pow(Math.E, Math.pow(1 - Math.sin(variables[1]), 2))
                        + Math.pow(variables[0] - variables[1], 2);
            case 3:
                double sum1 = IntStream.rangeClosed(1, 5)
                        .mapToDouble(i -> i * Math.cos((i + 1) * variables[0] + i))
                        .sum();
                double sum2 = IntStream.rangeClosed(1, 5)
                        .mapToDouble(i -> i * Math.cos((i + 1) * variables[1] + i))
                        .sum();
                return sum1 * sum2;
            case 4:
            case 5:
                return calcularMichalewiczReal(variables);
            default:
                throw new IllegalArgumentException("Función no soportada");
        }
    }

    private double calcularMichalewiczReal(double[] variables) {
        double m = 10;
        return -IntStream.range(0, dimension)
                .mapToDouble(i -> Math.sin(variables[i]) * Math.pow(Math.sin((i + 1) * Math.pow(variables[i], 2) / Math.PI), 2 * m))
                .sum();
    }

    public double calcularPromedioFitness() {
        return IntStream.range(0, poblacion.getIndividuos().length)
                .mapToDouble(i -> poblacion.getIndividuos()[i].getFitness())
                .average()
                .orElse(0.0);
    }

    public Poblacion getPoblacion() {
        return this.poblacion;
    }
}
