package Algoritmo;

import java.util.*;
import Model.CasaMap;
import Model.Room;
import java.awt.Point;

public class OperadoresGeneticos {

    public static IndividuoRuta[] crossover(IndividuoRuta padre1, IndividuoRuta padre2, int crossoverMethod) {
        switch(crossoverMethod) {
            case 0: // PMX mejorado
                return crossoverPMX(padre1, padre2);
            case 1: // OX (Order Crossover)
                return crossoverOX(padre1, padre2);
            case 2: // CX (Cycle Crossover)
                return crossoverCX(padre1, padre2);
            case 3: // Custom: combina bloques de ambos padres
                return crossoverCustom(padre1, padre2);
            case 4: // CO: Order Based Crossover (OBX)
                return crossoverCO(padre1, padre2);
            case 5: // ERX: Edge Recombination Crossover
                return crossoverERX(padre1, padre2);
            case 6: 
                return crossoverCustom(padre1, padre2);
            default:
                return crossoverPMX(padre1, padre2);
        }
    }
    
    // --- Crossover Mejorados ---
    
    // PMX mejorado: se selecciona un segmento y se mapea el resto para evitar duplicados
    private static IndividuoRuta[] crossoverPMX(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] parent1 = padre1.getRuta();
        int[] parent2 = padre2.getRuta();
        int size = parent1.length;
        int[] child1 = new int[size];
        int[] child2 = new int[size];
        for (int i = 0; i < size; i++) {
            child1[i] = -1;
            child2[i] = -1;
        }
        
        int cp1 = (int)(Math.random() * size);
        int cp2 = (int)(Math.random() * size);
        if(cp1 > cp2) { int temp = cp1; cp1 = cp2; cp2 = temp; }
        
        // Copia del segmento
        for (int i = cp1; i <= cp2; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }
        
        // Mapeo para child1: se coloca el gen de parent2 en la posición correspondiente
        for (int i = cp1; i <= cp2; i++) {
            int gene = parent2[i];
            if (!contains(child1, gene)) {
                int pos = i;
                while (true) {
                    int geneFromParent1 = parent1[pos];
                    pos = indexOf(parent2, geneFromParent1);
                    if (child1[pos] == -1) {
                        child1[pos] = gene;
                        break;
                    }
                }
            }
        }
        // Rellenar posiciones restantes para child1
        for (int i = 0; i < size; i++) {
            if(child1[i] == -1) {
                child1[i] = parent2[i];
            }
        }
        
