package MatrixTest;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.magister.Matrix.IntMatrix;
import org.magister.Matrix.Matrix;

import java.lang.reflect.Field;

public class MatrixOperationsTest {

    // Klasa pomocnicza MyInteger wykorzystywana w refleksyjnej implementacji Matrix
    public static class MyInteger extends Number {
        final int value;

        public MyInteger(String s) {
            this.value = Integer.parseInt(s);
        }

        public MyInteger(int value) {
            this.value = value;
        }

        public MyInteger sum(MyInteger other) {
            return new MyInteger(this.value + other.value);
        }

        public MyInteger multiply(MyInteger other) {
            return new MyInteger(this.value * other.value);
        }

        @Override
        public int intValue() {
            return value;
        }

        @Override
        public long longValue() {
            return value;
        }

        @Override
        public float floatValue() {
            return value;
        }

        @Override
        public double doubleValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    // Konwertuje int[][] na MyInteger[][], aby utworzyć obiekt Matrix<MyInteger>
    private static MyInteger[][] convertIntToMyInteger(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        MyInteger[][] result = new MyInteger[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = new MyInteger(matrix[i][j]);
            }
        }
        return result;
    }

    // Używamy refleksji, aby pobrać prywatne pole "data" z obiektu Matrix<T>
    private static <T extends Number> T[][] getMatrixData(Matrix<T> matrix) {
        try {
            Field field = Matrix.class.getDeclaredField("data");
            field.setAccessible(true);
            return (T[][]) field.get(matrix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Konwertuje wynik refleksyjnej macierzy (Matrix<MyInteger>) na int[][]
    private static int[][] convertMyIntegerMatrixToInt(Matrix<MyInteger> matrix) {
        // Pole "data" zostało utworzone jako Number[][]
        Number[][] data = (Number[][]) getMatrixData(matrix);
        int rows = data.length;
        int cols = data[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                result[i][j] = data[i][j].intValue();
            }
        }
        return result;
    }

    // Pobiera wewnętrzne dane z obiektu IntMatrix (pole "data")
    private static int[][] getIntMatrixData(IntMatrix matrix) {
        try {
            Field field = IntMatrix.class.getDeclaredField("data");
            field.setAccessible(true);
            return (int[][]) field.get(matrix);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Porównuje dwie macierze int[][] pod względem równości elementów
    private static boolean matricesEqual(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        if (a[0].length != b[0].length) return false;
        for (int i = 0; i < a.length; i++){
            for (int j = 0; j < a[0].length; j++){
                if (a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }

    // Test operacji dodawania
    @Test
    public void testAddition() throws Exception {
        int[][] a = {{1, 2}, {3, 4}};
        int[][] b = {{5, 6}, {7, 8}};
        int[][] expected = {{6, 8}, {10, 12}};

        // IntMatrix – operacja dodawania
        IntMatrix intMatrixA = new IntMatrix(a);
        IntMatrix intMatrixB = new IntMatrix(b);
        IntMatrix intAdditionResult = intMatrixA.add(intMatrixB);
        int[][] resultInt = getIntMatrixData(intAdditionResult);

        // Matrix<MyInteger> – operacja dodawania
        MyInteger[][] mA = convertIntToMyInteger(a);
        MyInteger[][] mB = convertIntToMyInteger(b);
        Matrix<MyInteger> reflectiveMatrixA = new Matrix<>(mA);
        Matrix<MyInteger> reflectiveMatrixB = new Matrix<>(mB);
        Matrix<MyInteger> reflectiveAdditionResult = reflectiveMatrixA.add(reflectiveMatrixB);
        int[][] resultReflective = convertMyIntegerMatrixToInt(reflectiveAdditionResult);

        Assertions.assertTrue(matricesEqual(expected, resultInt), "IntMatrix addition failed");
        Assertions.assertTrue(matricesEqual(expected, resultReflective), "Reflective Matrix addition failed");
    }

    // Test operacji odejmowania – dostępny tylko w IntMatrix
    @Test
    public void testSubtraction() {
        int[][] a = {{5, 6}, {7, 8}};
        int[][] b = {{1, 2}, {3, 4}};
        int[][] expected = {{4, 4}, {4, 4}};

        IntMatrix intMatrixA = new IntMatrix(a);
        IntMatrix intMatrixB = new IntMatrix(b);
        IntMatrix intSubtractionResult = intMatrixA.subtract(intMatrixB);
        int[][] resultInt = getIntMatrixData(intSubtractionResult);

        Assertions.assertTrue(matricesEqual(expected, resultInt), "IntMatrix subtraction failed");
    }

    // Test operacji mnożenia
    @Test
    public void testMultiplication() throws Exception {
        int[][] a = {{1, 2}, {3, 4}};
        int[][] b = {{5, 6}, {7, 8}};
        int[][] expected = {{19, 22}, {43, 50}};

        // IntMatrix – mnożenie
        IntMatrix intMatrixA = new IntMatrix(a);
        IntMatrix intMatrixB = new IntMatrix(b);
        IntMatrix intMultiplicationResult = intMatrixA.multiply(intMatrixB);
        int[][] resultInt = getIntMatrixData(intMultiplicationResult);

        // Matrix<MyInteger> – mnożenie
        MyInteger[][] mA = convertIntToMyInteger(a);
        MyInteger[][] mB = convertIntToMyInteger(b);
        Matrix<MyInteger> reflectiveMatrixA = new Matrix<>(mA);
        Matrix<MyInteger> reflectiveMatrixB = new Matrix<>(mB);
        Matrix<MyInteger> reflectiveMultiplicationResult = reflectiveMatrixA.multiply(reflectiveMatrixB);
        int[][] resultReflective = convertMyIntegerMatrixToInt(reflectiveMultiplicationResult);

        Assertions.assertTrue(matricesEqual(expected, resultInt), "IntMatrix multiplication failed");
        Assertions.assertTrue(matricesEqual(expected, resultReflective), "Reflective Matrix multiplication failed");
    }

    // Test wyznacznika – dla IntMatrix
    @Test
    public void testDeterminant() {
        int[][] a = {{1, 2}, {3, 4}};
        IntMatrix intMatrixA = new IntMatrix(a);
        double expected = 1 * 4 - 2 * 3; // -2
        double det = intMatrixA.determinant();
        Assertions.assertEquals(expected, det, 1e-10, "Determinant calculation failed");
    }
}
