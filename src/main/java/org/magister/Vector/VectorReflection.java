package org.magister.Vector;



import java.lang.reflect.Method;

public class VectorReflection {
    public static Object invokeOperation(Object instance, String methodName, Object... args) {
        try {
            Class<?> clazz = instance.getClass();
            // Wyznaczamy typy parametrów, uwzględniając typy prymitywne
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Integer) {
                    parameterTypes[i] = int.class;
                } else if (args[i] instanceof Double) {
                    parameterTypes[i] = double.class;
                } else if (args[i] instanceof Float) {
                    parameterTypes[i] = float.class;
                } else if (args[i] instanceof Long) {
                    parameterTypes[i] = long.class;
                } else {
                    parameterTypes[i] = args[i].getClass();
                }
            }
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas wywołania metody " + methodName, e);
        }
    }
}
