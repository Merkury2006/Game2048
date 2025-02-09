package Game;

import java.util.Random;

public class Game {
    private static final Random rnd = new Random();
    Field field = new Field();
    private int score = 0;
    private boolean win = false;

    public Game() {
    }

    public void newGame(int rowCount, int colCount, int score) {
        field.generateField(rowCount, colCount);
        this.score = score;
        this.win = false;
        generateNewCell(false);
        generateNewCell(false);
    }

    public void right() {
        boolean doneShift = false;
        int rowCount = field.getRowCount();
        for(int i = 0; i< rowCount; i++){
            //Запрашиваем очередную строку
            int[] arr = field.getRow(i);
            //Меняем порядок чисел на противоположный
            arr = reverseArr(arr);
            //Пытаемся сдвинуть числа в этом столбце
            Object[] res = shiftRow(arr, false);
            //Если произошел сдвиг, то мы должны сгенерировать новое число
            if ((boolean) res[1]) {
                doneShift = true;
            }
            //Возвращаем линию в исходный порядок
            int[] resShift = (int[]) res[0];
            resShift = reverseArr(resShift);
            //Записываем изменённую строку
            field.setRow(i, resShift);
        }
        //Создаем новое число
        if (doneShift) {
            generateNewCell(true);
        }
    }

    public void down() {
        boolean doneShift = false;
        int colCount = field.getColCount();
        for(int i = 0; i < colCount; i++){
            //Запрашиваем очередную строку
            int[] arr = field.getColumn(i);
            //Меняем порядок чисел на противоположный
            arr = reverseArr(arr);
            //Пытаемся сдвинуть числа в этом столбце
            Object[] res = shiftRow(arr,false);
            //Если произошел сдвиг, то мы должны сгенерировать новое число
            if ((boolean) res[1]) {
                doneShift = true;
            }
            //Возвращаем линию в исходный порядок
            int[] resShift = (int[]) res[0];
            resShift = reverseArr(resShift);
            //Записываем изменённую строку
            field.setColumn(i, resShift);
        }
        //Создаем новое число
        if (doneShift) {
            generateNewCell(true);
        }
    }

    public void left() {
        boolean doneShift = false;
        int rowCount = field.getRowCount();
        for(int i = 0; i < rowCount; i++){
            //Запрашиваем очередную строку
            int[] arr = field.getRow(i);
            //Пытаемся сдвинуть числа в этом столбце
            Object[] res = shiftRow(arr, false);
            //Если произошел сдвиг, то мы должны сгенерировать новое число
            if ((boolean) res[1]) {
                doneShift = true;
            }
            int[] resShift = (int[]) res[0];
            //Записываем изменённую строку
            field.setRow(i, resShift);
        }
        //Создаем новое число
        if (doneShift) {
            generateNewCell(true);
        }
    }

    public void up() {
        boolean doneShift = false;
        int colCount = field.getColCount();
        for(int i = 0; i < colCount; i++){
            //Запрашиваем очередную строку
            int[] arr = field.getColumn(i);
            //Пытаемся сдвинуть числа в этом столбце
            Object[] res = shiftRow(arr, false);
            //Если произошел сдвиг, то мы должны сгенерировать новое число
            if ((boolean) res[1]) {
                doneShift = true;
            }
            int[] resShift = (int[]) res[0];
            //Записываем изменённую строку
            field.setColumn(i, resShift);
        }
        //Создаем новое число
        if (doneShift) {
            generateNewCell(true);
        }
    }

    private Object[] shiftRow(int[] row, boolean checkAnyoneMove) {
        boolean didMove = false;
        int[] arrWithoutZero = new int[row.length]; //Массив без ненужных нулей(размер таблицы сохраняем)
        int k = 0;
        for (int i = 0; i < row.length; i++) {
            if (row[i] != 0) {
                if (i != k) {
                    didMove = true;
                }
                arrWithoutZero[k] = row[i];
                k++;
            }
        }
        int[] res = new int[arrWithoutZero.length];
        int j = 0;
        int i = 0;
        while (i < arrWithoutZero.length) {
            if ((i + 1 < arrWithoutZero.length) && (arrWithoutZero[i] == arrWithoutZero[i + 1])
                    && arrWithoutZero[i] != 0) {
                res[j] = arrWithoutZero[i] * 2;
                didMove = true;
                if (!checkAnyoneMove) {
                    if(res[j] == 2048) {
                        win = true;
                    }
                    score += res[j];
                }
                i++;
            } else {
                res[j] = arrWithoutZero[i];
            }
            j++;
            i++;
        }
        return new Object[]{res, didMove};
    }

    private void generateNewCell(boolean ok) {
        int state = 2;
        if (ok) {
            if (rnd.nextInt(100) <= 15) { //15 процентов шанса на выпадение 4 а не 2
                state = 4;
            }
        }
        int[] currentIandJ = field.findRandomIndexOfCellsWithZero();
        field.setCell(currentIandJ[0], currentIandJ[1], state);
    }



    public boolean haveAnyoneMove() {
        int colCount = field.getColCount();
        int rowCount = field.getRowCount();
        boolean left = false;
        boolean right = false;
        boolean down = false;
        boolean up = false;
        for (int i = 0; i < colCount; i ++) {
            int[] arr = field.getColumn(i);
            //Пытаемся сдвинуть числа в этом столбце
            Object[] resDown = shiftRow(arr, true);
            //Если произошел сдвиг, то свободные ячейки есть
            if ((boolean) resDown[1]) {
                down = true;
                break;
            }
            arr = reverseArr(arr);
            Object[] resUp = shiftRow(arr, true);
            //Если произошел сдвиг, то свободные ячейки есть
            if ((boolean) resUp[1]) {
                up = true;
                break;
            }
        }

        for (int i = 0; i < rowCount; i ++) {
            int[] arr = field.getRow(i);
            //Пытаемся сдвинуть числа в этом столбце
            Object[] resLeft= shiftRow(arr, true);
            //Если произошел сдвиг, то свободные ячейки есть
            if ((boolean) resLeft[1]) {
                left = true;
                break;
            }
            arr = reverseArr(arr);
            Object[] resRight = shiftRow(arr, true);
            //Если произошел сдвиг, то свободные ячейки есть
            if ((boolean) resRight[1]) {
                right = true;
                break;
            }
        }
        return right || left || up || down;
    }

    public int getCell(int row, int col) {
        return field.getCell(row, col);
    }

    public boolean win() {
        return win;
    }

    public int getRowCount() {
        return field.getRowCount();
    }

    public int getColCount() {
        return field.getColCount();
    }

    public int getScore() {
        return score;
    }

    public static int[] reverseArr(int[] arr) {
        int[] res = new int[arr.length];
        for(int i = 0; i < res.length; i++){
            res[i] = arr[res.length-i-1];
        }
        return res;
    }
}

