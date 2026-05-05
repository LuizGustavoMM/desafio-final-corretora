package br.com.broker.shared.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Trade implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String tradeId;
    private final String assetSymbol;
    private final String buyerId;
    private final String sellerId;
    private final int quantity;
    private final BigDecimal price;
    private final long timestamp;

    public Trade(String assetSymbol, String buyerId, String sellerId, int quantity, BigDecimal price) {
        this.tradeId = UUID.randomUUID().toString();
        this.assetSymbol = assetSymbol;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public String getTradeId() { return tradeId; }
    public String getAssetSymbol() { return assetSymbol; }
    public String getBuyerId() { return buyerId; }
    public String getSellerId() { return sellerId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("Trade[%s] %d %s @ R$ %s (Buyer: %s, Seller: %s)",
                tradeId, quantity, assetSymbol, price, buyerId, sellerId);
    }
}