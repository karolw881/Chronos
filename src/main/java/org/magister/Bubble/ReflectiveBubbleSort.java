package org.magister.Bubble;


// 2. Implementacja z użyciem refleksji
import java.lang.reflect.Method;

public class ReflectiveBubbleSort {
    public Object[] sort(Object[] arr, String compareMethodName) throws Exception {
        int n = arr.length;
        boolean swapped;

        // Pobieranie metody porównującej przez refleksję
        Method compareMethod = arr[0].getClass().getMethod(compareMethodName, arr[0].getClass());

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                // Wywołanie metody porównującej przez refleksję
                Integer result = (Integer) compareMethod.invoke(arr[j], arr[j + 1]);

                if (result > 0) {
                    // Zamiana miejscami
                    Object temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return arr;
    }
}