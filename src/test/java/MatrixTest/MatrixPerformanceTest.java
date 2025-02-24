package MatrixTest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MatrixPerformanceTest {
    private static final String INPUT_DIR = "test_data/matrix_input/";
    private static final String OUTPUT_DIR = "test_data/matrix_output/";
    private static final int RUNS = 10;
    // Testujemy macierze kwadratowe o podanych rozmiarach
    private static final int[] SIZES = {10, 50, 100, 200};
    private static final Random random = new Random(42);

    // Konkretny Numberxx dla Integer – bez użycia MyInteger
    private final IntegerNumber numberxx = new IntegerNumber();




    @BeforeAll
    void setUp() {
        createDirectories();
        generateTestFiles();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(INPUT_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generowanie przykładowych plików testowych – macierze zapisywane są w formacie:
    // Każdy wiersz macierzy w osobnej linii, liczby oddzielone spacjami.
    private void generateTestFiles() {
        int[] sizes = SIZES;
        for (int size : sizes) {
            // Generujemy macierz A
            String fileA = String.format("matrix_%d_A.txt", size);
            Path pathA = Paths.get(INPUT_DIR, fileA);
            if (!Files.exists(pathA)) {
                Integer[][] matrix = new Integer[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = random.nextInt(100);
                    }
                }
                writeMatrixToFile(pathA, matrix);
            }
            // Generujemy macierz B (dla operacji innych niż determinant)
            String fileB = String.format("matrix_%d_B.txt", size);
            Path pathB = Paths.get(INPUT_DIR, fileB);
            if (!Files.exists(pathB)) {
                Integer[][] matrix = new Integer[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        matrix[i][j] = random.nextInt(100);
                    }
                }
                writeMatrixToFile(pathB, matrix);
            }
        }
    }

    // Zapisuje macierz do pliku – każdy wiersz w osobnej linii, liczby oddzielone spacjami.
    private void writeMatrixToFile(Path path, Integer[][] matrix) {
        try (Formatter formatter = new Formatter(path.toFile())) {
            for (Integer[] row : matrix) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < row.length; j++) {
                    sb.append(row[j]);
                    if (j < row.length - 1) {
                        sb.append(" ");
                    }
                }
                formatter.format("%s%n", sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Odczytuje macierz z pliku – zakładamy, że macierz zapisana jest jako wiersze z liczbami oddzielonymi spacjami.
    private Integer[][] loadMatrix(String fileName) {
        try {
            Path filePath = Paths.get(INPUT_DIR, fileName);
            List<String> lines = Files.readAllLines(filePath);
            int rows = lines.size();
            if (rows == 0) {
                throw new IOException("Plik " + fileName + " jest pusty");
            }
            String[] firstRow = lines.get(0).trim().split("\\s+");
            int cols = firstRow.length;
            Integer[][] matrix = new Integer[rows][cols];
            for (int i = 0; i < rows; i++) {
                String[] parts = lines.get(i).trim().split("\\s+");
                if (parts.length != cols) {
                    throw new IOException("Wiersz " + i + " w pliku " + fileName + " ma niepoprawną liczbę kolumn");
                }
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = Integer.parseInt(parts[j]);
                }
            }
            return matrix;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ParameterizedTest
    @CsvSource({
            "10, add",
            "10, subtract",
            "10, multiply",
            "10, determinant",
            "50, add",
            "50, subtract",
            "50, multiply",
            "50, determinant",
            "100, add",
            "100, subtract",
            "100, multiply",
            "100, determinant",
            "200, add",
            "200, subtract",
            "200, multiply",
            "200, determinant"
    })

    /*
    void testMatrixOperationPerformance(int size, String operation) {
        List<Long> times = new ArrayList<>();
        StringBuilder aggregatedResults = new StringBuilder();
        aggregatedResults.append("Wyniki testów dla macierzy o rozmiarze: ")
                .append(size).append("x").append(size)
                .append(", operacja: ").append(operation).append("\n");
        aggregatedResults.append("Liczba powtórzeń: ").append(RUNS).append("\n\n");

        Matrix2<Integer> matrixA ;
        Matrix2<Integer> matrixB = null;

        matrixA = new Matrix2<>(loadMatrix(String.format("matrix_%d_A.txt", size)), numberxx);

        if (!operation.equals("determinant")) {
            matrixB = new Matrix2<>(loadMatrix(String.format("matrix_%d_B.txt", size)), numberxx);
        }

        for (int run = 1; run <= RUNS; run++) {
            long startTime = System.nanoTime();
            Object result = null;
            try {
                switch (operation) {
                    case "add":
                        result = matrixA.add(matrixB);
                        break;
                    case "subtract":
                        result = matrixA.subtract(matrixB);
                        break;
                    case "multiply":
                        result = matrixA.multiply(matrixB);
                        break;
                    case "determinant":
                        result = matrixA.determinant();
                        break;
                    default:
                        Assertions.fail("Nieznana operacja: " + operation);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Wyjątek przy operacji " + operation + " w powtórzeniu " + run + ": " + e.getMessage());
            }
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            times.add(duration);

            // Walidacja wyniku – dla operacji macierzowych sprawdzamy poprawność wymiarów
            if (result instanceof Matrix2) {
                Matrix2<Integer> resMatrix = (Matrix2<Integer>) result;
                if (operation.equals("add") || operation.equals("subtract")) {
                    Assertions.assertEquals(matrixA.getRows(), resMatrix.getRows(), "Różna liczba wierszy");
                    Assertions.assertEquals(matrixA.getColumns(), resMatrix.getColumns(), "Różna liczba kolumn");
                } else if (operation.equals("multiply")) {
                    Assertions.assertEquals(matrixA.getRows(), resMatrix.getRows(), "Różna liczba wierszy");
                    Assertions.assertEquals(matrixB.getColumns(), resMatrix.getColumns(), "Różna liczba kolumn");
                }
            } else { // dla determinanty – sprawdzamy, że wynik nie jest null
                Assertions.assertNotNull(result, "Wynik wyznacznika jest nullem");
            }

            StringBuilder runResult = new StringBuilder();
            runResult.append("Powtórzenie ").append(run)
                    .append(" - czas: ").append(duration / 1000.0).append(" mikrosekund\n");
            aggregatedResults.append(runResult);

            // Zapis wyników pojedynczego powtórzenia do pliku
            String individualFileName = String.format("results_matrix_%d_%s_run%d_%s.txt",
                    size, operation, run,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            try {
                writeResults(individualFileName, runResult.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Obliczanie statystyk: minimum, maksimum, średnia, mediana (wyniki w mikrosekundach)
        Collections.sort(times);
        long min = times.get(0);
        long max = times.get(times.size() - 1);
        double average = times.stream().mapToLong(Long::longValue).average().orElse(0);
        double median = computeMedian(times);

        aggregatedResults.append("\nStatystyki zbiorcze (mikrosekundy):\n");
        aggregatedResults.append("Min: ").append(min / 1000.0)
                .append(", Max: ").append(max / 1000.0)
                .append(", Średnia: ").append(average / 1000.0)
                .append(", Mediana: ").append(median / 1000.0).append("\n");

        // Zapis zbiorczy wyników do pliku
        String aggregatedFileName = String.format("results_matrix_%d_%s_all_%s.txt",
                size, operation,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        try {
            writeResults(aggregatedFileName, aggregatedResults.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    private double computeMedian(List<Long> times) {
        int size = times.size();
        if (size % 2 == 0) {
            return (times.get(size / 2 - 1) + times.get(size / 2)) / 2.0;
        } else {
            return times.get(size / 2);
        }
    }

    private void writeResults(String fileName, String content) throws IOException {
        Path filePath = Paths.get(OUTPUT_DIR, fileName);
        Files.writeString(filePath, content);
    }
}
