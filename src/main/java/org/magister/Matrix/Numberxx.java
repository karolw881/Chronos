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

    abstract T add(@NonNull T a, @NonNull T b);
    abstract T subtract(@NonNull T a, @NonNull T b);
    abstract T multiply(@NonNull T a, @NonNull T b);
    abstract T divide(@NonNull T a, @NonNull T b);
    abstract T negate(@NonNull T a);
    abstract  T zero();
    abstract  boolean isZero(@NonNull T value);
}
