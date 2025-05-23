package Presentation;

import BusinessLogic.ClientBLL;
import Model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClientPanel extends JPanel {
    private final ClientBLL clientBLL = new ClientBLL();
    private final JTable clientTable;
    private final DefaultTableModel tableModel;

    public ClientPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        clientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clientTable);

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
                Class<Client> clazz = Client.class;
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

                int option = JOptionPane.showConfirmDialog(this, dialogFields, "Add Client", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    Client client = new Client();

                    for (int i = 0; i < getters.size(); i++) {
                        String fieldName = getterToFieldName(getters.get(i).getName());
                        Class<?> returnType = getters.get(i).getReturnType();
                        String inputValue = inputs.get(i).getText();

                        Method setter = getSetter(clazz, fieldName, returnType);
                        if (setter != null) {
                            Object value = parseValue(inputValue, returnType);
                            setter.invoke(client, value);
                        }
                    }

                    clientBLL.insertClient(client);
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input or error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editBtn.addActionListener(e -> {
            int row = clientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a client to edit.");
                return;
            }

            try {
                Class<Client> clazz = Client.class;

                int id = (int) clientTable.getValueAt(row, 0);
                Client client = clientBLL.findClientById(id);
                if (client == null) return;

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
                    Object val = getter.invoke(client);
                    inputs.add(new JTextField(val != null ? val.toString() : ""));
                }

                Object[] dialogFields = new Object[labels.size() * 2];
                for (int i = 0; i < labels.size(); i++) {
                    dialogFields[i * 2] = labels.get(i);
                    dialogFields[i * 2 + 1] = inputs.get(i);
                }

                int option = JOptionPane.showConfirmDialog(this, dialogFields, "Edit Client", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    for (int i = 0; i < getters.size(); i++) {
                        String fieldName = getterToFieldName(getters.get(i).getName());
                        Class<?> returnType = getters.get(i).getReturnType();
                        String inputValue = inputs.get(i).getText();

                        Method setter = getSetter(clazz, fieldName, returnType);
                        if (setter != null) {
                            Object value = parseValue(inputValue, returnType);
                            setter.invoke(client, value);
                        }
                    }

                    clientBLL.updateClient(client);
                    refreshTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input or error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = clientTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a client to delete.");
                return;
            }

            int id = (int) clientTable.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete client ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clientBLL.deleteClient(id);
                refreshTable();
            }
        });
    }


    private void refreshTable() {
        List<Client> clients = clientBLL.getAllClients();
        DefaultTableModel model = TableGen.buildTableModel(clients);
        clientTable.setModel(model);
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
