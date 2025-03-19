package Algoritmo;

public class OperadoresGeneticos {

    public static IndividuoRuta[] crossover(IndividuoRuta padre1, IndividuoRuta padre2, int crossoverMethod) {
        switch(crossoverMethod) {
            case 0: // PMX
                return crossoverPMX(padre1, padre2);
            case 1: // OX
                return crossoverOX(padre1, padre2);
            case 2: // OXPP
                return crossoverOX(padre1, padre2); // Por simplicidad, igual a OX
            case 3: // CX
                return crossoverCX(padre1, padre2);
            case 4: // CO
                return crossoverCO(padre1, padre2);
            case 5: // ERX
                return crossoverERX(padre1, padre2);
            case 6: // Custom
                return crossoverCustom(padre1, padre2);
            default:
                return crossoverPMX(padre1, padre2);
        }
    }
    
    private static IndividuoRuta[] crossoverPMX(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] ruta1 = padre1.getRuta();
        int[] ruta2 = padre2.getRuta();
        int size = ruta1.length;
        int[] hijo1 = new int[size];
        int[] hijo2 = new int[size];
        for (int i = 0; i < size; i++) {
            hijo1[i] = -1;
            hijo2[i] = -1;
        }
        int cp1 = (int)(Math.random() * size);
        int cp2 = (int)(Math.random() * size);
        if(cp1 > cp2) { int temp = cp1; cp1 = cp2; cp2 = temp; }
        for (int i = cp1; i <= cp2; i++) {
            hijo1[i] = ruta1[i];
            hijo2[i] = ruta2[i];
        }
        int pos1 = (cp2 + 1) % size;
        int pos2 = (cp2 + 1) % size;
        for (int i = 0; i < size; i++) {
            int idx = (cp2 + 1 + i) % size;
            if (!contains(hijo1, ruta2[idx])) {
                hijo1[pos1] = ruta2[idx];
                pos1 = (pos1 + 1) % size;
            }
            if (!contains(hijo2, ruta1[idx])) {
                hijo2[pos2] = ruta1[idx];
                pos2 = (pos2 + 1) % size;
            }
        }
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(hijo1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(hijo2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    private static IndividuoRuta[] crossoverOX(IndividuoRuta padre1, IndividuoRuta padre2) {
        // Implementación sencilla similar a PMX para ejemplificar
        return crossoverPMX(padre1, padre2);
    }
    
    private static IndividuoRuta[] crossoverCX(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] p1 = padre1.getRuta();
        int[] p2 = padre2.getRuta();
        int size = p1.length;
        int[] hijo1 = new int[size];
        int[] hijo2 = new int[size];
        boolean[] visitado = new boolean[size];
        for (int i = 0; i < size; i++) {
            hijo1[i] = -1;
            hijo2[i] = -1;
        }
        for (int i = 0; i < size; i++) {
            if (!visitado[i]) {
                int index = i;
                do {
                    hijo1[index] = p1[index];
                    hijo2[index] = p2[index];
                    visitado[index] = true;
                    int newIndex = -1;
                    for (int j = 0; j < size; j++) {
                        if (p1[j] == p2[index]) { newIndex = j; break; }
                    }
                    index = newIndex;
                } while (index != i && index != -1);
            }
        }
        for (int i = 0; i < size; i++) {
            if (hijo1[i] == -1) { hijo1[i] = p2[i]; }
            if (hijo2[i] == -1) { hijo2[i] = p1[i]; }
        }
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(hijo1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(hijo2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    private static IndividuoRuta[] crossoverCO(IndividuoRuta padre1, IndividuoRuta padre2) {
        // Para simplificar, se reutiliza PMX
        return crossoverPMX(padre1, padre2);
    }
    
    private static IndividuoRuta[] crossoverERX(IndividuoRuta padre1, IndividuoRuta padre2) {
        return crossoverPMX(padre1, padre2);
    }
    
    private static IndividuoRuta[] crossoverCustom(IndividuoRuta padre1, IndividuoRuta padre2) {
        int[] ruta1 = padre1.getRuta();
        int[] ruta2 = padre2.getRuta();
        int size = ruta1.length;
        int[] hijo1 = new int[size];
        int[] hijo2 = new int[size];
        for (int i = 0; i < size; i++) {
            hijo1[i] = ruta1[i];
            hijo2[i] = ruta2[i];
        }
        int start = size / 4;
        int end = start + size / 2;
        for (int i = start, j = end - 1; i < end; i++, j--) {
            int temp = hijo1[i];
            hijo1[i] = hijo2[j];
            hijo2[j] = temp;
        }
        IndividuoRuta[] hijos = new IndividuoRuta[2];
        hijos[0] = new IndividuoRuta();
        hijos[1] = new IndividuoRuta();
        System.arraycopy(hijo1, 0, hijos[0].getRuta(), 0, size);
        System.arraycopy(hijo2, 0, hijos[1].getRuta(), 0, size);
        return hijos;
    }
    
    private static boolean contains(int[] arr, int value) {
        for (int i : arr) {
            if (i == value) return true;
        }
        return false;
    }
    
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
            ruta[i] = ruta[i+1];
        }
        int newPos = (int)(Math.random() * size);
        for (int i = size - 1; i > newPos; i--) {
            ruta[i] = ruta[i-1];
        }
        ruta[newPos] = val;
    }
    
    private static void mutacionSwap(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        int pos1 = (int)(Math.random() * size);
        int pos2 = (int)(Math.random() * size);
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
        mutacionSwap(individuo);
    }
    
    private static void mutacionCustom(IndividuoRuta individuo) {
        int[] ruta = individuo.getRuta();
        int size = ruta.length;
        for (int i = 0; i < size / 2; i++) {
            int temp = ruta[i];
            ruta[i] = ruta[i + size / 2];
            ruta[i + size / 2] = temp;
        }
    }
}
