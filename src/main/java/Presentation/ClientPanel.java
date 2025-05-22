package Presentation;

import BusinessLogic.ClientBLL;
import Model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
            JTextField firstName = new JTextField();
            JTextField lastName = new JTextField();
            JTextField email = new JTextField();
            JTextField age = new JTextField();

            Object[] fields = {
                    "First Name:", firstName,
                    "Last Name:", lastName,
                    "Email:", email,
                    "Age:", age
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Add Client", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Client client = new Client(0, firstName.getText(), lastName.getText(), email.getText(), Integer.parseInt(age.getText()));
                    clientBLL.insertClient(client);
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editBtn.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a client to edit.");
                return;
            }

            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String first = (String) tableModel.getValueAt(selectedRow, 1);
            String last = (String) tableModel.getValueAt(selectedRow, 2);
            String email = (String) tableModel.getValueAt(selectedRow, 3);
            int age = (int) tableModel.getValueAt(selectedRow, 4);

            JTextField firstName = new JTextField(first);
            JTextField lastName = new JTextField(last);
            JTextField emailField = new JTextField(email);
            JTextField ageField = new JTextField(String.valueOf(age));

            Object[] fields = {
                    "First Name:", firstName,
                    "Last Name:", lastName,
                    "Email:", emailField,
                    "Age:", ageField
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Edit Client", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Client client = new Client(id, firstName.getText(), lastName.getText(), emailField.getText(),Integer.parseInt(ageField.getText()));
                    clientBLL.updateClient(client);
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = clientTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a client to delete.");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete client ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clientBLL.deleteClient(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        List<Client> clients = clientBLL.getAllClients();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (clients == null || clients.isEmpty()) return;

        tableModel.addColumn("ID");
        tableModel.addColumn("First Name");
        tableModel.addColumn("Last Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Age");

        for (Client c : clients) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getFirst_name(), c.getLast_name(), c.getEmail(), c.getAge()
            });
        }
    }
}
