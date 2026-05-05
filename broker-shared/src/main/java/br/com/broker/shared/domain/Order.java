package br.com.broker.shared.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order implements Serializable, Comparable<Order> {

    private static final long serialVersionUID = 1L;

    private final String orderId;
    private final String brokerId;
    private final String investorId;
    private final String assetSymbol;
    private final OrderSide side;
    private final int quantity;
    private final BigDecimal price;
    private final long timestamp;

    public Order(String brokerId, String investorId, String assetSymbol,
                 OrderSide side, int quantity, BigDecimal price) {
        this.orderId = UUID.randomUUID().toString();
        this.brokerId = brokerId;
        this.investorId = investorId;
        this.assetSymbol = assetSymbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        // Captura o momento exato da criação da ordem
        this.timestamp = Instant.now().toEpochMilli();
    }

    public String getOrderId() { return orderId; }
    public String getBrokerId() { return brokerId; }
    public String getInvestorId() { return investorId; }
    public String getAssetSymbol() { return assetSymbol; }
    public OrderSide getSide() { return side; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public long getTimestamp() { return timestamp; }

    @Override
    public int compareTo(Order otherOrder) {
        // Garante a ordenação pela ordem de chegada (menor timestamp primeiro)
        return Long.compare(this.timestamp, otherOrder.timestamp);
    }

    @Override
    public String toString() {
        return String.format("Order[%s %s %d @ %s - Investor: %s]",
                side, assetSymbol, quantity, price.toString(), investorId);
    }
}