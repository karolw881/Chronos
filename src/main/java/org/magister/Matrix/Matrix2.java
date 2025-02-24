package org.magister.Matrix;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 *Tworzymy sobie typ generyczny dla matrixa
 * @param <T>
 */


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Matrix2<T>{
    private final T[][] data;
    private final int rows;
    private final int columns;
    private final Numberxx<T> numberxx;

    public Matrix2(T[][] temp, Numberxx<T> numberxx) {
        try {
            this.rows = temp.length;
            this.columns = temp[0].length;
            this.data = temp;
            this.numberxx = numberxx;
            // Sprawdzenie wymiarów
            for (int i = 1; i < rows; i++) {
                if (temp[i].length != columns) {
                    throw new IllegalArgumentException("Wszystkie wiersze muszą mieć tę samą długość");
                }
            }
        } catch (IllegalArgumentException e) {
            // Obsługa wyjątku, np. logowanie lub ponowne rzucenie
            System.err.println("Błąd: " + e.getMessage());throw e; // Możesz ponownie rzucić wyjątek, jeśli chcesz, aby był propagowany dalej
        } catch (Exception e) {
            // Obsługa innych wyjątków, jeśli to konieczne
            System.err.println("Wystąpił nieoczekiwany błąd: " + e.getMessage());throw e; // Możesz ponownie rzucić wyjątek, jeśli chcesz, aby był propagowany dalej
        }
    }




    /**
     * Dodawanie macierzy
     */
    public Matrix2<T> add(Matrix2<T> matrix) {
        if (this.rows != matrix.rows || this.columns != matrix.columns) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary");
        }

        T[][] result = (T[][]) new Object[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = numberxx.add(this.data[i][j], matrix.data[i][j]);
            }
        }

        return new Matrix2<>(result, numberxx);
    }

    /**
     * Odejmowanie macierzy
     */
    public Matrix2<T> subtract(Matrix2<T> other) {
        if (this.rows != other.rows || this.columns != other.columns) {
            throw new IllegalArgumentException("Macierze muszą mieć te same wymiary");
        }

        T[][] result = (T[][]) new Object[this.rows][this.columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = numberxx.subtract(this.data[i][j], other.data[i][j]);
            }
        }
        return new Matrix2<>(result,numberxx);
    }

    public Matrix2<T> multiply(Matrix2<T> matrix) {
        if (this.columns != matrix.rows) {
            throw new IllegalArgumentException("Liczba kolumn pierwszej macierzy musi b" +
                    "yć równa liczbie wierszy drugiej macierzy");
        }


        // Inicjalizacja
        T[][] result = (T[][]) new Object[this.rows][matrix.columns];


        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                T sum = (T) numberxx.zero();
                for (int k = 0; k < this.columns; k++) {
                    T product = numberxx.multiply(this.data[i][k], matrix.data[k][j]);
                    sum = numberxx.add(sum, product);
                }
                result[i][j] = sum;
            }
        }

        return new Matrix2<>(result, numberxx);
    }

    public Matrix2<T> divide(T scalar) {
        if (numberxx.isZero(scalar)) {
            throw new IllegalArgumentException("Nie można dzielić przez zero");
        }

        T[][] result = (T[][]) new Object[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = numberxx.divide(this.data[i][j], scalar);
            }
        }

        return new Matrix2<>(result, numberxx);
    }

    public T determinant() {
        if (rows != columns) {
            throw new IllegalArgumentException("Wyznacznik można obliczyć tylko dla macierzy kwadratowej");
        }

        if (rows == 1) {
            return data[0][0];
        }

        if (rows == 2) {
            T a = data[0][0];
            T b = data[0][1];
            T c = data[1][0];
            T d = data[1][1];

            T ad = numberxx.multiply(a, d);
            T bc = numberxx.multiply(b, c);

            return numberxx.subtract(ad, bc);
        }

        T result = (T) numberxx.zero();
        for (int j = 0; j < columns; j++) {
            Matrix2<T> subMatrix = createSubMatrix(0, j);
            T minor = subMatrix.determinant();
            T cofactor = numberxx.multiply(data[0][j], minor);

            if (j % 2 == 1) {
                cofactor = numberxx.negate(cofactor);
            }

            result = numberxx.add(result, cofactor);
        }

        return result;
    }

    /**
     * Tworzy podmacierz przez usunięcie wiersza i kolumny
     */


    /**
     * AI prawi ze
     * Kiedy usuwasz jeden wiersz i jedną kolumnę z macierzy,
     * liczba wierszy w nowej macierzy będzie o jeden mniejsza niż
     * w oryginalnej macierzy (stąd rows - 1), a liczba kolumn również
     * będzie o jeden mniejsza (stąd columns - 1).
     * @param rowToRemove
     * @param colToRemove
     * @return
     */
    private Matrix2<T> createSubMatrix(int rowToRemove, int colToRemove) {
        T[][] subData = (T[][]) new Object[rows - 1][columns - 1];
        int r = 0; // niby row w subData
        for (int i = 0; i < rows; i++) {
            if (i != rowToRemove) {
                int c = 0;
                for (int j = 0; j < columns; j++) {
                    if (j != colToRemove) {
                        subData[r][c] = data[i][j];c++;
                    }
                }r++;
            }
        }
        return new Matrix2<>(subData, numberxx);
    }

    /**
     * Sprawdza, czy macierz jest odwracalna
     */
    public boolean isInvertible() {
        if (rows != columns) {
            return false; // Tylko macierze kwadratowe mogą być odwracalne
        }

        T det = determinant();
        return !numberxx.isZero(det);
    }
}
