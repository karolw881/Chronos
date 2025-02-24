package MatrixTest;



public interface Numberxx<T> {
    T add(T a, T b);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T zero();
    boolean isZero(T a);
    T negate(T a);
}
