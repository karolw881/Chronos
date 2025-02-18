package Test.BubbleTest;



import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.magister.Bubble.BubbleSort;
import org.magister.Bubble.ReflectiveBubbleSort;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BubbleSortTest2 {
    private static final String INPUT_DIR = "test_data/input/";
    private static final String OUTPUT_DIR = "test_data/output/";
    private static BubbleSort<Integer> objectSorter;
    private static ReflectiveBubbleSort reflectiveSorter;
    // Liczba powtórzeń dla każdego przypadku
    private static final int RUNS = 10;

    @BeforeAll
    static void setUp() {
        objectSorter = new BubbleSort<>();
        reflectiveSorter = new ReflectiveBubbleSort();

        // Tworzenie katalogów jeśli nie istnieją
        createDirectories();

        // Generowanie plików testowych (tylko jeśli nie istnieją)
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
        int[] sizes = {10, 100, 1000, 10000};
        String[] types = {"random", "sorted", "reverse"};
        Random random = new Random(42); // stały seed dla powtarzalności

        for (int size : sizes) {
            for (String type : types) {
                String fileName = String.format("test_%d_%s.txt", size, type);
                Path filePath = Paths.get(INPUT_DIR, fileName);

                if (!Files.exists(filePath)) {
                    try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
                        Integer[] numbers = new Integer[size];

                        // Generowanie danych według typu
                        switch (type) {
                            case "random":
                                for (int i = 0; i < size; i++) {
                                    numbers[i] = random.nextInt(10000);
                                }
                                break;
                            case "sorted":
                                for (int i = 0; i < size; i++) {
                                    numbers[i] = i;
                                }
                                break;
                            case "reverse":
                                for (int i = 0; i < size; i++) {
                                    numbers[i] = size - i;
                                }
                                break;
                        }

                        // Zapisywanie do pliku
                        writer.println(Arrays.toString(numbers)
                                .replaceAll("[\\[\\]]", ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"10_random", "10_sorted", "10_reverse",
            "100_random", "100_sorted", "100_reverse",
            "1000_random", "1000_sorted", "1000_reverse"})
    void testBothImplementations(String testCase) {
        List<Long> objectTimes = new ArrayList<>();
        List<Long> reflectiveTimes = new ArrayList<>();
        StringBuilder aggregatedResults = new StringBuilder();
        aggregatedResults.append("Aggregated results for test case: ").append(testCase).append("\n");
        aggregatedResults.append("Number of runs: ").append(RUNS).append("\n\n");

        // Pętla wykonująca RUNS razy dany test
        for (int version = 1; version <= RUNS; version++) {
            String inputFileName = "test_" + testCase + ".txt";
            Integer[] data;
            try {
                data = loadTestData(inputFileName);
            } catch (IOException e) {
                e.printStackTrace();
                Assertions.fail("Nie udało się wczytać danych z pliku: " + inputFileName);
                return;
            }
            Integer[] dataCopy = data.clone();

            StringBuilder versionResult = new StringBuilder();
            versionResult.append("Test case: ").append(testCase)
                    .append("_version").append(version).append("\n");
            versionResult.append("Array size: ").append(data.length).append("\n\n");

            // Test implementacji obiektowej
            long startTime = System.nanoTime();
            Integer[] sortedObject = objectSorter.sort(data.clone());
            long endTime = System.nanoTime();
            long objectTime = endTime - startTime;

            // Test implementacji refleksyjnej
            startTime = System.nanoTime();
            Object[] sortedReflective = null;
            try {
                sortedReflective = reflectiveSorter.sort(dataCopy, "compareTo");
            } catch (Exception e) {
                e.printStackTrace();
                Assertions.fail("Wyjątek w implementacji refleksyjnej w wersji " + version + ": " + e.getMessage());
            }
            endTime = System.nanoTime();
            long reflectiveTime = endTime - startTime;

            objectTimes.add(objectTime);
            reflectiveTimes.add(reflectiveTime);

            versionResult.append("Object implementation time: ")
                    .append(objectTime / 1000.0).append(" microseconds\n");
            versionResult.append("Reflective implementation time: ")
                    .append(reflectiveTime / 1000.0).append(" microseconds\n");
            versionResult.append("Time difference: ")
                    .append((reflectiveTime - objectTime) / 1000.0).append(" microseconds\n\n");

            // Statystyki sortowania
            versionResult.append("Sorting statistics:\n");
            versionResult.append("Initial array: ").append(Arrays.toString(data)).append("\n");
            versionResult.append("Sorted array (object): ").append(Arrays.toString(sortedObject)).append("\n");
            versionResult.append("Sorted array (reflective): ").append(Arrays.toString(sortedReflective)).append("\n");

            boolean isObjectSorted = isSorted(sortedObject);
            boolean isReflectiveSorted = isSorted(Arrays.stream(sortedReflective)
                    .map(o -> (Integer) o)
                    .toArray(Integer[]::new));

            versionResult.append("\nValidation:\n");
            versionResult.append("Object implementation sorted correctly: ")
                    .append(isObjectSorted).append("\n");
            versionResult.append("Reflective implementation sorted correctly: ")
                    .append(isReflectiveSorted).append("\n");

            // Zapis wyników dla danej wersji do osobnego pliku
            String individualFileName = "results_" + testCase + "_version" + version + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            try {
                writeResults(individualFileName, versionResult.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Podsumowanie wyników dla wersji
            aggregatedResults.append("Version ").append(version).append(" - ");
            aggregatedResults.append("Object: ").append(objectTime / 1000.0).append(" microseconds, ");
            aggregatedResults.append("Reflective: ").append(reflectiveTime / 1000.0).append(" microseconds\n");

            // Walidacja w każdej iteracji
            Assertions.assertTrue(isObjectSorted,
                    "Object implementation failed to sort correctly in version " + version);
            Assertions.assertTrue(isReflectiveSorted,
                    "Reflective implementation failed to sort correctly in version " + version);
        }

        // Obliczanie statystyk
        Collections.sort(objectTimes);
        Collections.sort(reflectiveTimes);

        long objectMin = objectTimes.get(0);
        long objectMax = objectTimes.get(objectTimes.size() - 1);
        double objectAverage = objectTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double objectMedian = computeMedian(objectTimes);

        long reflectiveMin = reflectiveTimes.get(0);
        long reflectiveMax = reflectiveTimes.get(reflectiveTimes.size() - 1);
        double reflectiveAverage = reflectiveTimes.stream().mapToLong(Long::longValue).average().orElse(0);
        double reflectiveMedian = computeMedian(reflectiveTimes);

        aggregatedResults.append("\nAggregated Statistics (times in microseconds):\n");
        aggregatedResults.append("Object Implementation:\n");
        aggregatedResults.append("Min: ").append(objectMin / 1000.0)
                .append(", Max: ").append(objectMax / 1000.0)
                .append(", Average: ").append(objectAverage / 1000.0)
                .append(", Median: ").append(objectMedian / 1000.0).append("\n");

        aggregatedResults.append("Reflective Implementation:\n");
        aggregatedResults.append("Min: ").append(reflectiveMin / 1000.0)
                .append(", Max: ").append(reflectiveMax / 1000.0)
                .append(", Average: ").append(reflectiveAverage / 1000.0)
                .append(", Median: ").append(reflectiveMedian / 1000.0).append("\n");

        // Zapis wyników zbiorczych do pliku
        String aggregatedFileName = "results_" + testCase + "_all_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        try {
            writeResults(aggregatedFileName, aggregatedResults.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer[] loadTestData(String fileName) throws IOException {
        Path filePath = Paths.get(INPUT_DIR, fileName);
        String content = Files.readString(filePath);
        return Arrays.stream(content.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toArray(Integer[]::new);
    }

    private void writeResults(String fileName, String content) throws IOException {
        Path filePath = Paths.get(OUTPUT_DIR, fileName);
        Files.writeString(filePath, content);
    }

    private boolean isSorted(Integer[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private double computeMedian(List<Long> times) {
        int size = times.size();
        if (size % 2 == 0) {
            return (times.get(size / 2 - 1) + times.get(size / 2)) / 2.0;
        } else {
            return times.get(size / 2);
        }
    }
}
