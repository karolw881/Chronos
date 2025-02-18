package org.magister.Matrix;

// Implementacja konkretna dla liczb całkowitych
public class IntMatrix {
    private int[][] data;
    private final int rows;
    private final int cols;

    public IntMatrix(int[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    public IntMatrix add(IntMatrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Matrices dimensions must match");
        }
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return new IntMatrix(result);
    }

    public IntMatrix subtract(IntMatrix other) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Matrices dimensions must match");
        }
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = this.data[i][j] - other.data[i][j];
            }
        }
        return new IntMatrix(result);
    }

    public IntMatrix multiply(IntMatrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Invalid matrices dimensions for multiplication");
        }
        int[][] result = new int[this.rows][other.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                for (int k = 0; k < this.cols; k++) {
                    result[i][j] += this.data[i][k] * other.data[k][j];
                }
            }
        }
        return new IntMatrix(result);
    }

    public double determinant() {
        if (rows != cols) {
            throw new IllegalArgumentException("Matrix must be square");
        }
        return calculateDeterminant(this.data);
    }

    private double calculateDeterminant(int[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        double det = 0;
        for (int j = 0; j < n; j++) {
            det += Math.pow(-1, j) * matrix[0][j] * calculateDeterminant(getMinor(matrix, 0, j));
        }
        return det;
    }

    private int[][] getMinor(int[][] matrix, int row, int col) {
        int n = matrix.length;
        int[][] minor = new int[n-1][n-1];
        int r = 0, c = 0;

        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        return minor;
    }

    public boolean isInvertible() {
        return Math.abs(determinant()) > 1e-10;
    }
}