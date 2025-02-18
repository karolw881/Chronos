package MatrixTest;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.magister.Matrix.IntMatrix;
import org.magister.Matrix.Matrix;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MatrixTest {
    private static final String INPUT_DIR = "test_data/input/";
    private static final String OUTPUT_DIR = "test_data/output/";
    // Liczba uruchomień dla każdego przypadku
    private static final int RUNS = 10;
    private static final Random random = new Random(42);

    @BeforeAll
    static void setUp() {
        createDirectories();
        generateTestFiles();
    }

    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(INPUT_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generuje pliki testowe dla macierzy kwadratowych.
     * Format pliku:
     *  pierwsza linia: rozmiar (n)
     *  następne n linii: wiersze macierzy A (elementy oddzielone przecinkami)
     *  pusta linia
     *  kolejne n linii: wiersze macierzy B
     */
    private static void generateTestFiles() {
        int[] sizes = {2, 3, 5};
        String[] types = {"random", "sorted", "reverse"};

        for (int size : sizes) {
            for (String type : types) {
                String fileName = String.format("matrix_%d_%s.txt", size, type);
                Path filePath = Paths.get(INPUT_DIR, fileName);
                if (!Files.exists(filePath)) {
                    try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
                        // Zapisywanie rozmiaru macierzy
                        writer.println(size);
                        // Generowanie pierwszej macierzy
                        int[][] matrixA = generateMatrix(size, type);
                        for (int i = 0; i < size; i++) {
                            writer.println(arrayToString(matrixA[i]));
                        }
                        // Pusta linia jako separator
                        writer.println();
                        // Generowanie drugiej macierzy
                        int[][] matrixB = generateMatrix(size, type);
                        for (int i = 0; i < size; i++) {
                            writer.println(arrayToString(matrixB[i]));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Generuje macierz o zadanym rozmiarze i typie:
    // "random" – losowe liczby,
    // "sorted" – rosnące wartości,
    // "reverse" – malejące wartości.
    private static int[][] generateMatrix(int size, String type) {
        int[][] matrix = new int[size][size];
        switch (type) {
            case "random":
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = random.nextInt(100);
                    }
                }
                break;
            case "sorted":
                int val = 0;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = val++;
                    }
                }
                break;
            case "reverse":
                val = size * size;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = val--;
                    }
                }
                break;
            default:
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = random.nextInt(100);
                    }
                }
                break;
        }
        return matrix;
    }

    private static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    // Klasa pomocnicza przechowująca parę macierzy
    private static class MatrixPair {
        int[][] matrixA;
        int[][] matrixB;

        MatrixPair(int[][] a, int[][] b) {
            this.matrixA = a;
            this.matrixB = b;
        }
    }

    // Wczytuje dane z pliku i parsuje je na dwie macierze
    private static MatrixPair loadMatrixTestData(String fileName) throws IOException {
        Path filePath = Paths.get(INPUT_DIR, fileName);
        List<String> lines = Files.readAllLines(filePath);
        if (lines.isEmpty()) {
            throw new IOException("Plik pusty: " + fileName);
        }
        int size = Integer.parseInt(lines.get(0).trim());
        int[][] matrixA = new int[size][size];
        int[][] matrixB = new int[size][size];

        // Parsowanie macierzy A (kolejne n linii po pierwszej)
        for (int i = 0; i < size; i++) {
            String line = lines.get(1 + i).trim();
            String[] parts = line.split(",");
            for (int j = 0; j < size; j++) {
                matrixA[i][j] = Integer.parseInt(parts[j].trim());
            }
        }
        // Szukanie pustej linii – zakładamy, że jest na pozycji (1 + size)
        int emptyLineIndex = 1 + size;
        // Parsowanie macierzy B (kolejne n linii po pustej linii)
        for (int i = 0; i < size; i++) {
            String line = lines.get(emptyLineIndex + 1 + i).trim();
            String[] parts = line.split(",");
            for (int j = 0; j < size; j++) {
                matrixB[i][j] = Integer.parseInt(parts[j].trim());
            }
        }
        return new MatrixPair(matrixA, matrixB);
    }

    // Konwertuje int[][] na MyInteger[][]
    private static MyInteger[][] convertToMyIntegerMatrix(int[][] matrix) {
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

    // Porównuje dwie macierze int pod względem równości elementów
    private static boolean matricesEqual(int[][] m1, int[][] m2) {
        if (m1.length != m2.length || m1[0].length != m2[0].length) return false;
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                if (m1[i][j] != m2[i][j]) return false;
            }
        }
        return true;
    }

    // Oblicza medianę z listy czasów (w nanosekundach)
    private static double computeMedian(List<Long> times) {
        List<Long> sorted = new ArrayList<>(times);
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 0) {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        } else {
            return sorted.get(size / 2);
        }
    }

    private static void writeResults(String fileName, String content) throws IOException {
        Path filePath = Paths.get(OUTPUT_DIR, fileName);
        Files.writeString(filePath, content);
    }

    // Używamy refleksyjnego testu – dla każdego przypadku (np. "2_random", "3_reverse" itd.)
    // wykonujemy RUNS uruchomień, mierzymy osobno czasy dla dodawania i mnożenia
    @ParameterizedTest
    @ValueSource(strings = {
            "2_random", "2_sorted", "2_reverse",
            "3_random", "3_sorted", "3_reverse",
            "5_random", "5_sorted", "5_reverse"
    })
    void testMatrixOperations(String testCase) {
        // Listy przechowujące czasy operacji (w nanosekundach)
        List<Long> objectAdditionTimes = new ArrayList<>();
        List<Long> reflectiveAdditionTimes = new ArrayList<>();
        List<Long> objectMultiplicationTimes = new ArrayList<>();
        List<Long> reflectiveMultiplicationTimes = new ArrayList<>();

        StringBuilder aggregatedResults = new StringBuilder();
        aggregatedResults.append("Agregowane wyniki dla testu: ").append(testCase).append("\n");
        aggregatedResults.append("Liczba uruchomień: ").append(RUNS).append("\n\n");

        for (int version = 1; version <= RUNS; version++) {
            String inputFileName = "matrix_" + testCase + ".txt";
            MatrixPair matrixPair;
            try {
                matrixPair = loadMatrixTestData(inputFileName);
            } catch (IOException e) {
                e.printStackTrace();
                Assertions.fail("Nie udało się wczytać danych z pliku: " + inputFileName);
                return;
            }

            int size = matrixPair.matrixA.length;
            StringBuilder versionResult = new StringBuilder();
            versionResult.append("Test: ").append(testCase)
                    .append("_version").append(version).append("\n");
            versionResult.append("Rozmiar macierzy: ").append(size).append("x").append(size).append("\n\n");

            // IMPLEMENTACJA OBIEKTOWA – IntMatrix
            IntMatrix intMatrixA = new IntMatrix(matrixPair.matrixA);
            IntMatrix intMatrixB = new IntMatrix(matrixPair.matrixB);

            long startTime = System.nanoTime();
            IntMatrix intAdditionResult = intMatrixA.add(intMatrixB);
            long endTime = System.nanoTime();
            long objectAdditionTime = endTime - startTime;

            startTime = System.nanoTime();
            IntMatrix intMultiplicationResult = intMatrixA.multiply(intMatrixB);
            endTime = System.nanoTime();
            long objectMultiplicationTime = endTime - startTime;

            // IMPLEMENTACJA REFLEKCYJNA – Matrix<MyInteger>
            MyInteger[][] myMatrixA = convertToMyIntegerMatrix(matrixPair.matrixA);
            MyInteger[][] myMatrixB = convertToMyIntegerMatrix(matrixPair.matrixB);
            Matrix<MyInteger> reflectiveMatrixA = new Matrix<>(myMatrixA);
            Matrix<MyInteger> reflectiveMatrixB = new Matrix<>(myMatrixB);

            startTime = System.nanoTime();
            Matrix<MyInteger> reflectiveAdditionResult = null;
            try {
                reflectiveAdditionResult = reflectiveMatrixA.add(reflectiveMatrixB);
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Wyjątek przy dodawaniu w wersji refleksyjnej (version " + version + "): " + e.getMessage());
            }
            endTime = System.nanoTime();
            long reflectiveAdditionTime = endTime - startTime;

            startTime = System.nanoTime();
            Matrix<MyInteger> reflectiveMultiplicationResult = null;
            try {
                reflectiveMultiplicationResult = reflectiveMatrixA.multiply(reflectiveMatrixB);
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Wyjątek przy mnożeniu w wersji refleksyjnej (version " + version + "): " + e.getMessage());
            }
            endTime = System.nanoTime();
            long reflectiveMultiplicationTime = endTime - startTime;

            objectAdditionTimes.add(objectAdditionTime);
            reflectiveAdditionTimes.add(reflectiveAdditionTime);
            objectMultiplicationTimes.add(objectMultiplicationTime);
            reflectiveMultiplicationTimes.add(reflectiveMultiplicationTime);

            // Dla porównania wyników – konwersja wyniku refleksyjnego do int[][]
            int[][] reflectiveAdditionInt = convertMyIntegerMatrixToInt(reflectiveAdditionResult);
            int[][] reflectiveMultiplicationInt = convertMyIntegerMatrixToInt(reflectiveMultiplicationResult);

            versionResult.append("Czasy (dodawanie):\n");
            versionResult.append("  Obiektowa: ").append(objectAdditionTime / 1000.0).append(" µs\n");
            versionResult.append("  Refleksyjna: ").append(reflectiveAdditionTime / 1000.0).append(" µs\n");
            versionResult.append("  Różnica: ").append((reflectiveAdditionTime - objectAdditionTime) / 1000.0).append(" µs\n\n");

            versionResult.append("Czasy (mnożenie):\n");
            versionResult.append("  Obiektowa: ").append(objectMultiplicationTime / 1000.0).append(" µs\n");
            versionResult.append("  Refleksyjna: ").append(reflectiveMultiplicationTime / 1000.0).append(" µs\n");
            versionResult.append("  Różnica: ").append((reflectiveMultiplicationTime - objectMultiplicationTime) / 1000.0).append(" µs\n\n");

            versionResult.append("Wyniki operacji (dodawanie):\n");
            versionResult.append("  IntMatrix:\n").append(matrixToString(getIntMatrixData(intAdditionResult))).append("\n");
            versionResult.append("  Matrix (refleksyjna):\n").append(matrixToString(reflectiveAdditionInt)).append("\n\n");

            versionResult.append("Wyniki operacji (mnożenie):\n");
            versionResult.append("  IntMatrix:\n").append(matrixToString(getIntMatrixData(intMultiplicationResult))).append("\n");
            versionResult.append("  Matrix (refleksyjna):\n").append(matrixToString(reflectiveMultiplicationInt)).append("\n\n");

            // Walidacja – porównanie macierzy wynikowych
            boolean additionEqual = matricesEqual(getIntMatrixData(intAdditionResult), reflectiveAdditionInt);
            boolean multiplicationEqual = matricesEqual(getIntMatrixData(intMultiplicationResult), reflectiveMultiplicationInt);

            versionResult.append("Walidacja:\n");
            versionResult.append("  Wynik dodawania równy: ").append(additionEqual).append("\n");
            versionResult.append("  Wynik mnożenia równy: ").append(multiplicationEqual).append("\n");

            // Zapis wyników dla danej wersji do osobnego pliku
            String individualFileName = "results_matrix_" + testCase + "_version" + version + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            try {
                writeResults(individualFileName, versionResult.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            aggregatedResults.append("Wersja ").append(version).append(" - ");
            aggregatedResults.append("Dodawanie: Obiektowa: ").append(objectAdditionTime / 1000.0)
                    .append(" µs, Refleksyjna: ").append(reflectiveAdditionTime / 1000.0).append(" µs; ");
            aggregatedResults.append("Mnożenie: Obiektowa: ").append(objectMultiplicationTime / 1000.0)
                    .append(" µs, Refleksyjna: ").append(reflectiveMultiplicationTime / 1000.0).append(" µs\n");

            // Asercje – jeżeli wyniki nie są równe, test upadnie
            Assertions.assertTrue(additionEqual, "Wynik dodawania nie jest zgodny w wersji " + version);
            Assertions.assertTrue(multiplicationEqual, "Wynik mnożenia nie jest zgodny w wersji " + version);
        }

        // Obliczanie zagregowanych statystyk
        Collections.sort(objectAdditionTimes);
        Collections.sort(reflectiveAdditionTimes);
        Collections.sort(objectMultiplicationTimes);
        Collections.sort(reflectiveMultiplicationTimes);

        long objectAdditionMin = objectAdditionTimes.get(0);
        long objectAdditionMax = objectAdditionTimes.get(objectAdditionTimes.size() - 1);
        double objectAdditionAvg = objectAdditionTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double objectAdditionMedian = computeMedian(objectAdditionTimes);

        long reflectiveAdditionMin = reflectiveAdditionTimes.get(0);
        long reflectiveAdditionMax = reflectiveAdditionTimes.get(reflectiveAdditionTimes.size() - 1);
        double reflectiveAdditionAvg = reflectiveAdditionTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double reflectiveAdditionMedian = computeMedian(reflectiveAdditionTimes);

        long objectMultiplicationMin = objectMultiplicationTimes.get(0);
        long objectMultiplicationMax = objectMultiplicationTimes.get(objectMultiplicationTimes.size() - 1);
        double objectMultiplicationAvg = objectMultiplicationTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double objectMultiplicationMedian = computeMedian(objectMultiplicationTimes);

        long reflectiveMultiplicationMin = reflectiveMultiplicationTimes.get(0);
        long reflectiveMultiplicationMax = reflectiveMultiplicationTimes.get(reflectiveMultiplicationTimes.size() - 1);
        double reflectiveMultiplicationAvg = reflectiveMultiplicationTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double reflectiveMultiplicationMedian = computeMedian(reflectiveMultiplicationTimes);

        aggregatedResults.append("\nStatystyki zagregowane (czas w µs):\n");
        aggregatedResults.append("Operacja dodawania:\n");
        aggregatedResults.append("  Obiektowa – Min: ").append(objectAdditionMin / 1000.0)
                .append(", Max: ").append(objectAdditionMax / 1000.0)
                .append(", Średnia: ").append(objectAdditionAvg / 1000.0)
                .append(", Mediana: ").append(objectAdditionMedian / 1000.0).append("\n");
        aggregatedResults.append("  Refleksyjna – Min: ").append(reflectiveAdditionMin / 1000.0)
                .append(", Max: ").append(reflectiveAdditionMax / 1000.0)
                .append(", Średnia: ").append(reflectiveAdditionAvg / 1000.0)
                .append(", Mediana: ").append(reflectiveAdditionMedian / 1000.0).append("\n");

        aggregatedResults.append("\nOperacja mnożenia:\n");
        aggregatedResults.append("  Obiektowa – Min: ").append(objectMultiplicationMin / 1000.0)
                .append(", Max: ").append(objectMultiplicationMax / 1000.0)
                .append(", Średnia: ").append(objectMultiplicationAvg / 1000.0)
                .append(", Mediana: ").append(objectMultiplicationMedian / 1000.0).append("\n");
        aggregatedResults.append("  Refleksyjna – Min: ").append(reflectiveMultiplicationMin / 1000.0)
                .append(", Max: ").append(reflectiveMultiplicationMax / 1000.0)
                .append(", Średnia: ").append(reflectiveMultiplicationAvg / 1000.0)
                .append(", Mediana: ").append(reflectiveMultiplicationMedian / 1000.0).append("\n");

        String aggregatedFileName = "results_matrix_" + testCase + "_all_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        try {
            writeResults(aggregatedFileName, aggregatedResults.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Pobiera dane wewnętrzne macierzy z obiektu IntMatrix (poprzez refleksję)
    private static int[][] getIntMatrixData(IntMatrix matrix) {
        try {
            Field field = IntMatrix.class.getDeclaredField("data");
            field.setAccessible(true);
            return (int[][]) field.get(matrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Pobiera dane wewnętrzne macierzy z obiektu Matrix<T> (poprzez refleksję)
    private static <T extends Number> T[][] getMatrixData(Matrix<T> matrix) {
        try {
            Field field = Matrix.class.getDeclaredField("data");
            field.setAccessible(true);
            return (T[][]) field.get(matrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Konwertuje wynik refleksyjnej macierzy (Matrix<MyInteger>) na int[][]
    private static int[][] convertMyIntegerMatrixToInt(Matrix<MyInteger> matrix) {
        Number[][] data = getMatrixData(matrix);
        int rows = data.length;
        int cols = data[0].length;
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = data[i][j].intValue();
            }
        }
        return result;
    }


    private static String matrixToString(int[][] data) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : data) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }

    // Klasa pomocnicza MyInteger – opakowanie liczby całkowitej z metodami sum i multiply,
    // wymaganymi przez refleksyjną implementację Matrix.
    public static class MyInteger extends Number {
        private final int value;

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
}
