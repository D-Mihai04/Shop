package Presentation;

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TableGen {

    public static <T> DefaultTableModel buildTableModel(List<T> objects) {
        DefaultTableModel model = new DefaultTableModel();

        if (objects == null || objects.isEmpty()) return model;

        T first = objects.get(0);
        Class<?> clazz = first.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        List<Method> getters = new ArrayList<>();
        Method idGetter = null;


        for (Method method : methods) {
            if (method.getName().equals("getId") &&
                    method.getParameterCount() == 0)
            {
                idGetter = method;
            }
            else if (method.getName().startsWith("get") &&
                    method.getParameterCount() == 0 &&
                    !method.getName().equals("getClass"))
            {
                getters.add(method);
            }
        }

        if (idGetter != null) {
            model.addColumn(toHeader(idGetter.getName()));
        }

        for (Method getter : getters) {
            model.addColumn(toHeader(getter.getName()));
        }

        for (T obj : objects) {
            List<Object> row = new ArrayList<>();

            try {
                if (idGetter != null) {
                    row.add(idGetter.invoke(obj));
                }

                for (Method getter : getters) {
                    row.add(getter.invoke(obj));
                }
            } catch (Exception e) {
                row.add("");
            }

            model.addRow(row.toArray());
        }

        return model;
    }

    private static String toHeader(String getterName) {
        String base = getterName.substring(3);
        return base.replaceAll("([A-Z])", " $1").trim();
    }
}
