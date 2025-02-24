package org.magister.Matrix;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
@Setter
public class MatrixR<T > {
    private T[][] data;
    private final Integer rows;
    private final Integer cols;
    private final Class<T> clazz;

    // Konstruktor kopiujący dane z podanej tablicy
    public MatrixR(T[][] data, Class<T> clazz) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }
        this.rows = data.length;
        this.cols = data[0].length;
        this.clazz = clazz;
        // Tworzymy tablicę generyczną przy użyciu refleksji
        this.data = (T[][]) Array.newInstance(clazz, rows, cols);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    // Konstruktor kopiujący
    public MatrixR(MatrixR<T> matrixR) {
        this.rows = matrixR.rows;
        this.cols = matrixR.cols;
        this.clazz = matrixR.clazz;
        this.data = (T[][]) Array.newInstance(clazz, rows, cols);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrixR.data[i], 0, this.data[i], 0, cols);
        }
    }

    // Konstruktor tworzący pustą macierz o zadanych wymiarach
    public MatrixR(int rows, int cols, Class<T> clazz) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive");
        }
        this.rows = rows;
        this.cols = cols;
        this.clazz = clazz;
        this.data = (T[][]) Array.newInstance(clazz, rows, cols);
    }

    /**
     * Dodaje dwie macierze przy użyciu metody sum() wywoływanej refleksyjnie.
     */
    public MatrixR<T> add(MatrixR<T> other) throws Exception {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrices dimensions must match");
        }
        MatrixR<T> result = new MatrixR<>(rows, cols, clazz);
        // Pobieramy metodę sum(T) z klasy elementów macierzy
        Method sumMethod = clazz.getMethod("sum", clazz);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = (T) sumMethod.invoke(this.data[i][j], other.data[i][j]);
            }
        }
        return result;
    }

    /**
     * Odejmowanko
     */
    public MatrixR<T> subtract(MatrixR<T> other) throws Exception {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrices dimensions must match");
        }
        MatrixR<T> result = new MatrixR<>(rows, cols, clazz);
        Method subtractMethod = clazz.getMethod("subtract", clazz);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.data[i][j] = (T) subtractMethod.invoke(this.data[i][j], other.data[i][j]);
            }
        }
        return result;
    }

    /**
     mnozenie
     */
    public MatrixR<T> multiply(MatrixR<T> other) throws Exception {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Invalid matrices dimensions for multiplication");
        }
        MatrixR<T> result = new MatrixR<>(this.rows, other.cols, clazz);
        Method multiplyMethod = clazz.getMethod("multiply", clazz);
        Method sumMethod = clazz.getMethod("sum", clazz);

        // Inicjalizacja elementu zerowego:
        T zero;
        try {
            // Próbujemy użyć konstruktora przyjmującego String (np. "0")
            zero = clazz.getConstructor(String.class).newInstance("0");
        } catch (NoSuchMethodException e) {
            try {
                // Alternatywnie, domyślny konstruktor (zakładamy, że tworzy wartość zero)
                zero = clazz.getConstructor().newInstance();
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Cannot find a suitable constructor for matrix element initialization");
            }
        }

        // Wypełniamy wynikową macierz wartością zero (jeśli elementy są niemutowalne, można używać jednej instancji)
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                result.data[i][j] = zero;
            }
        }

        // Przeprowadzamy mnożenie macierzy
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                T sum = null;
                for (int k = 0; k < this.cols; k++) {
                    T product = (T) multiplyMethod.invoke(this.data[i][k], other.data[k][j]);
                    if (sum == null) {
                        sum = product;
                    } else {
                        sum = (T) sumMethod.invoke(sum, product);
                    }
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[");
            for (int j = 0; j < cols; j++) {
                sb.append(data[i][j]);
                if (j < cols - 1) {
                    sb.append(", ");
                }
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
