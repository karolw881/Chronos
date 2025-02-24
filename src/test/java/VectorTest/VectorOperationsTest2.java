package VectorTest;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.magister.Vector.Vector1;
import org.magister.Vector.Vector2;
import org.magister.Vector.VectorReflection;


import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VectorOperationsTest2 {
    private static final String INPUT_DIR = "test_data2/vector_input/";
    private static final String OUTPUT_DIR = "test_data2/vector_output/";
    private static final int RUNS = 10;

    @BeforeAll
    static void setUp() {
        createDirectories();
   
    }

    private static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(INPUT_DIR));
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    void testVectorOperations(int size) {
        // Mapy przechowujące czasy wykonania dla poszczególnych implementacji
        Map<String, List<Long>> executionTimes = new HashMap<>();
        executionTimes.put("vector1_standard", new ArrayList<>());
        executionTimes.put("vector1_reflection", new ArrayList<>());
        executionTimes.put("vector2_standard", new ArrayList<>());
        executionTimes.put("vector2_reflection", new ArrayList<>());

        StringBuilder aggregatedResults = new StringBuilder();
        aggregatedResults.append("Test case: size = ").append(size).append("\n");
        aggregatedResults.append("Number of runs: ").append(RUNS).append("\n\n");

        Random random = new Random(42);

        for (int run = 1; run <= RUNS; run++) {
            // Generowanie losowych danych dla wektorów
            int[] data1 = new int[size];
            int[] data2 = new int[size];
            for (int i = 0; i < size; i++) {
                data1[i] = random.nextInt(10);
                data2[i] = random.nextInt(10);
            }

            // Tworzymy obiekty Vector1 (konkretne) i Vector2 (generyczne)
            Vector1 v1 = new Vector1(data1);
            Vector1 v2 = new Vector1(data2);

            Integer[] genericData1 = convertToGeneric(data1);
            Integer[] genericData2 = convertToGeneric(data2);
            Vector2<Integer> gv1 = new Vector2<>(genericData1);
            Vector2<Integer> gv2 = new Vector2<>(genericData2);

            StringBuilder runResults = new StringBuilder();
            runResults.append("Run ").append(run).append("\n");

            // 1. Dodawanie wektorów – Vector1 (standard)
            long startTime = System.nanoTime();
            Vector1 sum1 = v1.addVector(v2);
            long standardTime1 = System.nanoTime() - startTime;
            executionTimes.get("vector1_standard").add(standardTime1);

            // 2. Dodawanie wektorów – Vector1 (refleksja)
            startTime = System.nanoTime();
            Object sum1Reflective = VectorReflection.invokeOperation(v1, "addVector", v2);
            long reflectiveTime1 = System.nanoTime() - startTime;
            executionTimes.get("vector1_reflection").add(reflectiveTime1);

            // 3. Dodawanie wektorów – Vector2 (standard)
            startTime = System.nanoTime();
            Vector2<Integer> sum2 = gv1.addVector(gv2);
            long standardTime2 = System.nanoTime() - startTime;
            executionTimes.get("vector2_standard").add(standardTime2);

            // 4. Dodawanie wektorów – Vector2 (refleksja)
            startTime = System.nanoTime();
            Object sum2Reflective = VectorReflection.invokeOperation(gv1, "addVector", gv2);
            long reflectiveTime2 = System.nanoTime() - startTime;
            executionTimes.get("vector2_reflection").add(reflectiveTime2);

            runResults.append(String.format("Vector1 add standard: %.3f ms%n", standardTime1 / 1_000_000.0));
            runResults.append(String.format("Vector1 add reflection: %.3f ms%n", reflectiveTime1 / 1_000_000.0));
            runResults.append(String.format("Vector2 add standard: %.3f ms%n", standardTime2 / 1_000_000.0));
            runResults.append(String.format("Vector2 add reflection: %.3f ms%n", reflectiveTime2 / 1_000_000.0));

            // Walidacja wyników – porównanie tablic danych
            boolean vector1Valid = Arrays.equals(sum1.getDane(), ((Vector1) sum1Reflective).getDane());
            boolean vector2Valid = Arrays.equals(sum2.getDane(), ((Vector2<Integer>) sum2Reflective).getDane());
            runResults.append("Validation results:\n");
            runResults.append("Vector1 implementations match: ").append(vector1Valid).append("\n");
            runResults.append("Vector2 implementations match: ").append(vector2Valid).append("\n\n");

            String runFileName = String.format("vector_size%d_run%d_%s.txt",
                    size, run,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            try {
                writeResults(runFileName, runResults.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            aggregatedResults.append(runResults);
        }

        // Agregacja statystyk
        aggregatedResults.append("\nAggregated Statistics:\n");
        for (String key : executionTimes.keySet()) {
            List<Long> times = executionTimes.get(key);
            Collections.sort(times);
            double avg = times.stream().mapToLong(Long::longValue).average().orElse(0);
            double median = computeMedian(times);
            long min = times.get(0);
            long max = times.get(times.size() - 1);

            aggregatedResults.append(String.format("%s:%n", key));
            aggregatedResults.append(String.format("Min: %.3f ms%n", min / 1_000_000.0));
            aggregatedResults.append(String.format("Max: %.3f ms%n", max / 1_000_000.0));
            aggregatedResults.append(String.format("Average: %.3f ms%n", avg / 1_000_000.0));
            aggregatedResults.append(String.format("Median: %.3f ms%n%n", median / 1_000_000.0));
        }

        String aggregatedFileName = String.format("vector_size%d_aggregated_%s.txt",
                size,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
        try {
            writeResults(aggregatedFileName, aggregatedResults.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Integer[] convertToGeneric(int[] array) {
        Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
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

