package VectorTest;


// Klasa testowa dla klasy Vector


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.magister.Vector.IntVector;

import java.lang.reflect.Field;

public class VectorIntOperationsTest {

    /**
     * Metoda pomocnicza wykorzystująca refleksję do pobrania prywatnego pola "coordinates"
     * z obiektu IntVector.
     */
    private int[] getCoordinates(IntVector vector) throws Exception {
        Field field = IntVector.class.getDeclaredField("coordinates");
        field.setAccessible(true);
        return (int[]) field.get(vector);
    }

    /**
     * Test dodawania dwóch wektorów.
     */
    @Test
    public void testAddVector() throws Exception {
        IntVector v1 = new IntVector(new int[]{1, 2, 3});
        IntVector v2 = new IntVector(new int[]{4, 5, 6});
        IntVector result = v1.addVector(v2);
        int[] expected = {5, 7, 9};
        assertArrayEquals(expected, getCoordinates(result),
                "Wynik dodawania wektorów nie jest zgodny z oczekiwaniami");
    }

    /**
     * Test zwracania wektora przeciwnika.
     */
    @Test
    public void testOpposite() throws Exception {
        IntVector v = new IntVector(new int[]{1, -2, 3});
        IntVector opposite = v.opposite();
        int[] expected = {-1, 2, -3};
        assertArrayEquals(expected, getCoordinates(opposite),
                "Wektor przeciwny nie został poprawnie wyliczony");
    }

    /**
     * Test odejmowania wektorów przy użyciu metody subVector (dodanie wektora przeciwnika).
     */
    @Test
    public void testSubVector() throws Exception {
        IntVector v1 = new IntVector(new int[]{10, 20, 30});
        IntVector v2 = new IntVector(new int[]{1, 2, 3});
        IntVector result = v1.subVector(v2);
        int[] expected = {9, 18, 27};
        assertArrayEquals(expected, getCoordinates(result),
                "Wynik odejmowania wektorów (subVector) nie jest zgodny z oczekiwaniami");
    }

    /**
     * Test odejmowania wektorów przy użyciu metody subVectorDirect.
     */
    @Test
    public void testSubVectorDirect() throws Exception {
        IntVector v1 = new IntVector(new int[]{10, 20, 30});
        IntVector v2 = new IntVector(new int[]{1, 2, 3});
        IntVector result = v1.subVectorDirect(v2);
        int[] expected = {9, 18, 27};
        assertArrayEquals(expected, getCoordinates(result),
                "Wynik odejmowania wektorów (subVectorDirect) nie jest zgodny z oczekiwaniami");
    }

    /**
     * Test mnożenia wektora przez skalar.
     */
    @Test
    public void testMultiplyByScalar() throws Exception {
        IntVector v = new IntVector(new int[]{2, 3, 4});
        int scalar = 3;
        IntVector result = v.multiplyByScalar(scalar);
        int[] expected = {6, 9, 12};
        assertArrayEquals(expected, getCoordinates(result),
                "Mnożenie wektora przez skalar nie zostało poprawnie wyliczone");
    }

    /**
     * Test iloczynu skalarnego dwóch wektorów.
     */
    @Test
    public void testDotProduct() {
        IntVector v1 = new IntVector(new int[]{1, 2, 3});
        IntVector v2 = new IntVector(new int[]{4, 5, 6});
        int expected = 1 * 4 + 2 * 5 + 3 * 6; // 32
        int result = v1.dotProduct(v2);
        assertEquals(expected, result,
                "Iloczyn skalarny wektorów nie został poprawnie obliczony");
    }

    /**
     * Test obliczania długości wektora.
     */
    @Test
    public void testLength() {
        IntVector v = new IntVector(new int[]{3, 4});
        double expected = 5.0; // sqrt(3^2 + 4^2) = 5
        assertEquals(expected, v.length(), 0.0001,
                "Długość wektora została obliczona nieprawidłowo");
    }

    /**
     * Test sprawdzający, czy próba dodania wektorów o różnych wymiarach zgłasza wyjątek.
     */
    @Test
    public void testAddVectorDimensionMismatch() {
        IntVector v1 = new IntVector(new int[]{1, 2});
        IntVector v2 = new IntVector(new int[]{3, 4, 5});
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            v1.addVector(v2);
        });
        String expectedMessage = "Vectors must have same dimension";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "Wyjątek powinien zgłaszać komunikat o niezgodności wymiarów przy dodawaniu");
    }

    /**
     * Test sprawdzający, czy próba obliczenia iloczynu skalarnego wektorów o różnych wymiarach zgłasza wyjątek.
     */
    @Test
    public void testDotProductDimensionMismatch() {
        IntVector v1 = new IntVector(new int[]{1, 2});
        IntVector v2 = new IntVector(new int[]{3, 4, 5});
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            v1.dotProduct(v2);
        });
        String expectedMessage = "Vectors must have same dimension";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "Wyjątek powinien zgłaszać komunikat o niezgodności wymiarów przy iloczynie skalarnym");
    }

    /**
     * Test sprawdzający, czy próba odejmowania wektorów o różnych wymiarach zgłasza wyjątek.
     */
    @Test
    public void testSubVectorDimensionMismatch() {
        IntVector v1 = new IntVector(new int[]{1, 2});
        IntVector v2 = new IntVector(new int[]{3, 4, 5});
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            v1.subVector(v2);
        });
        String expectedMessage = "Vectors must have same dimension";
        assertTrue(exception.getMessage().contains(expectedMessage),
                "Wyjątek powinien zgłaszać komunikat o niezgodności wymiarów przy odejmowaniu");
    }
}
