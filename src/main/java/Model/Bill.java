package Model;
import java.time.LocalDateTime;

/**
 *Bill class represents a bill
 * It is a record class ,being a compact and immutable class for storing simple data

 */
public record Bill(int orderId, int clientId, String clientName, String productName,
                   int quantity, double totalPrice, LocalDateTime orderDate) {
}