package org.magister.Matrix;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Macierz2<T extends Number> {
    private final T[][] dane;
    private final int rows;
    private final int columns;

    public Macierz2(T[][] dane) {
        this.rows = dane.length;
        this.columns = dane[0].length;
        this.dane = dane;
    }

    public Macierz2<T> add(Macierz2<T> macierz2) {
        if (this.rows != macierz2.rows || this.columns != macierz2.columns) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary");
        }


        T[][] result = (T[][]) java.lang.reflect.Array.newInstance(
                dane.getClass().getComponentType().getComponentType(), rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double sum = this.dane[i][j].doubleValue() + macierz2.dane[i][j].doubleValue();
                result[i][j] = convertToT(sum);
            }
        }

        return new Macierz2<>(result);
    }

    public Macierz2<T> subtract(Macierz2<T> macierz2) {
        if (this.rows != macierz2.rows || this.columns != macierz2.columns) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary");
        }


        T[][] result = (T[][]) java.lang.reflect.Array.newInstance(
                dane.getClass().getComponentType().getComponentType(), rows, columns);


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double diff = this.dane[i][j].doubleValue() - macierz2.dane[i][j].doubleValue();
                result[i][j] = convertToT(diff);
            }
        }

        return new Macierz2<>(result);
    }

    public Macierz2<T> multiply(Macierz2<T> macierz2) {
        if (this.columns != macierz2.rows) {
            throw new IllegalArgumentException("Nieprawidłowe wymiary macierzy do mnożenia");
        }


        T[][] result = (T[][]) java.lang.reflect.Array.newInstance(
                dane.getClass().getComponentType().getComponentType(), rows, columns);


        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < macierz2.columns; j++) {
                double sum = 0.0;
                for (int k = 0; k < this.columns; k++) {
                    sum += this.dane[i][k].doubleValue() * macierz2.dane[k][j].doubleValue();
                }
                result[i][j] = convertToT(sum);
            }
        }

        return new Macierz2<>(result);
    }

    public Macierz2<T> divide(T scalar) {
        if (scalar.doubleValue() == 0) {
            throw new IllegalArgumentException("Nie można dzielić przez zero");
        }


        T[][] result = (T[][]) java.lang.reflect.Array.newInstance(
                dane.getClass().getComponentType().getComponentType(), rows, columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                double div = this.dane[i][j].doubleValue() / scalar.doubleValue();
                result[i][j] = convertToT(div);
            }
        }

        return new Macierz2<>(result);
    }



    private T convertToT(double value) {
        if (dane[0][0] instanceof Integer) {
            return (T) Integer.valueOf((int) value);
        } else if (dane[0][0] instanceof Double) {
            return (T) Double.valueOf(value);
        } else if (dane[0][0] instanceof Float) {
            return (T) Float.valueOf((float) value);
        } else if (dane[0][0] instanceof Long) {
            return (T) Long.valueOf((long) value);
        }
        throw new UnsupportedOperationException("Nieobsługiwany typ numeryczny");
    }

    public T[][] getDane() {
        return dane;
    }
}