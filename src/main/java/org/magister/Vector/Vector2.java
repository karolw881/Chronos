package org.magister.Vector;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Vector2<T extends Number> {
    private final T[] dane;

    public Vector2(T[] dane) {
        // Możesz tu zrobić kopię tablicy, jeśli wymagana jest niemodyfikowalność
        this.dane = dane;
    }

    public T[] getDane() {
        return dane;
    }

    // Dodawanie wektorów (suma współrzędnych)
    public Vector2<T> addVector(Vector2<T> other) {
        if (this.dane.length != other.dane.length) {
            throw new IllegalArgumentException("Wektory muszą mieć ten sam rozmiar.");
        }
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(dane.getClass().getComponentType(), dane.length);
        for (int i = 0; i < dane.length; i++) {
            double sum = this.dane[i].doubleValue() + other.dane[i].doubleValue();
            result[i] = convert(sum);
        }
        return new Vector2<>(result);
    }

    // Wektor przeciwny (negacja)
    public Vector2<T> opposite() {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(dane.getClass().getComponentType(), dane.length);
        for (int i = 0; i < dane.length; i++) {
            result[i] = convert(-dane[i].doubleValue());
        }
        return new Vector2<>(result);
    }

    // Odejmowanie wektorów (a - b = a + (-b))
    public Vector2<T> subVector(Vector2<T> other) {
        return this.addVector(other.opposite());
    }

    // Mnożenie wektora przez skalar
    public Vector2<T> multiply(T scalar) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(dane.getClass().getComponentType(), dane.length);
        for (int i = 0; i < dane.length; i++) {
            result[i] = convert(dane[i].doubleValue() * scalar.doubleValue());
        }
        return new Vector2<>(result);
    }

    // Iloczyn skalarny dwóch wektorów
    public double dot(Vector2<T> other) {
        if (this.dane.length != other.dane.length) {
            throw new IllegalArgumentException("Wektory muszą mieć ten sam rozmiar.");
        }
        double sum = 0.0;
        for (int i = 0; i < dane.length; i++) {
            sum += this.dane[i].doubleValue() * other.dane[i].doubleValue();
        }
        return sum;
    }

    // Długość wektora
    public double length() {
        double sum = 0.0;
        for (T x : dane) {
            sum += x.doubleValue() * x.doubleValue();
        }
        return Math.sqrt(sum);
    }

    // Prywatna metoda konwersji wartości double na typ T
    @SuppressWarnings("unchecked")
    private T convert(double value) {
        Class<?> type = dane.getClass().getComponentType();
        if (type == Integer.class) {
            return (T) Integer.valueOf((int) value);
        } else if (type == Double.class) {
            return (T) Double.valueOf(value);
        } else if (type == Float.class) {
            return (T) Float.valueOf((float) value);
        } else if (type == Long.class) {
            return (T) Long.valueOf((long) value);
        } else {
            throw new UnsupportedOperationException("Typ " + type + " nie jest obsługiwany.");
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(dane);
    }
}
