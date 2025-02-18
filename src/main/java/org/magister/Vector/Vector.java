package org.magister.Vector;

import java.lang.reflect.Method;

// Implementacja generyczna
public class Vector<T extends Number> {
    private T[] coordinates;

    @SuppressWarnings("unchecked")
    public Vector(T[] coordinates) {
        this.coordinates = coordinates.clone();
    }

    // Implementacja z użyciem refleksji dla operacji matematycznych
    public Vector<T> addVector(Vector<T> other) throws Exception {
        if (this.coordinates.length != other.coordinates.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }

        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Number[coordinates.length];
        Method addMethod = coordinates[0].getClass().getMethod("sum", coordinates[0].getClass());

        for (int i = 0; i < coordinates.length; i++) {
            result[i] = (T) addMethod.invoke(coordinates[i], other.coordinates[i]);
        }

        return new Vector<>(result);
    }

    public Vector<T> opposite() throws Exception {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Number[coordinates.length];
        Method negateMethod = coordinates[0].getClass().getMethod("negate");

        for (int i = 0; i < coordinates.length; i++) {
            result[i] = (T) negateMethod.invoke(coordinates[i]);
        }

        return new Vector<>(result);
    }

    // Pozostałe metody implementowane analogicznie z użyciem refleksji...
}
