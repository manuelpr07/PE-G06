package Model;

import java.util.ArrayList;
import java.util.List;

public class CasaMap {
    public static final int ROWS = 15;
    public static final int COLS = 15;
    private char[][] grid;
    private List<Room> rooms;
    
    // Posición fija de la base
    private final int baseRow = 7;
    private final int baseCol = 7;
    
    public CasaMap() {
        grid = new char[ROWS][COLS];
        // Inicializamos la cuadrícula con espacios
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                grid[i][j] = ' ';
            }
        }
        // Colocamos la base (B)
        grid[baseRow][baseCol] = 'B';
        
        // Creamos la lista de habitaciones (con posiciones fijas)
        rooms = new ArrayList<>();
        rooms.add(new Room(1, "Sala", 2, 2));
        rooms.add(new Room(2, "Cocina", 2, 12));
        rooms.add(new Room(3, "Dormitorio", 12, 2));
        rooms.add(new Room(4, "Baño", 12, 12));
        rooms.add(new Room(5, "Comedor", 2, 7));
        rooms.add(new Room(6, "Oficina", 7, 2));
        rooms.add(new Room(7, "Estudio", 7, 12));
        rooms.add(new Room(8, "Lavandería", 12, 7));
        rooms.add(new Room(9, "Terraza", 0, 7));
        rooms.add(new Room(10, "Garaje", 7, 0));
        rooms.add(new Room(11, "Jardín", 14, 7));
        rooms.add(new Room(12, "Sótano", 7, 14));
        rooms.add(new Room(13, "Balcón", 0, 0));
        rooms.add(new Room(14, "Despacho", 0, 14));
        rooms.add(new Room(15, "Vestíbulo", 14, 0));
        rooms.add(new Room(16, "Cuarto de lavado", 14, 14));
        rooms.add(new Room(17, "Sala de estar", 4, 4));
        rooms.add(new Room(18, "Sala de juegos", 4, 12));
        rooms.add(new Room(19, "Sala de TV", 10, 4));
        rooms.add(new Room(20, "Sala de reuniones", 10, 12));
        
        // Colocamos las habitaciones en el mapa (mostrando el dígito final del ID)
        for (Room room : rooms) {
            grid[room.getRow()][room.getCol()] = Character.forDigit(room.getId() % 10, 10);
        }
        
        // Obstáculos según el enunciado:
        // Pared horizontal: fila 5, columnas 5 a 9
        for (int j = 5; j <= 9; j++) {
            grid[5][j] = '■';
        }
        // Pared vertical: columna 10, filas 8 a 12
        for (int i = 8; i <= 12; i++) {
            grid[i][10] = '■';
        }
        // Obstáculos individuales
        grid[10][3] = '■';
        grid[11][4] = '■';
        // Pared vertical adicional: columna 6, filas 10 a 13
        for (int i = 10; i <= 13; i++) {
            grid[i][6] = '■';
        }
        // Pared horizontal adicional: fila 8, columnas 1 a 4
        for (int j = 1; j <= 4; j++) {
            grid[8][j] = '■';
        }

        // Obstáculos en esquina superior derecha: (0,13) y (1,13)
        grid[0][13] = '■';
        grid[1][13] = '■';
        // Pared horizontal adicional: fila 3, columnas 8 a 11
        for (int j = 8; j <= 11; j++) {
            grid[3][j] = '■';
        }
        
        // Obstáculo adicional: se añade un obstáculo representado por 'X'
        grid[6][6] = 'X';
    }
    
    public void printMap() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                System.out.print(String.format("|%2s", grid[i][j]));
            }
            System.out.println("|");
        }
    }
    
    public List<Room> getRooms() {
        return rooms;
    }
    
    public int getBaseRow() {
        return baseRow;
    }
    
    public int getBaseCol() {
        return baseCol;
    }
    
    public char[][] getGrid() {
        return grid;
    }
}
