package Presentation;



import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

/**
 *OrderPanel is the interface for the orders
 *
 */
public class OrderPanel extends JPanel {
    private final BusinessLogic.ClientBLL clientBLL = new BusinessLogic.ClientBLL();
    private final BusinessLogic.ProductBLL productBLL = new BusinessLogic.ProductBLL();
    private final BusinessLogic.OrderBLL orderBLL = new BusinessLogic.OrderBLL();


    private JComboBox<Model.Client> clientCombo;
    private JComboBox<Model.Product> productCombo;
    private JTextField quantityField;
    private JButton orderBtn;
    private DefaultTableModel orderTableModel;
    private JTable orderTable;

    public OrderPanel() {
        setLayout(new BorderLayout());


        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Select Client:"));
        clientCombo = new JComboBox<>();
        formPanel.add(clientCombo);

        formPanel.add(new JLabel("Select Product:"));
        productCombo = new JComboBox<>();
        formPanel.add(productCombo);

        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        formPanel.add(quantityField);

        orderBtn = new JButton("Place Order");
        formPanel.add(orderBtn);

        add(formPanel, BorderLayout.NORTH);


        orderTableModel = new DefaultTableModel();
        orderTable = new JTable(orderTableModel);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        refreshClients();
        refreshProducts();
        refreshOrders();

        orderBtn.addActionListener(e -> placeOrder());
    }

    private void refreshClients() {
        clientCombo.removeAllItems();
        List<Model.Client> clients = clientBLL.getAllClients();
        if (clients != null) clients.forEach(clientCombo::addItem);
    }

    private void refreshProducts() {
        productCombo.removeAllItems();
        List<Model.Product> products = productBLL.getAllProducts();
        if (products != null) products.forEach(productCombo::addItem);
    }

    private void refreshOrders() {
        List<Model.Order> orders = orderBLL.getAllOrders();
        orderTableModel.setRowCount(0);
        orderTableModel.setColumnCount(0);

        if (orders == null || orders.isEmpty()) return;

        orderTableModel.addColumn("ID");
        orderTableModel.addColumn("Client ID");
        orderTableModel.addColumn("Order Date");
        orderTableModel.addColumn("Status");
        orderTableModel.addColumn("Total");

        for (Model.Order o : orders) {
            orderTableModel.addRow(new Object[]{
                    o.getId(),
                    o.getClient_id(),
                    o.getOrder_date(),
                    o.getStatus(),
                    o.getTotal()
            });
        }
    }

    private void placeOrder() {
        Model.Client client = (Model.Client) clientCombo.getSelectedItem();
        Model.Product product = (Model.Product) productCombo.getSelectedItem();
        if (client == null || product == null) {
            JOptionPane.showMessageDialog(this, "Select both client and product!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String resultMessage = orderBLL.createOrder(client.getId(), product.getId(), quantity);
        JOptionPane.showMessageDialog(this, resultMessage);

        refreshProducts();
        refreshOrders();
    }

}