package Presentation;

import Model.Product;
import BusinessLogic.ProductBLL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProductPanel extends JPanel {
    private final ProductBLL productBLL = new ProductBLL();
    private final JTable productTable;
    private final DefaultTableModel tableModel;

    public ProductPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();

        refreshBtn.addActionListener(e -> refreshTable());

        addBtn.addActionListener(e -> {
            try {
                Class<Product> clazz = Product.class;
                Method[] methods = clazz.getDeclaredMethods();

                List<Method> getters = new ArrayList<>();
                for (Method m : methods) {
                    if (m.getName().startsWith("get") &&
                            m.getParameterCount() == 0 &&
                            !m.getName().equals("getClass") &&
                            !m.getName().equals("getId")) {
                        getters.add(m);
                    }
                }

                List<JTextField> inputs = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                for (Method getter : getters) {
                    labels.add(getterToFieldName(getter.getName()) + ":");
                    inputs.add(new JTextField());
                }

                Object[] dialogFields = new Object[labels.size() * 2];
                for (int i = 0; i < labels.size(); i++) {
                    dialogFields[i * 2] = labels.get(i);
                    dialogFields[i * 2 + 1] = inputs.get(i);
                }

                int option = JOptionPane.showConfirmDialog(this, dialogFields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    Product product = new Product();

                    for (int i = 0; i < getters.size(); i++) {
                        String fieldName = getterToFieldName(getters.get(i).getName());
                        Class<?> returnType = getters.get(i).getReturnType();
                        String inputValue = inputs.get(i).getText();

                        Method setter = getSetter(clazz, fieldName, returnType);
                        if (setter != null) {
                            Object value = parseValue(inputValue, returnType);
                            setter.invoke(product, value);
                        }
                    }

                    productBLL.insertProduct(product);
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input or error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) return;

            try {
                Class<Product> cl = Product.class;

                int id = (int) productTable.getValueAt(row, 0);
                Product product = productBLL.findProductById(id);
                if (product == null) return;

                Method[] methods = cl.getDeclaredMethods();

                List<Method> getters = new ArrayList<>();
                for (Method m : methods) {
                    if (m.getName().startsWith("get") &&
                            m.getParameterCount() == 0 &&
                            !m.getName().equals("getClass") &&
                            !m.getName().equals("getId")) {
                        getters.add(m);
                    }
                }

                List<JTextField> inputs = new ArrayList<>();
                List<String> labels = new ArrayList<>();

                for (Method getter : getters) {
                    labels.add(getterToFieldName(getter.getName()) + ":");
                    Object val = getter.invoke(product);
                    inputs.add(new JTextField(val != null ? val.toString() : ""));
                }

                Object[] dialogFields = new Object[labels.size() * 2];
                for (int i = 0; i < labels.size(); i++) {
                    dialogFields[i * 2] = labels.get(i);
                    dialogFields[i * 2 + 1] = inputs.get(i);
                }

                int option = JOptionPane.showConfirmDialog(this, dialogFields, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    for (int i = 0; i < getters.size(); i++) {
                        String fieldName = getterToFieldName(getters.get(i).getName());
                        Class<?> returnType = getters.get(i).getReturnType();
                        String inputValue = inputs.get(i).getText();

                        Method setter = getSetter(cl, fieldName, returnType);
                        if (setter != null) {
                            Object value = parseValue(inputValue, returnType);
                            setter.invoke(product, value);
                        }
                    }

                    productBLL.updateProduct(product);
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input or error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) return;

            int id = (int) productTable.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                productBLL.deleteProduct(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        List<Product> products = productBLL.getAllProducts();
        DefaultTableModel model = TableGen.buildTableModel(products);
        productTable.setModel(model);
    }

    private static String getterToFieldName(String getterName) {
        return getterName.substring(3);
    }

    private static Method getSetter(Class<?> cl, String fieldName, Class<?> paramType) {
        try {
            return cl.getMethod("set" + fieldName, paramType);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static Object parseValue(String input, Class<?> type) {
        if (type == String.class) return input;
        else if (type == int.class || type == Integer.class) return Integer.parseInt(input);
        else if (type == double.class || type == Double.class) return Double.parseDouble(input);
        else if (type == long.class || type == Long.class) return Long.parseLong(input);
        else if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(input);
        return null;
    }
}
