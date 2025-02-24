package org.magister.Matrix;

import lombok.NonNull;

/**
 * Numberxx potrzebny zeby byc klasa pomocna dla opcji Z liczbami w matrixam
 * poniewaz wykorzystujemy typy generyczne tak
 * @param <T>
 */


/**
 * abstract dla jaj dodałem bo błedy były
 * jest jedzcze opcja  jakims return 0
 * @param <T>
 */

public abstract class Numberxx<T> {

    protected abstract T add(@NonNull T a, @NonNull T b);

    public abstract Integer add(Integer a, Integer b);

    public abstract T subtract(@NonNull T a, @NonNull T b);

    public abstract Integer subtract(Integer a, Integer b);

    protected abstract T multiply(@NonNull T a, @NonNull T b);

    public abstract Integer multiply(Integer a, Integer b);

    abstract T divide(@NonNull T a, @NonNull T b);

    public abstract boolean isZero(Integer a);

    abstract T negate(@NonNull T a);


    public abstract Integer divide(Integer a, Integer b);

    public abstract Integer zero();
    abstract  boolean isZero(@NonNull T value);


    public abstract Integer negate(Integer a);
}
