package org.magister.Matrix;


import java.lang.reflect.Method;

public class MatrixReflection {
    public static Object invokeOperation(Object matrix, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            Method method = matrix.getClass().getMethod(methodName, paramTypes);
            return method.invoke(matrix, args);
        } catch (Exception e) {
            throw new RuntimeException("Błąd wywołania refleksyjnego: " + e.getMessage());
        }
    }
}