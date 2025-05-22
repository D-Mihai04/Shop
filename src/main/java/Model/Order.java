package Model;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private int client_id;
    private LocalDateTime order_date;
    private String status;
    private double total;

    public Order() {}

    public Order(int id, int client_id, LocalDateTime order_date, String status, double total) {
        this.id = id;
        this.client_id = client_id;
        this.order_date = order_date;
        this.status = status;
        this.total = total;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getClient_id() {
        return client_id;
    }
    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public LocalDateTime getOrder_date() {
        return order_date;
    }
    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotal() {
        return total;
    }
    public void setTotal(double total) {
        this.total = total;
    }
}
