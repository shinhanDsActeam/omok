package src.main.service;
import java.util.Arrays;

//오목 로직
public class Board {
    String[][] map;
    int size;
    int lastRow = -1, lastCol = -1;

    public Board(int size) {
        this.size = size;
        map = new String[size][size];
        for (int i = 0; i < size; i++) Arrays.fill(map[i], ".");
    }

    public void print() {
        System.out.print("   ");
        for (int i = 0; i < size; i++)
            System.out.print((char) ('A' + i) + " ");
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.printf("%2d ", i + 1);
            for (int j = 0; j < size; j++) {
                String cell = map[i][j];
                if (i == lastRow && j == lastCol && (cell.equals("O") || cell.equals("X")))
                    System.out.print(cell + "* ");
                else
                    System.out.print(cell + "  ");
            }
            System.out.println();
        }
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isEmpty(int row, int col) {
        return map[row][col].equals(".");
    }

    public void placeStone(int row, int col, String stone) {
        map[row][col] = stone;
        lastRow = row;
        lastCol = col;
    }

    public String getStone(int row, int col) {
        String val = map[row][col];
        return switch (val) {
            case "." -> null;
            case "O" -> "black";
            case "X" -> "white";
            default -> null;
        };
    }

    public boolean checkWin(int row, int col, String stone) {
        return count(row, col, stone, 1, 0) + count(row, col, stone, -1, 0) + 1 >= 5 ||
                count(row, col, stone, 0, 1) + count(row, col, stone, 0, -1) + 1 >= 5 ||
                count(row, col, stone, 1, 1) + count(row, col, stone, -1, -1) + 1 >= 5 ||
                count(row, col, stone, 1, -1) + count(row, col, stone, -1, 1) + 1 >= 5;
    }

    private int count(int row, int col, String stone, int dRow, int dCol) {
        int count = 0;
        int r = row + dRow;
        int c = col + dCol;
        while (isInBounds(r, c) && map[r][c].equals(stone)) {
            count++;
            r += dRow;
            c += dCol;
        }
        return count;
    }

    public boolean isForbiddenMove(int row, int col, String stone) {
        // 금수는 흑(첫 번째 플레이어 O)만 해당
        if (!stone.equals("O")) return false;

        map[row][col] = stone; // 임시 착수
        int openThrees = countOpenThree(row, col, stone);
        int openFours = countOpenFour(row, col, stone);
        map[row][col] = "."; // 복원

        return openThrees >= 2 || openFours >= 2 || isOverline(row, col, stone);
    }

    private boolean isOverline(int row, int col, String stone) {
        return count(row, col, stone, 1, 0) + count(row, col, stone, -1, 0) + 1 > 5 ||
                count(row, col, stone, 0, 1) + count(row, col, stone, 0, -1) + 1 > 5 ||
                count(row, col, stone, 1, 1) + count(row, col, stone, -1, -1) + 1 > 5 ||
                count(row, col, stone, 1, -1) + count(row, col, stone, -1, 1) + 1 > 5;
    }

    private int countOpenThree(int row, int col, String stone) {
        return countOpenPattern(row, col, stone, 3);
    }

    private int countOpenFour(int row, int col, String stone) {
        return countOpenPattern(row, col, stone, 4);
    }

    private int countOpenPattern(int row, int col, String stone, int length) {
        int openCount = 0;
        int[][] dirs = { {1,0}, {0,1}, {1,1}, {1,-1} };
        for (int[] dir : dirs) {
            int count = 1 + count(row, col, stone, dir[0], dir[1]) + count(row, col, stone, -dir[0], -dir[1]);
            if (count == length) {
                int r1 = row + (count * dir[0]);
                int c1 = col + (count * dir[1]);
                int r2 = row - (count * dir[0]);
                int c2 = col - (count * dir[1]);
                if ((isInBounds(r1, c1) && isEmpty(r1, c1)) && (isInBounds(r2, c2) && isEmpty(r2, c2))) {
                    openCount++;
                }
            }
        }
        return openCount;
    }
}
