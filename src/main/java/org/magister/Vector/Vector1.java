package org.magister.Vector;

import java.util.Arrays;

public class Vector1 {
    private final int[] dane;

    public Vector1(int[] dane) {
        // Kopiujemy dane, aby zewnętrzna modyfikacja nie wpływała na wektor
        this.dane = dane.clone();
    }

    public int[] getDane() {
        return dane.clone();
    }

    // Dodawanie wektorów (suma współrzędnych)
    public Vector1 addVector(Vector1 other) {
        if (this.dane.length != other.dane.length) {
            throw new IllegalArgumentException("Wektory muszą mieć ten sam rozmiar.");
        }
        int[] result = new int[dane.length];
        for (int i = 0; i < dane.length; i++) {
            result[i] = this.dane[i] + other.dane[i];
        }
        return new Vector1(result);
    }

    // Wektor przeciwny (negacja)
    public Vector1 opposite() {
        int[] result = new int[dane.length];
        for (int i = 0; i < dane.length; i++) {
            result[i] = -this.dane[i];
        }
        return new Vector1(result);
    }

    // Odejmowanie wektorów (a - b = a + (-b))
    public Vector1 subVector(Vector1 other) {
        return this.addVector(other.opposite());
    }

    // Mnożenie wektora przez skalar
    public Vector1 multiply(int scalar) {
        int[] result = new int[dane.length];
        for (int i = 0; i < dane.length; i++) {
            result[i] = this.dane[i] * scalar;
        }
        return new Vector1(result);
    }

    // Iloczyn skalarny dwóch wektorów
    public int dot(Vector1 other) {
        if (this.dane.length != other.dane.length) {
            throw new IllegalArgumentException("Wektory muszą mieć ten sam rozmiar.");
        }
        int sum = 0;
        for (int i = 0; i < dane.length; i++) {
            sum += this.dane[i] * other.dane[i];
        }
        return sum;
    }

    // Długość wektora
    public double length() {
        int sum = 0;
        for (int x : dane) {
            sum += x * x;
        }
        return Math.sqrt(sum);
    }

    @Override
    public String toString() {
        return Arrays.toString(dane);
    }
}
