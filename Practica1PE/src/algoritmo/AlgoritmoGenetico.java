package algoritmo;

import java.util.stream.IntStream;

public class AlgoritmoGenetico {
    private Poblacion poblacion;
    private double probMutacion;
    private double probCruce;
    private double porcentajeElitismo;
    private String metodoSeleccion;
    private String metodoCruce;
    private int opcionFuncion;
    private int dimension;
    private boolean esMaximizacion;

    public AlgoritmoGenetico(int tamPoblacion, int numVariables, double[] min, double[] max,
                              double probMutacion, double probCruce, double porcentajeElitismo,
                              String metodoSeleccion, String metodoCruce, int opcionFuncion, boolean esMaximizacion) {
        this.poblacion = new Poblacion(tamPoblacion, numVariables, min, max, esMaximizacion);
        this.probMutacion = probMutacion;
        this.probCruce = probCruce;
        this.porcentajeElitismo = porcentajeElitismo;
        this.metodoSeleccion = metodoSeleccion;
        this.metodoCruce = metodoCruce;
        this.opcionFuncion = opcionFuncion;
        this.dimension = numVariables;
        this.esMaximizacion = esMaximizacion;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public Individuo ejecutar() {
        this.poblacion.evolucionar(probMutacion, probCruce, porcentajeElitismo, metodoSeleccion, metodoCruce);
        return this.poblacion.getMejor();
    }

    public double calcularFitness(double[] variables) {
        double fitness = 0.0;
        switch (opcionFuncion) {
            case 1:
                fitness = 21.5 + variables[0] * Math.sin(4 * Math.PI * variables[0]) + variables[1] * Math.sin(20 * Math.PI * variables[1]);
                break;
            case 2:
                fitness = Math.sin(variables[1]) * Math.pow(Math.E, Math.pow(1 - Math.cos(variables[0]), 2))
                        + Math.cos(variables[0]) * Math.pow(Math.E, Math.pow(1 - Math.sin(variables[1]), 2))
                        + Math.pow(variables[0] - variables[1], 2);
                break;
            case 3:
                double sum1 = IntStream.rangeClosed(1, 5)
                        .mapToDouble(i -> i * Math.cos((i + 1) * variables[0] + i))
                        .sum();
                double sum2 = IntStream.rangeClosed(1, 5)
                        .mapToDouble(i -> i * Math.cos((i + 1) * variables[1] + i))
                        .sum();
                fitness = sum1 * sum2;
                break;
            case 4:
            case 5:
                fitness = calcularMichalewiczReal(variables);
                break;
            default:
                throw new IllegalArgumentException("Función no soportada");
        }
        return esMaximizacion ? fitness : -fitness; // Invertir el fitness para minimización
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
