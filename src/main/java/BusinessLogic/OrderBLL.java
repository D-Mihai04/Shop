package BusinessLogic;

import DataAccess.BillDAO;
import DataAccess.OrderDAO;
import DataAccess.ProductDAO;
import DataAccess.ClientDAO;
import Model.Bill;
import Model.Order;
import Model.Product;
import Model.Client;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderBLL implements the logic for order placement.
 * It manages the creating, checking the stock and bill generation
 */
public class OrderBLL {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final ClientDAO clientDAO;
    private final BillDAO billDAO;

    public OrderBLL() {
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        clientDAO = new ClientDAO();
        billDAO = new BillDAO();
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }


    public String createOrder(int clientId, int productId, int quantity) {
        Product product = productDAO.findById(productId);
        Client client = clientDAO.findById(clientId);

        if (product == null || client == null) {
            return "Client or Product not found.";
        }

        if (product.getQuantity() < quantity) {
            return "Not enough stock.";
        }
        Order order = new Order();
        order.setClient_id(clientId);
        order.setOrder_date(LocalDateTime.now());
        order.setStatus("FINALIZED");
        order.setTotal(product.getPrice() * quantity);

        order = orderDAO.insert(order);

        if (order == null) {
            return "Failed to create order.";
        }

        product.setQuantity(product.getQuantity() - quantity);
        productDAO.update(product);

        Bill bill = new Bill(order.getId(), client.getId(), client.getFirst_name() + " " + client.getLast_name(),
                product.getName(), quantity, product.getPrice() * quantity, order.getOrder_date());

        billDAO.insert(bill);

        return "Order created";
    }
}
