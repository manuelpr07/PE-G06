package ejecución;

import algoritmo.AlgoritmoGenetico;
import algoritmo.Individuo;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import org.math.plot.Plot2DPanel;

public class Main {

    // Declaración de campos para la interfaz
    private JTextField txtTamPoblacion = new JTextField("100", 10);
    private JTextField txtGeneraciones = new JTextField("100", 10);
    private JTextField txtPorcentajeMutacion = new JTextField("5", 10);
    private JTextField txtPorcentajeCruce = new JTextField("60", 10);
    private JTextField txtPorcentajeElitismo = new JTextField("0", 10);
    private JTextField txtPrecision = new JTextField("0.001", 10);
    private JTextField txtDimension = new JTextField("2", 10);
    private JComboBox<String> comboSeleccion = new JComboBox<>(new String[]{
        "Torneo", 
        "Ruleta", 
        "Torneo Probabilistico", 
        "Estocastico Universal", 
        "Truncamiento", 
        "Restos"
    });
    private JComboBox<String> comboCruce = new JComboBox<>(new String[]{
        "Monopunto", "Uniforme", "Aritmético", "BLX-α"
    });
    private JComboBox<String> comboFuncion = new JComboBox<>(new String[]{
        "1. Función 1: Maximización (2D)",
        "2. Mishra Bird (2D) Minimización",
        "3. Schubert (2D) Minimización",
        "4. Michalewicz (Binario)",
        "5. Michalewicz (Real)"
    });
    private JCheckBox chkMutacionReales = new JCheckBox("Mutación sobre reales", false);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch(Exception e) {
            System.out.println("Nimbus no disponible.");
        }
        SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Configuración del Algoritmo Genético");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 800);
        frame.setLocationRelativeTo(null);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Configuración del Algoritmo Genético", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

       
        JPanel formPanel = createFormPanel();
        formPanel.setOpaque(false);
        formPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(255, 255, 255, 200), 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel de botón
        JButton btnEjecutar = new JButton("Ejecutar");
        btnEjecutar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnEjecutar);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);

        // Acción del botón "Ejecutar"
        btnEjecutar.addActionListener(e -> ejecutarAlgoritmo());
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        addLabelAndField(panel, gbc, row++, "Tamaño de la Población:", txtTamPoblacion);
        addLabelAndField(panel, gbc, row++, "Número de Generaciones:", txtGeneraciones);
        addLabelAndField(panel, gbc, row++, "Porcentaje de Mutación (%):", txtPorcentajeMutacion);
        addLabelAndField(panel, gbc, row++, "Porcentaje de Cruce (%):", txtPorcentajeCruce);
        addLabelAndField(panel, gbc, row++, "Porcentaje de Elitismo (%):", txtPorcentajeElitismo);
        addLabelAndField(panel, gbc, row++, "Precisión de la representación:", txtPrecision);
        addLabelAndCombo(panel, gbc, row++, "Método de Selección:", comboSeleccion);
        addLabelAndCombo(panel, gbc, row++, "Método de Cruce:", comboCruce);
        addLabelAndField(panel, gbc, row++, "Dimensión (Solo para Función 4 y 5):", txtDimension);
        addLabelAndCombo(panel, gbc, row++, "Selecciona la Función a Optimizar:", comboFuncion);
        addLabelAndCheckbox(panel, gbc, row++, "Mutación sobre reales:", chkMutacionReales);

        // Habilitamos o deshabilitamos el campo "Dimensión" según la función seleccionada
        comboFuncion.addActionListener(e -> {
            int opcion = comboFuncion.getSelectedIndex() + 1;
            txtDimension.setEnabled(opcion == 4 || opcion == 5);
        });

        return panel;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        gbc.gridx = 1;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(field, gbc);
    }

    private void addLabelAndCombo(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComboBox<?> combo) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        gbc.gridx = 1;
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(combo, gbc);
    }

    private void addLabelAndCheckbox(JPanel panel, GridBagConstraints gbc, int row, String labelText, JCheckBox checkBox) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);

        gbc.gridx = 1;
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        checkBox.setOpaque(false);
        panel.add(checkBox, gbc);
    }

    private void ejecutarAlgoritmo() {
        try {
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
            boolean esMinimizacion = (opcionFuncion == 2 || opcionFuncion == 3 ||
                                      opcionFuncion == 4 || opcionFuncion == 5);

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

            boolean usarMutacionReales = chkMutacionReales.isSelected();
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

            Individuo mejorIndividuoAbsoluto = null;
            double mejorAbsoluto = esMinimizacion ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            Individuo peorIndividuoAbsoluto = null;
            double peorAbsoluto = esMinimizacion ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

            double[] mejoresAbsolutos = new double[generaciones];
            double[] mejoresGeneracion = new double[generaciones];
            double[] promediosGeneracion = new double[generaciones];

            for (int gen = 0; gen < generaciones; gen++) {
                Individuo mejorGen = ag.ejecutar();
                double fitGen = mejorGen.getFitness();
                mejoresGeneracion[gen] = fitGen;
                promediosGeneracion[gen] = ag.calcularPromedioFitness();

                if ((esMinimizacion && fitGen < mejorAbsoluto) ||
                    (!esMinimizacion && fitGen > mejorAbsoluto)) {
                    mejorAbsoluto = fitGen;
                    mejorIndividuoAbsoluto = mejorGen;
                }
                mejoresAbsolutos[gen] = mejorAbsoluto;

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

            System.out.println("=============================================");
            System.out.println("RESULTADOS DEL ALGORITMO GENÉTICO");
            System.out.println("---------------------------------------------");
            System.out.printf("Mejor fitness (global): %.6f\n", mejorAbsoluto);
            if (mejorIndividuoAbsoluto != null) {
                System.out.println("Fenotipo (Mejor):");
                for (int i = 0; i < dimension; i++) {
                    System.out.printf("  x%d = %.6f\n", i + 1, mejorIndividuoAbsoluto.getFenotipo(i));
                }
            }
            System.out.println("---------------------------------------------");
            System.out.printf("Peor fitness (global): %.6f\n", peorAbsoluto);
            if (peorIndividuoAbsoluto != null) {
                System.out.println("Fenotipo (Peor):");
                for (int i = 0; i < dimension; i++) {
                    System.out.printf("  x%d = %.6f\n", i + 1, peorIndividuoAbsoluto.getFenotipo(i));
                }
            }
            System.out.println("=============================================");

            graficarEvolucion(mejoresAbsolutos, mejoresGeneracion, promediosGeneracion, 
                              esMinimizacion, mejorAbsoluto, peorAbsoluto);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void graficarEvolucion(double[] mejoresAbsolutos, double[] mejoresGeneracion, double[] promediosGeneracion, 
                                  boolean esMinimizacion, double mejorValGlobal, double peorValGlobal) {
        Plot2DPanel plot = new Plot2DPanel();
        int n = mejoresAbsolutos.length;
        double[] generaciones = new double[n];
        for (int i = 0; i < n; i++) {
            generaciones[i] = i;
        }
        
        plot.addLinePlot("Mejor Absoluto", Color.BLUE, generaciones, mejoresAbsolutos);
        plot.addLinePlot("Mejor de la Generación", Color.RED, generaciones, mejoresGeneracion);
        plot.addLinePlot("Media de la Generación", Color.GREEN, generaciones, promediosGeneracion);
        
        int idxOpt = n - 1;
        double[] optX = { generaciones[idxOpt] };
        double[] optY = { mejoresAbsolutos[idxOpt] };
        String labelOpt = String.format("Óptimo (%.2f)", mejorValGlobal);
        plot.addScatterPlot(labelOpt, Color.MAGENTA, optX, optY);
        
        double[] worstX = { generaciones[n - 1] };
        double[] worstY = { peorValGlobal };
        String labelWorst = String.format("Peor (%.2f)", peorValGlobal);
        plot.addScatterPlot(labelWorst, Color.ORANGE, worstX, worstY);
        
        plot.addLegend("SOUTH");
        plot.setAxisLabel(0, "Generación");
        plot.setAxisLabel(1, "Evaluación");
        
        JFrame plotFrame = new JFrame("Evolución del Fitness");
        plotFrame.setSize(800, 600);
        plotFrame.setLocationRelativeTo(null);
        plotFrame.setContentPane(plot);
        plotFrame.setVisible(true);
    }

    // Clase para un panel con fondo degradado
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            Color colorInicio = new Color(0, 102, 204);   // Azul brillante
            Color colorFin = new Color(0, 51, 102);         // Azul oscuro
            GradientPaint gp = new GradientPaint(0, 0, colorInicio, 0, height, colorFin);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }
}
