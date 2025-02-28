package algoritmo;

/**
 * Clase que representa el algoritmo genético.
 * Aquí se crea y administra la población, se ejecutan las generaciones
 * y se calcula el promedio de fitness.
 */
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
    private boolean usarMutacionReales;
    private double precision;  // Precisión para la representación en binario

    // Variable global que indica qué función se está usando
    public static int opcionFuncionGlobal;

    /**
     * Constructor actualizado para incluir todos los parámetros.
     */
    public AlgoritmoGenetico(int tamPoblacion, int numVariables, double[] min, double[] max,
                              double probMutacion, double probCruce, double porcentajeElitismo,
                              String metodoSeleccion, String metodoCruce, int opcionFuncion, boolean esMaximizacion,
                              boolean usarMutacionReales, double precision) {
        // Se crea la población, pasando la precisión
        this.poblacion = new Poblacion(tamPoblacion, numVariables, min, max, esMaximizacion, precision);
        this.probMutacion = probMutacion;
        this.probCruce = probCruce;
        this.porcentajeElitismo = porcentajeElitismo;
        this.metodoSeleccion = metodoSeleccion;
        this.metodoCruce = metodoCruce;
        this.opcionFuncion = opcionFuncion;
        this.dimension = numVariables;
        this.esMaximizacion = esMaximizacion;
        this.usarMutacionReales = usarMutacionReales;
        this.precision = precision;
        
        // Se asigna la función global para que otros componentes la conozcan
        AlgoritmoGenetico.opcionFuncionGlobal = opcionFuncion;
    }

    // Método para actualizar la dimensión (usado para Michalewicz)
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    // Ejecuta una generación y retorna el mejor individuo de esa generación
    public Individuo ejecutar() {
        this.poblacion.evolucionar(probMutacion, probCruce, porcentajeElitismo, metodoSeleccion, metodoCruce, usarMutacionReales);
        return this.poblacion.getMejor();
    }

    // Calcula el promedio de fitness de la población
    public double calcularPromedioFitness() {
        return java.util.Arrays.stream(poblacion.getIndividuos())
                .mapToDouble(Individuo::getFitness)
                .average()
                .orElse(0.0);
    }

    // Retorna la población actual
    public Poblacion getPoblacion() {
        return this.poblacion;
    }
}