        // Mapeo para child2: análogo al proceso anterior
        for (int i = cp1; i <= cp2; i++) {
            int gene = parent1[i];
            if (!contains(child2, gene)) {
                int pos = i;
                while (true) {
                    int geneFromParent2 = parent2[pos];
                    pos = indexOf(parent1, geneFromParent2);
                    if (child2[pos] == -1) {
                        child2[pos] = gene;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < size; i++) {
            if(child2[i] == -1) {
                child2[i] = parent1[i];
            }
        }
        
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(child1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(child2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    // OX (Order Crossover): se copia un segmento y se completan los espacios vacíos con el orden del otro padre
    private static IndividuoRuta[] crossoverOX(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] parent1 = padre1.getRuta();
        int[] parent2 = padre2.getRuta();
        int size = parent1.length;
        int[] child1 = new int[size];
        int[] child2 = new int[size];
        
        for (int i = 0; i < size; i++) {
            child1[i] = -1;
            child2[i] = -1;
        }
        
        int cp1 = (int)(Math.random() * size);
        int cp2 = (int)(Math.random() * size);
        if(cp1 > cp2) { int temp = cp1; cp1 = cp2; cp2 = temp; }
        
        // Copia del segmento
        for (int i = cp1; i <= cp2; i++) {
            child1[i] = parent1[i];
            child2[i] = parent2[i];
        }
        
        // Rellenar child1 con el orden de parent2
        int currentIndex = (cp2 + 1) % size;
        int parentIndex = (cp2 + 1) % size;
        while (contains(child1, -1)) {
            if (!contains(child1, parent2[parentIndex])) {
                child1[currentIndex] = parent2[parentIndex];
                currentIndex = (currentIndex + 1) % size;
            }
            parentIndex = (parentIndex + 1) % size;
        }
        
        // Rellenar child2 con el orden de parent1
        currentIndex = (cp2 + 1) % size;
        parentIndex = (cp2 + 1) % size;
        while (contains(child2, -1)) {
            if (!contains(child2, parent1[parentIndex])) {
                child2[currentIndex] = parent1[parentIndex];
                currentIndex = (currentIndex + 1) % size;
            }
            parentIndex = (parentIndex + 1) % size;
        }
        
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(child1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(child2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    // CX (Cycle Crossover): intercambia ciclos de genes entre padres
    private static IndividuoRuta[] crossoverCX(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] p1 = padre1.getRuta();
        int[] p2 = padre2.getRuta();
        int size = p1.length;
        int[] child1 = new int[size];
        int[] child2 = new int[size];
        boolean[] visited = new boolean[size];
        
        for (int i = 0; i < size; i++) {
            child1[i] = -1;
            child2[i] = -1;
        }
        
        for (int i = 0; i < size; i++) {
            if (!visited[i]) {
                int index = i;
                do {
                    child1[index] = p1[index];
                    child2[index] = p2[index];
                    visited[index] = true;
                    index = indexOf(p1, p2[index]);
                } while (index != i && index != -1);
            }
        }
        
        for (int i = 0; i < size; i++) {
            if (child1[i] == -1) {
                child1[i] = p2[i];
            }
            if (child2[i] == -1) {
                child2[i] = p1[i];
            }
        }
        
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(child1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(child2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    // Custom: combina segmentos de cada padre y repara la permutación para asegurar unicidad
    private static IndividuoRuta[] crossoverCustom(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] p1 = padre1.getRuta();
        int[] p2 = padre2.getRuta();
        int size = p1.length;
        int[] child1 = new int[size];
        int[] child2 = new int[size];
        
        int seg1 = size / 3;
        int seg2 = 2 * size / 3;
        
        // Child1: primer y tercer segmento de p1, intermedio de p2
        System.arraycopy(p1, 0, child1, 0, seg1);
        System.arraycopy(p2, seg1, child1, seg1, seg2 - seg1);
        System.arraycopy(p1, seg2, child1, seg2, size - seg2);
        
        // Child2: primer y tercer segmento de p2, intermedio de p1
        System.arraycopy(p2, 0, child2, 0, seg1);
        System.arraycopy(p1, seg1, child2, seg1, seg2 - seg1);
        System.arraycopy(p2, seg2, child2, seg2, size - seg2);
        
        child1 = repairPermutation(child1, size);
        child2 = repairPermutation(child2, size);
        
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(child1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(child2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    // CO (Order Based Crossover – OBX): se selecciona aleatoriamente un subconjunto de posiciones del primer padre y se rellenan los huecos con el orden del segundo padre.
    private static IndividuoRuta[] crossoverCO(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] parent1 = padre1.getRuta();
        int[] parent2 = padre2.getRuta();
        int size = parent1.length;
        int[] child1 = new int[size];
        int[] child2 = new int[size];
        // Inicializamos los hijos con -1
        for (int i = 0; i < size; i++) {
            child1[i] = -1;
            child2[i] = -1;
        }
        
        // Seleccionamos posiciones aleatorias (por ejemplo, alrededor del 50% de los índices)
        boolean[] positions = new boolean[size];
        for (int i = 0; i < size; i++) {
            positions[i] = Math.random() < 0.5;
        }
        // Aseguramos que al menos se seleccione una posición
        boolean any = false;
        for (boolean pos : positions) {
            if (pos) { any = true; break; }
        }
        if (!any) { positions[(int)(Math.random()*size)] = true; }
        
        // Para child1, copiamos los genes de parent1 en las posiciones seleccionadas
        for (int i = 0; i < size; i++) {
            if (positions[i]) {
                child1[i] = parent1[i];
            }
        }
        // Rellenamos los huecos con los genes de parent2 en el orden en que aparecen
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (child1[i] == -1) {
                while (index < size && contains(child1, parent2[index])) {
                    index++;
                }
                if (index < size) {
                    child1[i] = parent2[index];
                    index++;
                }
            }
        }
        
        // Para child2, intercambiamos el rol de los padres
        boolean[] positions2 = new boolean[size];
        for (int i = 0; i < size; i++) {
            positions2[i] = Math.random() < 0.5;
        }
        any = false;
        for (boolean pos : positions2) {
            if (pos) { any = true; break; }
        }
        if (!any) { positions2[(int)(Math.random()*size)] = true; }
        
        for (int i = 0; i < size; i++) {
            if (positions2[i]) {
                child2[i] = parent2[i];
            }
        }
        index = 0;
        for (int i = 0; i < size; i++) {
            if (child2[i] == -1) {
                while (index < size && contains(child2, parent1[index])) {
                    index++;
                }
                if (index < size) {
                    child2[i] = parent1[index];
                    index++;
                }
            }
        }
        
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(child1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(child2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    // ERX (Edge Recombination Crossover): utiliza una tabla de vecinos construida a partir de ambos padres.
    private static IndividuoRuta[] crossoverERX(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] parent1 = padre1.getRuta();
        int[] parent2 = padre2.getRuta();
        int size = parent1.length;
        
        // Generamos dos hijos ejecutando ERX con diferente orden de padres para diversidad.
        int[] child1 = edgeRecombinationCrossover(parent1, parent2);
        int[] child2 = edgeRecombinationCrossover(parent2, parent1);
        
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(child1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(child2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    // Implementación de ERX
    private static int[] edgeRecombinationCrossover(int[] p1, int[] p2) {
        int size = p1.length;
        // Construir tabla de vecinos
        Map<Integer, Set<Integer>> edgeTable = new HashMap<>();
        for (int gene : p1) {
            edgeTable.put(gene, new HashSet<>());
        }
        // Añadimos vecinos de p1
        for (int i = 0; i < size; i++) {
            int gene = p1[i];
            int left = p1[(i - 1 + size) % size];
            int right = p1[(i + 1) % size];
            edgeTable.get(gene).add(left);
            edgeTable.get(gene).add(right);
        }
        // Añadimos vecinos de p2
        for (int i = 0; i < size; i++) {
            int gene = p2[i];
            int left = p2[(i - 1 + size) % size];
            int right = p2[(i + 1) % size];
            edgeTable.get(gene).add(left);
            edgeTable.get(gene).add(right);
        }
        
        int[] child = new int[size];
        boolean[] used = new boolean[size + 1]; // Genes del 1 al size
        // Elegir aleatoriamente el gen inicial (se elige de p1)
        int currentGene = p1[(int)(Math.random() * size)];
        child[0] = currentGene;
        used[currentGene] = true;
        
        for (int i = 1; i < size; i++) {
            // Remover currentGene de todas las listas
            for (Set<Integer> neighbors : edgeTable.values()) {
                neighbors.remove(currentGene);
            }
            Set<Integer> neighbors = edgeTable.get(currentGene);
            int nextGene = -1;
            if (!neighbors.isEmpty()) {
                int minCount = Integer.MAX_VALUE;
                for (int candidate : neighbors) {
                    if (!used[candidate]) {
                        int count = edgeTable.get(candidate).size();
                        if (count < minCount) {
                            minCount = count;
                            nextGene = candidate;
                        }
                    }
                }
            }
            if (nextGene == -1) {
                // Si no hay vecinos disponibles, elegir aleatoriamente entre los genes no usados
                List<Integer> candidates = new ArrayList<>();
                for (int gene = 1; gene <= size; gene++) {
                    if (!used[gene]) {
                        candidates.add(gene);
                    }
                }
                nextGene = candidates.get((int)(Math.random() * candidates.size()));
            }
            child[i] = nextGene;
            used[nextGene] = true;
            currentGene = nextGene;
        }
        
        return child;
    }
    
    // --- Métodos auxiliares para operadores de cruce ---
    
    private static boolean contains(int[] arr, int value) {
        for (int i : arr) {
            if (i == value) return true;
        }
        return false;
    }
    
    private static int indexOf(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }
    
    // Repara la permutación para asegurar que todos los genes estén presentes y sin duplicados
    private static int[] repairPermutation(int[] child, int size) {
        for (int i = 0; i < size; i++) {
            if (!contains(child, i + 1)) {
                for (int j = 0; j < size; j++) {
                    if (child[j] == -1 || duplicate(child, child[j], j)) {
                        child[j] = i + 1;
                        break;
                    }
                }
            }
        }
        return child;
    }
    
    private static boolean duplicate(int[] arr, int value, int pos) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) count++;
        }
        return count > 1;
    }
    
    // --- Mutaciones (se mantienen las mejoras previas) ---
    
    public static void mutacion(IndividuoRuta individuo, int mutationMethod) {
        switch(mutationMethod) {
            case 0:
                mutacionInsercion(individuo);
                break;
            case 1:
                mutacionSwap(individuo);
                break;
            case 2:
                mutacionInversion(individuo);
                break;
            case 3:
                mutacionHeuristica(individuo);
                break;
            case 4:
                mutacionCustom(individuo);
                break;
            case 5:
                mutacionScramble(individuo);
                break;
            default:
                mutacionSwap(individuo);
                break;
        }
    }
    
    private static void mutacionInsercion(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        int pos = (int)(Math.random() * size);
        int val = ruta[pos];
        for (int i = pos; i < size - 1; i++) {
            ruta[i] = ruta[i + 1];
        }
        int newPos = (int)(Math.random() * size);
        for (int i = size - 1; i > newPos; i--) {
            ruta[i] = ruta[i - 1];
        }
        ruta[newPos] = val;
    }
    
    private static void mutacionSwap(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        int pos1 = (int)(Math.random() * size);
        int pos2 = (int)(Math.random() * size);
        while(pos1 == pos2) {
            pos2 = (int)(Math.random() * size);
        }
        int temp = ruta[pos1];
        ruta[pos1] = ruta[pos2];
        ruta[pos2] = temp;
    }
    
    private static void mutacionInversion(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        int pos1 = (int)(Math.random() * size);
        int pos2 = (int)(Math.random() * size);
        if (pos1 > pos2) { int temp = pos1; pos1 = pos2; pos2 = temp; }
        while (pos1 < pos2) {
            int temp = ruta[pos1];
            ruta[pos1] = ruta[pos2];
            ruta[pos2] = temp;
            pos1++;
            pos2--;
        }
    }
    
    private static void mutacionHeuristica(IndividuoRuta individuo) {
        // Mutación heurística simple: se utiliza una variante de swap
        mutacionSwap(individuo);
    }
    
    private static void mutacionCustom(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        int mid = size / 2;
        for (int i = 0; i < mid; i++) {
            int temp = ruta[i];
            ruta[i] = ruta[i + mid];
            ruta[i + mid] = temp;
        }
    }
    
    // Mutación Scramble: se selecciona un segmento y se reordena aleatoriamente
    private static void mutacionScramble(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        int pos1 = (int)(Math.random() * size);
        int pos2 = (int)(Math.random() * size);
        if(pos1 > pos2) { int temp = pos1; pos1 = pos2; pos2 = temp; }
        for (int i = pos1; i < pos2; i++) {
            int j = pos1 + (int)(Math.random() * (pos2 - pos1 + 1));
            int temp = ruta[i];
            ruta[i] = ruta[j];
            ruta[j] = temp;
        }
    }
}
