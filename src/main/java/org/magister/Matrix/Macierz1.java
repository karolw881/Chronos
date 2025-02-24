package org.magister.Matrix;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Macierz1 {
    private final int[][] dane;
    private final int rows;
    private final int columns;

    public Macierz1(int[][] dane) {
        this.rows = dane.length;
        this.columns = dane[0].length;
        this.dane = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(dane[i], 0, this.dane[i], 0, columns);
        }
    }

    public Macierz1 add(Macierz1 macierz1) {
        if (this.rows != macierz1.rows || this.columns != macierz1.columns) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary");
        }
        int[][] result = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = this.dane[i][j] + macierz1.dane[i][j];
            }
        }
        return new Macierz1(result);
    }

    public Macierz1 subtract(Macierz1 macierz1) {
        if (this.rows != macierz1.rows || this.columns != macierz1.columns) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary");
        }
        int[][] result = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = this.dane[i][j] - macierz1.dane[i][j];
            }
        }
        return new Macierz1(result);
    }

    public Macierz1 multiply(Macierz1 macierz1) {
        if (this.columns != macierz1.rows) {
            throw new IllegalArgumentException("Nieprawidłowe wymiary macierzy do mnożenia");
        }
        int[][] result = new int[this.rows][macierz1.columns];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < macierz1.columns; j++) {
                for (int k = 0; k < this.columns; k++) {
                    result[i][j] += this.dane[i][k] * macierz1.dane[k][j];
                }
            }
        }
        return new Macierz1(result);
    }

    public int[][] getDane() {
        return dane;
    }
}