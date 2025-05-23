package Presentation;

import BusinessLogic.BillBLL;
import Model.Bill;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
*BillPanel is the interface for bills
 */
public class BillPanel extends JPanel {
    private final BillBLL billBLL = new BillBLL();
    private final JTable billTable;
    private final DefaultTableModel tableModel;


    public BillPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        billTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(billTable);

        JButton refreshBtn = new JButton("Refresh");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();

        refreshBtn.addActionListener(e -> refreshTable());
    }


    private void refreshTable() {
        List<Bill> bills = billBLL.getAllBills();

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        if (bills == null || bills.isEmpty()) return;

        tableModel.addColumn("Order ID");
        tableModel.addColumn("Client ID");
        tableModel.addColumn("Client Name");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Total Price");
        tableModel.addColumn("Date");

        for (Bill b : bills) {
            tableModel.addRow(new Object[]{
                    b.orderId(),
                    b.clientId(),
                    b.clientName(),
                    b.productName(),
                    b.quantity(),
                    b.totalPrice(),
                    b.orderDate()
            });
        }
    }
}
