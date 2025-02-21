package ejecución;

import algoritmo.AlgoritmoGenetico;
import algoritmo.Individuo;
import javax.swing.*;
import java.awt.*;
import org.math.plot.Plot2DPanel;

public class Main {
    public static void main(String[] args) {
        // Crear ventana para configurar parámetros
        JFrame frame = new JFrame("Configuración del Algoritmo Genético");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        // Campos para los parámetros básicos
        JTextField txtTamPoblacion = new JTextField("100");
        JTextField txtGeneraciones = new JTextField("100");
        JTextField txtPorcentajeMutacion = new JTextField("5");
        JTextField txtDimension = new JTextField("2");
        txtDimension.setEnabled(false); // Deshabilitar por defecto

        // Campos para los nuevos parámetros
        JTextField txtPorcentajeCruce = new JTextField("60");
        JTextField txtPorcentajeElitismo = new JTextField("0");

        String[] opcionesSeleccion = {"Torneo", "Ruleta"};
        JComboBox<String> comboSeleccion = new JComboBox<>(opcionesSeleccion);

        String[] opcionesCruce = {"Monopunto", "Uniforme"};
        JComboBox<String> comboCruce = new JComboBox<>(opcionesCruce);

        JComboBox<String> comboFuncion = new JComboBox<>(new String[]{
                "1. Función 1: Maximización (2D)",
                "2. Mishra Bird (2D) Minimización",
                "3. Schubert (2D) Minimización",
                "4. Michalewicz (Binario)",
                "5. Michalewicz (Real)"
        });

        // Habilitar el campo de dimensión solo para funciones 4 y 5
        comboFuncion.addActionListener(e -> {
            int opcion = comboFuncion.getSelectedIndex() + 1;
            txtDimension.setEnabled(opcion == 4 || opcion == 5);
        });

        // Agregar campos al panel
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
        panel.add(new JLabel("Método de Selección:"));
        panel.add(comboSeleccion);
        panel.add(new JLabel("Método de Cruce:"));
        panel.add(comboCruce);
        panel.add(new JLabel("Dimensión (Solo para Función 4 y 5):"));
        panel.add(txtDimension);
        panel.add(new JLabel("Selecciona la Función a Optimizar:"));
        panel.add(comboFuncion);

        JButton btnEjecutar = new JButton("Ejecutar");
        panel.add(btnEjecutar);

        frame.add(panel);
        frame.setVisible(true);

        // Acción al presionar el botón
        btnEjecutar.addActionListener(e -> {
            try {
                // Leer parámetros básicos
                int tamPoblacion = Integer.parseInt(txtTamPoblacion.getText());
                int generaciones = Integer.parseInt(txtGeneraciones.getText());
                double probMutacion = Integer.parseInt(txtPorcentajeMutacion.getText()) / 100.0;
                int dimension = Integer.parseInt(txtDimension.getText());
                int opcionFuncion = comboFuncion.getSelectedIndex() + 1;

                // Leer nuevos parámetros
                double probCruce = Integer.parseInt(txtPorcentajeCruce.getText()) / 100.0;
                double porcentajeElitismo = Integer.parseInt(txtPorcentajeElitismo.getText()) / 100.0;
                String metodoSeleccion = comboSeleccion.getSelectedItem().toString();
                String metodoCruce = comboCruce.getSelectedItem().toString();

                boolean esMinimizacion = (opcionFuncion == 2 || opcionFuncion == 3 || opcionFuncion == 4 || opcionFuncion == 5);

                // Configurar límites según la función seleccionada
                double[] min, max;
                if (opcionFuncion == 4 || opcionFuncion == 5) {
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
                    } else {
                        min = new double[]{-10.0, -6.5};
                        max = new double[]{0.0, 0.0};
                    }
                }

                // Crear algoritmo genético con todos los parámetros
                AlgoritmoGenetico ag = new AlgoritmoGenetico(
                        tamPoblacion,
                        min.length,
                        min,
                        max,
                        probMutacion,
                        probCruce,
                        porcentajeElitismo,
                        metodoSeleccion,
                        metodoCruce,
                        opcionFuncion
                );
                if (opcionFuncion == 4 || opcionFuncion == 5) {
                    ag.setDimension(dimension);
                }

                // Seguimiento del mejor individuo
                Individuo mejorIndividuoAbsoluto = null;
                double mejorAbsoluto = esMinimizacion ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;

                double[] mejoresAbsolutos = new double[generaciones];
                double[] mejoresGeneracion = new double[generaciones];
                double[] promediosGeneracion = new double[generaciones];

                for (int gen = 0; gen < generaciones; gen++) {
                    Individuo mejorGen = ag.ejecutar();
                    mejoresGeneracion[gen] = mejorGen.getFitness();
                    promediosGeneracion[gen] = ag.calcularPromedioFitness();

                    if ((esMinimizacion && mejorGen.getFitness() < mejorAbsoluto) ||
                        (!esMinimizacion && mejorGen.getFitness() > mejorAbsoluto)) {
                        mejorAbsoluto = mejorGen.getFitness();
                        mejorIndividuoAbsoluto = mejorGen;
                    }

                    mejoresAbsolutos[gen] = mejorAbsoluto;
                }

                // Graficar evolución
                graficarEvolucion(mejoresAbsolutos, mejoresGeneracion, promediosGeneracion);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });
    }

    public static void graficarEvolucion(double[] mejoresAbsolutos, double[] mejoresGeneracion, double[] promediosGeneracion) {
        Plot2DPanel plot = new Plot2DPanel();

        double[] generaciones = new double[mejoresAbsolutos.length];
        for (int i = 0; i < generaciones.length; i++) {
            generaciones[i] = i;
        }

        plot.addLinePlot("Mejor Absoluto (Negro)", Color.BLACK, generaciones, mejoresAbsolutos);
        plot.addLinePlot("Mejor Generación (Rojo)", Color.RED, generaciones, mejoresGeneracion);
        plot.addLinePlot("Aptitud Media (Verde)", Color.GREEN, generaciones, promediosGeneracion);
      
        JFrame frame = new JFrame("Evolución del Fitness");
        frame.setSize(800, 600);
        frame.setContentPane(plot);
        frame.setVisible(true);
    }
}
