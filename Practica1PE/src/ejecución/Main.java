package ejecución;

import algoritmo.AlgoritmoGenetico;
import algoritmo.Individuo;
import javax.swing.*;
import java.awt.*;
import org.math.plot.Plot2DPanel;

/**
 * Este Main es el punto de entrada del programa.
 * Se muestra una interfaz gráfica donde el usuario configura los parámetros del algoritmo
 * (tamaño de población, generaciones, métodos, precisión, etc.).
 * Al final, se imprime en la consola el mejor y el peor fitness junto a sus fenotipos,
 * y se grafica la evolución del fitness (con puntos destacados para el óptimo y el peor).
 */
public class Main {
    public static void main(String[] args) {
        // Configuración básica de la ventana
        JFrame frame = new JFrame("Configuración del Algoritmo Genético");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        
        // Panel con layout en grid para organizar la interfaz
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(Color.cyan);
    
        // Campos de entrada para los parámetros básicos
        JTextField txtTamPoblacion = new JTextField("100");
        JTextField txtGeneraciones = new JTextField("100");
        JTextField txtPorcentajeMutacion = new JTextField("5");
        JTextField txtDimension = new JTextField("2");
        txtDimension.setEnabled(false);  // Solo se habilita para funciones 4 y 5 (Michalewicz)
        
        JTextField txtPorcentajeCruce = new JTextField("60");
        JTextField txtPorcentajeElitismo = new JTextField("0");
        // Campo para la precisión (relevante en la codificación binaria)
        JTextField txtPrecision = new JTextField("0.001");

        // Opciones para el método de selección
        String[] opcionesSeleccion = {
            "Torneo", 
            "Ruleta", 
            "Torneo Probabilistico", 
            "Estocastico Universal", 
            "Truncamiento", 
            "Restos"
        };
        JComboBox<String> comboSeleccion = new JComboBox<>(opcionesSeleccion);

        // Opciones para el método de cruce (incluye opciones para reales)
        String[] opcionesCruce = {"Monopunto", "Uniforme", "Aritmético", "BLX-α"};
        JComboBox<String> comboCruce = new JComboBox<>(opcionesCruce);

        // Opciones de la función a optimizar
        JComboBox<String> comboFuncion = new JComboBox<>(new String[]{
                "1. Función 1: Maximización (2D)",
                "2. Mishra Bird (2D) Minimización",
                "3. Schubert (2D) Minimización",
                "4. Michalewicz (Binario)",
                "5. Michalewicz (Real)"
        });
        comboFuncion.addActionListener(e -> {
            // Habilitamos el campo dimensión solo para las funciones de Michalewicz (binario o real)
            int opcion = comboFuncion.getSelectedIndex() + 1;
            txtDimension.setEnabled(opcion == 4 || opcion == 5);
        });

        // Checkbox para mutación real (aplicable en la función 5)
        JCheckBox chkMutacionReales = new JCheckBox("Mutación sobre reales");
        // Por defecto, desmarcado (según tu solicitud)
        chkMutacionReales.setSelected(false);

        // Agregar todos los campos al panel con etiquetas
        panel.add(new JLabel("Tamaño de la Población:"));
        panel.add(txtTamPoblacion);
        panel.add(new JLabel("Número de Generaciones:"));
        panel.add(txtGeneraciones);
        panel.add(new JLabel("Porcentaje de Mutación (%):"));
        panel.add(txtPorcentajeMutacion);
        panel.add(new JLabel("Porcentaje de Cruce (%):"));
        panel.add(txtPorcentajeCruce);
        panel.add(new JLabel("Porcentaje de Elitismo (%):"));
        panel.add(txtPorcentajeElitismo);
        panel.add(new JLabel("Precisión de la representación:"));
        panel.add(txtPrecision);
        panel.add(new JLabel("Método de Selección:"));
        panel.add(comboSeleccion);
        panel.add(new JLabel("Método de Cruce:"));
        panel.add(comboCruce);
        panel.add(new JLabel("Dimensión (Solo para Función 4 y 5):"));
        panel.add(txtDimension);
        panel.add(new JLabel("Selecciona la Función a Optimizar:"));
        panel.add(comboFuncion);
        panel.add(new JLabel("Mutación sobre reales:"));
        panel.add(chkMutacionReales);

        // Botón para ejecutar el algoritmo
        JButton btnEjecutar = new JButton("Ejecutar");
        panel.add(btnEjecutar);

        frame.add(panel);
        frame.setVisible(true);

        // Acción al presionar el botón "Ejecutar"
        btnEjecutar.addActionListener(e -> {
            try {
                // Lectura de parámetros desde la interfaz
                int tamPoblacion = Integer.parseInt(txtTamPoblacion.getText());
                int generaciones = Integer.parseInt(txtGeneraciones.getText());
                double probMutacion = Integer.parseInt(txtPorcentajeMutacion.getText()) / 100.0;
                int dimension = Integer.parseInt(txtDimension.getText());
                int opcionFuncion = comboFuncion.getSelectedIndex() + 1;
                double probCruce = Integer.parseInt(txtPorcentajeCruce.getText()) / 100.0;
                double porcentajeElitismo = Integer.parseInt(txtPorcentajeElitismo.getText()) / 100.0;
                double precision = Double.parseDouble(txtPrecision.getText());
                String metodoSeleccion = comboSeleccion.getSelectedItem().toString();
                String metodoCruce = comboCruce.getSelectedItem().toString();

                // Determinamos si se trata de un problema de minimización
                boolean esMinimizacion = (opcionFuncion == 2 || opcionFuncion == 3 ||
                                          opcionFuncion == 4 || opcionFuncion == 5);

                // Configuración de los límites según la función
                double[] min, max;
                if (opcionFuncion == 4 || opcionFuncion == 5) {
                    // Para Michalewicz, el dominio es [0, π] para cada variable
                    min = new double[dimension];
                    max = new double[dimension];
                    for (int i = 0; i < dimension; i++) {
                        min[i] = 0.0;
                        max[i] = Math.PI;
                    }
                } else {
                    if (opcionFuncion == 1) {
                        min = new double[]{-3.0, 4.1};
                        max = new double[]{12.1, 5.8};
                    } else if (opcionFuncion == 2) {
                        min = new double[]{-10.0, -6.5};
                        max = new double[]{0.0, 0.0};
                    } else if (opcionFuncion == 3) {
                        min = new double[]{-10.0, -10.0};
                        max = new double[]{10.0, 10.0};
                    } else {
                        min = new double[]{-10.0, -10.0};
                        max = new double[]{10.0, 10.0};
                    }
                }

                // Determinamos si usar la mutación real (checkbox)
                boolean usarMutacionReales = chkMutacionReales.isSelected();

                // Creamos el objeto del algoritmo genético
                algoritmo.AlgoritmoGenetico ag = new algoritmo.AlgoritmoGenetico(
                        tamPoblacion,
                        min.length,
                        min,
                        max,
                        probMutacion,
                        probCruce,
                        porcentajeElitismo,
                        metodoSeleccion,
                        metodoCruce,
                        opcionFuncion,
                        !esMinimizacion,
                        usarMutacionReales,
                        precision
                );
                if (opcionFuncion == 4 || opcionFuncion == 5) {
                    ag.setDimension(dimension);
                }

                // Variables para llevar el seguimiento del mejor y peor fitness global
                Individuo mejorIndividuoAbsoluto = null;
                double mejorAbsoluto = esMinimizacion ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
                Individuo peorIndividuoAbsoluto = null;
                double peorAbsoluto = esMinimizacion ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

                // Arrays para guardar la traza de la evolución
                double[] mejoresAbsolutos = new double[generaciones];
                double[] mejoresGeneracion = new double[generaciones];
                double[] promediosGeneracion = new double[generaciones];

                // Bucle principal de generaciones
                for (int gen = 0; gen < generaciones; gen++) {
                    // Ejecutamos una generación y obtenemos el mejor individuo de esa generación
                    Individuo mejorGen = ag.ejecutar();
                    double fitGen = mejorGen.getFitness();

                    mejoresGeneracion[gen] = fitGen;
                    promediosGeneracion[gen] = ag.calcularPromedioFitness();

                    // Actualizamos el mejor global según minimización o maximización
                    if ((esMinimizacion && fitGen < mejorAbsoluto) ||
                        (!esMinimizacion && fitGen > mejorAbsoluto)) {
                        mejorAbsoluto = fitGen;
                        mejorIndividuoAbsoluto = mejorGen;
                    }
                    mejoresAbsolutos[gen] = mejorAbsoluto;

                    // Actualizamos el peor global (para minimización, peor es el mayor; para maximización, peor es el menor)
                    if (esMinimizacion) {
                        if (fitGen > peorAbsoluto) {
                            peorAbsoluto = fitGen;
                            peorIndividuoAbsoluto = mejorGen;
                        }
                    } else {
                        if (fitGen < peorAbsoluto) {
                            peorAbsoluto = fitGen;
                            peorIndividuoAbsoluto = mejorGen;
                        }
                    }
                }

                // Imprimimos en consola los resultados del algoritmo
                System.out.println("=============================================");
                System.out.println("RESULTADOS DEL ALGORITMO GENÉTICO");
                System.out.println("---------------------------------------------");
                // Mejor global
                System.out.printf("Mejor fitness (global): %.6f\n", mejorAbsoluto);
                if (mejorIndividuoAbsoluto != null) {
                    System.out.println("Fenotipo (Mejor):");
                    for (int i = 0; i < dimension; i++) {
                        System.out.printf("  x%d = %.6f\n", i + 1, mejorIndividuoAbsoluto.getFenotipo(i));
                    }
                }
                System.out.println("---------------------------------------------");
                // Peor global
                System.out.printf("Peor fitness (global): %.6f\n", peorAbsoluto);
                if (peorIndividuoAbsoluto != null) {
                    System.out.println("Fenotipo (Peor):");
                    for (int i = 0; i < dimension; i++) {
                        System.out.printf("  x%d = %.6f\n", i + 1, peorIndividuoAbsoluto.getFenotipo(i));
                    }
                }
                System.out.println("=============================================");

                // Finalmente, graficamos la evolución y resaltamos el óptimo y el peor
                graficarEvolucion(mejoresAbsolutos, mejoresGeneracion, promediosGeneracion, 
                                  esMinimizacion, mejorAbsoluto, peorAbsoluto);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });
    }

    /**
     * Método para graficar la evolución del fitness.
     * Se dibujan las curvas del "Mejor Absoluto", "Mejor de la Generación" y "Media".
     * Además, se añaden dos scatter plots que muestran el punto óptimo y el peor, incluyendo el valor en la etiqueta.
     */
    public static void graficarEvolucion(
            double[] mejoresAbsolutos, 
            double[] mejoresGeneracion, 
            double[] promediosGeneracion, 
            boolean esMinimizacion,
            double mejorValGlobal,
            double peorValGlobal
    ) {
        Plot2DPanel plot = new Plot2DPanel();
        int n = mejoresAbsolutos.length;
        double[] generaciones = new double[n];
        for (int i = 0; i < n; i++) {
            generaciones[i] = i;
        }

        // Añadimos las curvas de la evolución
        plot.addLinePlot("Mejor Absoluto", Color.BLUE, generaciones, mejoresAbsolutos);
        plot.addLinePlot("Mejor de la Generación", Color.RED, generaciones, mejoresGeneracion);
        plot.addLinePlot("Media de la Generación", Color.GREEN, generaciones, promediosGeneracion);

        // Para el óptimo, usaremos el último valor del array de mejoresAbsolutos
        int idxOpt = n - 1;
        double[] optX = { generaciones[idxOpt] };
        double[] optY = { mejoresAbsolutos[idxOpt] };
        // La etiqueta muestra el valor del mejor global
        String labelOpt = String.format("Óptimo (%.2f)", mejorValGlobal);
        plot.addScatterPlot(labelOpt, Color.MAGENTA, optX, optY);

        // Para el peor, usamos el valor peorValGlobal que rastreamos globalmente
        double[] worstX = { generaciones[n - 1] };
        double[] worstY = { peorValGlobal };
        String labelWorst = String.format("Peor (%.2f)", peorValGlobal);
        plot.addScatterPlot(labelWorst, Color.ORANGE, worstX, worstY);

        // Añadimos leyenda y etiquetas de ejes
        plot.addLegend("SOUTH");
        plot.setAxisLabel(0, "Generación");
        plot.setAxisLabel(1, "Evaluación");

        JFrame frame = new JFrame("Evolución del Fitness");
        frame.setSize(800, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }
}
