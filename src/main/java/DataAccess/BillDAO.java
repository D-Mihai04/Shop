package DataAccess;

import Model.Bill;
import Connection.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

/**
 *BIllDAO uses AbstractDAO and manages inserting and retrieving bills
 */
public class BillDAO extends AbstractDAO<Bill> {
    private static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    public BillDAO() {
        super();
    }

    @Override
    protected List<Bill> createObjects(ResultSet resultSet) {
        List<Bill> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int orderId = resultSet.getInt("orderId");
                int clientId = resultSet.getInt("clientId");
                String clientName = resultSet.getString("clientName");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                double totalPrice = resultSet.getDouble("totalPrice");
                LocalDateTime orderDate = resultSet.getTimestamp("orderDate").toLocalDateTime();

                Bill bill = new Bill(orderId, clientId, clientName, productName, quantity, totalPrice, orderDate);
                list.add(bill);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:createObjects " + e.getMessage());
        }
        return list;
    }

    @Override
    public Bill insert(Bill bill) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO bill (orderId, clientId, clientName, productName, quantity, totalPrice, orderDate) VALUES (?, ?, ?, ?, ?, ?, ?)");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, bill.orderId());
            statement.setInt(2, bill.clientId());
            statement.setString(3, bill.clientName());
            statement.setString(4, bill.productName());
            statement.setInt(5, bill.quantity());
            statement.setDouble(6, bill.totalPrice());
            statement.setObject(7, bill.orderDate());

            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                int generatedId = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return bill;
    }

    @Override
    public List<Bill> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = "SELECT * FROM bill";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "BillDAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return new ArrayList<>();
    }
}