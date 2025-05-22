package Presentation;

import Model.Product;

import BusinessLogic.ProductBLL;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

/**
 *ProductPanel is the interface for the products
 *
 */
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
            JTextField name = new JTextField();
            JTextField price = new JTextField();
            JTextField quantity = new JTextField();

            Object[] fields = {
                    "Name:", name,
                    "Price:", price,
                    "Quantity:", quantity
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Add Product", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Product product = new Product(0, name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(quantity.getText()));
                    productBLL.insertProduct(product);
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) return;

            int id = (int) tableModel.getValueAt(row, 0);
            String nameVal = (String) tableModel.getValueAt(row, 1);
            double priceVal = (double) tableModel.getValueAt(row, 2);
            int qtyVal = (int) tableModel.getValueAt(row, 3);

            JTextField name = new JTextField(nameVal);
            JTextField price = new JTextField(String.valueOf(priceVal));
            JTextField quantity = new JTextField(String.valueOf(qtyVal));

            Object[] fields = {
                    "Name:", name,
                    "Price:", price,
                    "Quantity:", quantity
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Product product = new Product(id, name.getText(), Double.parseDouble(price.getText()), Integer.parseInt(quantity.getText()));
                    productBLL.updateProduct(product);
                    refreshTable();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row == -1) return;

            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete product ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                productBLL.deleteProduct(id);
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        List<Product> products = productBLL.getAllProducts();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (products == null || products.isEmpty()) return;

        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Quantity");

        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getQuantity()
            });
        }
    }
}

