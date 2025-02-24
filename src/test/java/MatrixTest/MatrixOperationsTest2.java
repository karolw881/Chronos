package MatrixTest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.magister.Matrix.Macierz1;
import org.magister.Matrix.Macierz2;
import org.magister.Matrix.MatrixReflection;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MatrixOperationsTest2 {
    private static final String INPUT_DIR = "test_data2/matrix_input/";
    private static final String OUTPUT_DIR = "test_data2/matrix_output/";
    private static final int RUNS = 10;

    private static Macierz1 concreteMatrix;
    private static Macierz2<Integer> genericMatrix;

    @BeforeAll
    static void setUp() {
        // Tworzenie katalogów jeśli nie istnieją
        createDirectories();
        // Generowanie plików testowych
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

    private static void generateTestFiles() {
        int[] sizes = {10, 50, 100, 200};
        String[] types = {"random", "identity", "diagonal"};
        Random random = new Random(42); // stały seed dla powtarzalności

        for (int size : sizes) {
            for (String type : types) {
                String fileName = String.format("matrix_%d_%s.txt", size, type);
                Path filePath = Paths.get(INPUT_DIR, fileName);

                if (!Files.exists(filePath)) {
                    try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
                        int[][] matrix = new int[size][size];

                        switch (type) {
                            case "random":
                                for (int i = 0; i < size; i++) {
                                    for (int j = 0; j < size; j++) {
                                        matrix[i][j] = random.nextInt(10);
                                    }
                                }
                                break;
                            case "identity":
                                for (int i = 0; i < size; i++) {
                                    matrix[i][i] = 1;
                                }
                                break;
                            case "diagonal":
                                for (int i = 0; i < size; i++) {
                                    matrix[i][i] = random.nextInt(10) + 1;
                                }
                                break;
                        }

                        // Zapisywanie macierzy do pliku
                        for (int i = 0; i < size; i++) {
                            writer.println(Arrays.toString(matrix[i])
                                    .replaceAll("[\\[\\]]", ""));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "10_random", "10_identity", "10_diagonal",
            "50_random", "50_identity", "50_diagonal",
            "100_random", "100_identity", "100_diagonal"
    })
    void testMatrixOperations(String testCase) {
        // Struktury do przechowywania czasów wykonania
        Map<String, List<Long>> executionTimes = new HashMap<>();
        executionTimes.put("concrete_standard", new ArrayList<>());
        executionTimes.put("concrete_reflection", new ArrayList<>());
        executionTimes.put("generic_standard", new ArrayList<>());
        executionTimes.put("generic_reflection", new ArrayList<>());

        StringBuilder aggregatedResults = new StringBuilder();
        aggregatedResults.append("Test case: ").append(testCase).append("\n");
        aggregatedResults.append("Number of runs: ").append(RUNS).append("\n\n");

        for (int run = 1; run <= RUNS; run++) {
            try {
                // Wczytywanie danych testowych
                int[][] data1 = loadTestData(testCase);
                int[][] data2 = loadTestData(testCase);

                // Przygotowanie macierzy
                Macierz1 m1 = new Macierz1(data1);
                Macierz1 m2 = new Macierz1(data2);

                Integer[][] genericData1 = convertToGeneric(data1);
                Integer[][] genericData2 = convertToGeneric(data2);
                Macierz2<Integer> gm1 = new Macierz2<>(genericData1);
                Macierz2<Integer> gm2 = new Macierz2<>(genericData2);
                // Test wszystkich czterech przypadków
                StringBuilder runResults = new StringBuilder();
                runResults.append("Run ").append(run).append("\n");

                // 1. Concrete standard
                long startTime = System.nanoTime();
                Macierz1 resultConcrete = m1.multiply(m2);
                long concreteTime = System.nanoTime() - startTime;
                executionTimes.get("concrete_standard").add(concreteTime);

                // 2. Concrete reflection
                startTime = System.nanoTime();
                Object resultConcreteReflective = MatrixReflection.invokeOperation(m1, "multiply", m2);
                long concreteReflectiveTime = System.nanoTime() - startTime;
                executionTimes.get("concrete_reflection").add(concreteReflectiveTime);

                // 3. Generic standard
                startTime = System.nanoTime();
                Macierz2<Integer> resultGeneric = gm1.multiply(gm2);
                long genericTime = System.nanoTime() - startTime;
                executionTimes.get("generic_standard").add(genericTime);

                // 4. Generic reflection
                startTime = System.nanoTime();
                Object resultGenericReflective = MatrixReflection.invokeOperation(gm1, "multiply", gm2);
                long genericReflectiveTime = System.nanoTime() - startTime;
                executionTimes.get("generic_reflection").add(genericReflectiveTime);

                // Zapisywanie wyników dla pojedynczego przebiegu
                runResults.append(formatExecutionResults(concreteTime, concreteReflectiveTime,
                        genericTime, genericReflectiveTime));

                // Walidacja wyników
                boolean concreteValid = validateResults(resultConcrete.getDane(),
                        ((Macierz1)resultConcreteReflective).getDane());
                boolean genericValid = validateGenericResults(resultGeneric.getDane(),
                        ((Macierz2<Integer>)resultGenericReflective).getDane());

                runResults.append("\nValidation results:\n");
                runResults.append("Concrete implementations match: ").append(concreteValid).append("\n");
                runResults.append("Generic implementations match: ").append(genericValid).append("\n");

                // Zapis wyników pojedynczego przebiegu
                String runFileName = String.format("matrix_%s_run%d_%s.txt",
                        testCase, run,
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
                writeResults(runFileName, runResults.toString());

                // Dodawanie do zbiorczych wyników
                aggregatedResults.append(runResults);

            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Error in run " + run + ": " + e.getMessage());
            }
        }

        // Obliczanie i dodawanie statystyk
        aggregatedResults.append("\nAggregated Statistics:\n");
        for (String implementation : executionTimes.keySet()) {
            List<Long> times = executionTimes.get(implementation);
            Collections.sort(times);

            double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
            double median = computeMedian(times);
            long min = times.get(0);
            long max = times.get(times.size() - 1);

            aggregatedResults.append(String.format("%s:%n", implementation));
            aggregatedResults.append(String.format("Min: %.3f ms%n", min / 1_000_000.0));
            aggregatedResults.append(String.format("Max: %.3f ms%n", max / 1_000_000.0));
            aggregatedResults.append(String.format("Average: %.3f ms%n", avg / 1_000_000.0));
            aggregatedResults.append(String.format("Median: %.3f ms%n%n", median / 1_000_000.0));
        }

        // Zapis zbiorczych wyników
        try {
            String aggregatedFileName = String.format("matrix_%s_aggregated_%s.txt",
                    testCase,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            writeResults(aggregatedFileName, aggregatedResults.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[][] loadTestData(String testCase) throws IOException {
        String fileName = "matrix_" + testCase + ".txt";
        Path filePath = Paths.get(INPUT_DIR, fileName);
        List<String> lines = Files.readAllLines(filePath);

        int size = lines.size();
        int[][] matrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            String[] values = lines.get(i).split(",");
            for (int j = 0; j < size; j++) {
                matrix[i][j] = Integer.parseInt(values[j].trim());
            }
        }

        return matrix;
    }

    private static Integer[][] convertToGeneric(int[][] matrix) {
        Integer[][] result = new Integer[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                result[i][j] = matrix[i][j];
            }
        }
        return result;
    }

    private static String formatExecutionResults(long concreteTime, long concreteReflectiveTime,
                                                 long genericTime, long genericReflectiveTime) {
        return String.format("""
            Execution times:
            Concrete standard: %.3f ms
            Concrete reflection: %.3f ms
            Generic standard: %.3f ms
            Generic reflection: %.3f ms
            
            """,
                concreteTime / 1_000_000.0,
                concreteReflectiveTime / 1_000_000.0,
                genericTime / 1_000_000.0,
                genericReflectiveTime / 1_000_000.0);
    }

    private static boolean validateResults(int[][] result1, int[][] result2) {
        if (result1.length != result2.length || result1[0].length != result2[0].length) {
            return false;
        }

        for (int i = 0; i < result1.length; i++) {
            if (!Arrays.equals(result1[i], result2[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateGenericResults(Integer[][] result1, Integer[][] result2) {
        if (result1.length != result2.length || result1[0].length != result2[0].length) {
            return false;
        }

        for (int i = 0; i < result1.length; i++) {
            if (!Arrays.equals(result1[i], result2[i])) {
                return false;
            }
        }
        return true;
    }

    private static double computeMedian(List<Long> times) {
        int size = times.size();
        if (size % 2 == 0) {
            return (times.get(size / 2 - 1) + times.get(size / 2)) / 2.0;
        } else {
            return times.get(size / 2);
        }
    }

    private static void writeResults(String fileName, String content) throws IOException {
        Path filePath = Paths.get(OUTPUT_DIR, fileName);
        Files.writeString(filePath, content);
    }
}