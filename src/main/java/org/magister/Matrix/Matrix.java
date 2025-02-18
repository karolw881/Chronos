package org.magister.Matrix;

import java.lang.reflect.Method;

// Implementacja generyczna z użyciem refleksji
public class Matrix<T extends Number> {
    private T[][] data;
    private final int rows;
    private final int cols;


    public Matrix(T[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = (T[][]) new Number[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    public Matrix<T> add(Matrix<T> other) throws Exception {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException("Matrices dimensions must match");
        }


        T[][] result = (T[][]) new Number[rows][cols];
        Method addMethod = data[0][0].getClass().getMethod("sum", data[0][0].getClass());

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = (T) addMethod.invoke(data[i][j], other.data[i][j]);
            }
        }
        return new Matrix<>(result);
    }

    public Matrix<T> multiply(Matrix<T> other) throws Exception {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Invalid matrices dimensions for multiplication");
        }

        T[][] result = (T[][]) new Number[this.rows][other.cols];
        Method multiplyMethod = data[0][0].getClass().getMethod("multiply", data[0][0].getClass());
        Method addMethod = data[0][0].getClass().getMethod("sum", data[0][0].getClass());

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                result[i][j] = (T) data[0][0].getClass().getConstructor(String.class).newInstance("0");
                for (int k = 0; k < this.cols; k++) {
                    T product = (T) multiplyMethod.invoke(this.data[i][k], other.data[k][j]);
                    result[i][j] = (T) addMethod.invoke(result[i][j], product);
                }
            }
        }
        return new Matrix<>(result);
    }
}