package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Field {
    private int[][] field = null;
    private static final Random rnd = new Random();
    public Field() {
    }

    public void generateField(int rowCount, int colCount) {
        this.field = new int[rowCount][colCount];
    }

    public int[] getRow(int i) {
        return field[i];
    }

    public void setRow(int i, int[] arr) {
        this.field[i] = arr;
    }

    public int[] getColumn(int j) {
        int[] res = new int[field.length];
        for(int i = 0; i < field.length; i ++) {
            res[i] = field[i][j];
        }
        return res;
    }

    public void setColumn(int j, int[] arr) {
        for (int i = 0; i < field.length; i ++) {
            field[i][j] = arr[i];
        }
    }

    public int getRowCount() {
        return field == null ? 0 : field.length;
    }

    public int getColCount() {
        return field == null ? 0 : field[0].length;
    }

    public int getCell(int row, int col) {
        return (row < 0 || row >= getRowCount() || col < 0 || col >= getColCount()) ? 0 : field[row][col];
    }

    public void setCell(int row, int col, int value) {
        if (!(row < 0 || row >= getRowCount() || col < 0 || col >= getColCount())) {
            field[row][col] = value;
        }
    }

    public int[] findRandomIndexOfCellsWithZero() {
        List<int[]> cellsWithZero = new ArrayList<>();
        for (int i = 0; i < field.length; i ++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == 0) {
                    cellsWithZero.add(new int[]{i, j});
                }
            }
        }
        int[] randomIandJ = cellsWithZero.get(rnd.nextInt(cellsWithZero.size()));
        return randomIandJ;
    }
}
