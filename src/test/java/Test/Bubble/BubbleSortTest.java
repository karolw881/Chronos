package Test.Bubble;

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

public class BubbleSortTest {
    private static final String INPUT_DIR = "test_data/input/";
    private static final String OUTPUT_DIR = "test_data/output/";
    private static BubbleSort<Integer> objectSorter;
    private static ReflectiveBubbleSort reflectiveSorter;
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
        int[] sizes = {10, 100, 1000,10000};
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
        String inputFileName = "test_" + testCase + ".txt";
        String outputFileName = "results_" + testCase + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

        try {
            // Wczytywanie danych (poza pomiarem czasu)
            Integer[] data = loadTestData(inputFileName);
            Integer[] dataCopy = data.clone();

            // Przygotowanie pliku wynikowego
            StringBuilder results = new StringBuilder();
            results.append("Test case: ").append(testCase).append("\n");
            results.append("Array size: ").append(data.length).append("\n\n");

            // Test implementacji obiektowej
            results.append("Object implementation:\n");
            long startTime = System.nanoTime();
            Integer[] sortedObject = objectSorter.sort(data.clone());
            long endTime = System.nanoTime();
            long objectTime = endTime - startTime;

            // Test implementacji refleksyjnej
            results.append("Reflective implementation:\n");
            startTime = System.nanoTime();
            Object[] sortedReflective = reflectiveSorter.sort(dataCopy, "compareTo");
            endTime = System.nanoTime();
            long reflectiveTime = endTime - startTime;

            // Zapisywanie wyników
            results.append("Object implementation time: ").append(objectTime / 1000.0).append(" microseconds\n");
            results.append("Reflective implementation time: ").append(reflectiveTime / 1000.0).append(" microseconds\n");
            results.append("Time difference: ").append((reflectiveTime - objectTime) / 1000.0).append(" microseconds\n\n");

            // Statystyki sortowania
            results.append("Sorting statistics:\n");
            results.append("Initial array: ").append(Arrays.toString(data)).append("\n");
            results.append("Sorted array (object): ").append(Arrays.toString(sortedObject)).append("\n");
            results.append("Sorted array (reflective): ").append(Arrays.toString(sortedReflective)).append("\n");

            // Weryfikacja poprawności sortowania
            boolean isObjectSorted = isSorted(sortedObject);
            boolean isReflectiveSorted = isSorted(Arrays.stream(sortedReflective)
                    .map(o -> (Integer)o)
                    .toArray(Integer[]::new));

            results.append("\nValidation:\n");
            results.append("Object implementation sorted correctly: ").append(isObjectSorted).append("\n");
            results.append("Reflective implementation sorted correctly: ").append(isReflectiveSorted).append("\n");

            // Zapisywanie wyników do pliku
            writeResults(outputFileName, results.toString());

            // Asercje JUnit
            Assertions.assertTrue(isObjectSorted, "Object implementation failed to sort correctly");
            Assertions.assertTrue(isReflectiveSorted, "Reflective implementation failed to sort correctly");

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Test failed with exception: " + e.getMessage());
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
}
