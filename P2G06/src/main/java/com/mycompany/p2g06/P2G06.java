package com.mycompany.p2g06;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import Model.CasaMap;
import Model.Room;
import Algoritmo.AStar;
import Algoritmo.AlgorimoGeneticoRuta;
import Algoritmo.IndividuoRuta;

public class P2G06 {

    private MapPanel mapPanelEmpty;
    private MapPanel mapPanelRuta;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new P2G06().crearInterfaz());
    }
    
    private void crearInterfaz() {
        JFrame frame = new JFrame("Optimización de Ruta del Robot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 700);
        frame.setLayout(new BorderLayout());
        
        // Inicializamos el mapa
        CasaMap map = new CasaMap();
        
        mapPanelEmpty = new MapPanel(map);
        mapPanelRuta  = new MapPanel(map);
        mapPanelRuta.setRoute(new ArrayList<>());
        
        JPanel panelMapas = new JPanel(new GridLayout(1, 2));
        panelMapas.add(mapPanelEmpty);
        panelMapas.add(mapPanelRuta);
        frame.add(panelMapas, BorderLayout.CENTER);
        
        // Panel de configuración con selección de métodos
        JPanel panelConfig = new JPanel(new FlowLayout());
        JTextField txtPoblacion = new JTextField("50", 5);
        JTextField txtGeneraciones = new JTextField("300", 5);
        JTextField txtProbCruce = new JTextField("0.8", 5);
        JTextField txtProbMutacion = new JTextField("0.3", 5);
        JComboBox<String> comboFitness = new JComboBox<>(new String[]{
            "Distancia Total", "Distancia + Suavidad"
        });
        JComboBox<String> comboSeleccion = new JComboBox<>(new String[]{
            "Ruleta", "Torneo", "Estocástico", "Restos", "Truncamiento", "Ranking"
        });
        JComboBox<String> comboCruce = new JComboBox<>(new String[]{
            "PMX", "OX", "OXPP", "CX", "CO", "ERX", "Custom"
        });
        JComboBox<String> comboMutacion = new JComboBox<>(new String[]{
            "Inserción", "Intercambio", "Inversión", "Heurística", "Custom"
        });
        
        panelConfig.add(new JLabel("Población:"));
        panelConfig.add(txtPoblacion);
        panelConfig.add(new JLabel("Generaciones:"));
        panelConfig.add(txtGeneraciones);
        panelConfig.add(new JLabel("Prob. Cruce:"));
        panelConfig.add(txtProbCruce);
        panelConfig.add(new JLabel("Prob. Mutación:"));
        panelConfig.add(txtProbMutacion);
        panelConfig.add(new JLabel("Fitness:"));
        panelConfig.add(comboFitness);
        panelConfig.add(new JLabel("Selección:"));
        panelConfig.add(comboSeleccion);
        panelConfig.add(new JLabel("Cruce:"));
        panelConfig.add(comboCruce);
        panelConfig.add(new JLabel("Mutación:"));
        panelConfig.add(comboMutacion);
        
        JButton btnEjecutar = new JButton("Ejecutar Algoritmo");
        panelConfig.add(btnEjecutar);
        
        frame.add(panelConfig, BorderLayout.NORTH);
        
        btnEjecutar.addActionListener(e -> {
            int tamPob = Integer.parseInt(txtPoblacion.getText());
            int gens = Integer.parseInt(txtGeneraciones.getText());
            double pCruce = Double.parseDouble(txtProbCruce.getText());
            double pMutacion = Double.parseDouble(txtProbMutacion.getText());
            int fitnessType = comboFitness.getSelectedIndex() == 0 ? 1 : 2;
            int selectionMethod = comboSeleccion.getSelectedIndex(); // 0: Ruleta, 1: Torneo, etc.
            int crossoverMethod = comboCruce.getSelectedIndex();
            int mutationMethod = comboMutacion.getSelectedIndex();
            
            AlgorimoGeneticoRuta ag = new AlgorimoGeneticoRuta(tamPob, gens, pMutacion, pCruce, map, fitnessType, selectionMethod, crossoverMethod, mutationMethod);
            ag.evolucionar();
            IndividuoRuta mejor = ag.getMejor();
            
            StringBuilder info = new StringBuilder();
            info.append("Mejor ruta encontrada:\n\n");
            info.append("Secuencia de habitaciones: ");
            for (int id : mejor.getRuta()) {
                info.append(id).append(" ");
            }
            info.append("\nFitness: ").append(mejor.getFitness());
            JOptionPane.showMessageDialog(frame, info.toString(), "Resultados", JOptionPane.INFORMATION_MESSAGE);
            
            // Mostrar la gráfica de evolución del fitness
            FitnessChartPanel chartPanel = new FitnessChartPanel(
                ag.getBestFitnessPerGeneration(),
                ag.getAverageFitnessPerGeneration(),
                ag.getAbsoluteBestFitness()
            );
            JFrame chartFrame = new JFrame("Evolución del Fitness");
            chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            chartFrame.add(chartPanel);
            chartFrame.setSize(800, 600);
            chartFrame.setLocationRelativeTo(null);
            chartFrame.setVisible(true);
            
            // Calcular y animar la ruta
            List<Point> fullPath = new ArrayList<>();
            AStar astar = new AStar(map.getGrid());
            
            Room firstRoom = map.getRooms().get(mejor.getRuta()[0] - 1);
            List<Point> segment = astar.calcularRuta(map.getBaseRow(), map.getBaseCol(), firstRoom.getRow(), firstRoom.getCol());
            if(segment != null) fullPath.addAll(segment);
            
            for (int i = 0; i < mejor.getRuta().length - 1; i++) {
                Room r1 = map.getRooms().get(mejor.getRuta()[i] - 1);
                Room r2 = map.getRooms().get(mejor.getRuta()[i+1] - 1);
                segment = astar.calcularRuta(r1.getRow(), r1.getCol(), r2.getRow(), r2.getCol());
                if (segment != null && !segment.isEmpty()) {
                    if (!fullPath.isEmpty() && fullPath.get(fullPath.size()-1).equals(segment.get(0))) {
                        segment.remove(0);
                    }
                    fullPath.addAll(segment);
                }
            }
            
            Room lastRoom = map.getRooms().get(mejor.getRuta()[mejor.getRuta().length - 1] - 1);
            segment = astar.calcularRuta(lastRoom.getRow(), lastRoom.getCol(), map.getBaseRow(), map.getBaseCol());
            if(segment != null && !segment.isEmpty()){
                if (!fullPath.isEmpty() && fullPath.get(fullPath.size()-1).equals(segment.get(0))) {
                    segment.remove(0);
                }
                fullPath.addAll(segment);
            }
            
            animateRoute(fullPath, 100);
        });
        
        frame.setVisible(true);
    }
    
    private void animateRoute(List<Point> fullPath, int delay) {
        List<Point> animatedRoute = new ArrayList<>();
        Timer timer = new Timer(delay, null);
        final int[] index = {0};
        timer.addActionListener(e -> {
            if (index[0] < fullPath.size()) {
                animatedRoute.add(fullPath.get(index[0]));
                mapPanelRuta.setRoute(new ArrayList<>(animatedRoute));
                index[0]++;
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        timer.start();
    }
}
