package org.magister.Vector;

// Implementacja konkretna dla liczb całkowitych
public class IntVector {
    private int[] coordinates;

    public IntVector(int[] coordinates) {
        this.coordinates = coordinates.clone();
    }

    public IntVector addVector(IntVector other) {
        if (this.coordinates.length != other.coordinates.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        int[] result = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = this.coordinates[i] + other.coordinates[i];
        }
        return new IntVector(result);
    }

    public IntVector opposite() {
        int[] result = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = -coordinates[i];
        }
        return new IntVector(result);
    }

    public IntVector subVector(IntVector other) {
        // Metoda 1: bezpośrednie odejmowanie
        return this.addVector(other.opposite());
    }

    public IntVector subVectorDirect(IntVector other) {
        // Metoda 2: bezpośrednie odejmowanie
        if (this.coordinates.length != other.coordinates.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        int[] result = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = this.coordinates[i] - other.coordinates[i];
        }
        return new IntVector(result);
    }

    public IntVector multiplyByScalar(int scalar) {
        int[] result = new int[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            result[i] = coordinates[i] * scalar;
        }
        return new IntVector(result);
    }

    public int dotProduct(IntVector other) {
        if (this.coordinates.length != other.coordinates.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }
        int sum = 0;
        for (int i = 0; i < coordinates.length; i++) {
            sum += this.coordinates[i] * other.coordinates[i];
        }
        return sum;
    }

    public double length() {
        int sum = 0;
        for (int coordinate : coordinates) {
            sum += coordinate * coordinate;
        }
        return Math.sqrt(sum);
    }
}
