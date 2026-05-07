package br.com.broker.core.engine;

import br.com.broker.core.grpc.CustodyClient;
import br.com.broker.shared.domain.Order;
import br.com.broker.shared.domain.OrderSide;
import br.com.broker.shared.domain.Trade;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

public class OrderBook {

    private final String assetSymbol;
    private final PriorityQueue<Order> buyOrders;
    private final PriorityQueue<Order> sellOrders;
    private final ReentrantLock lock = new ReentrantLock();
    private final CustodyClient custodyClient = new CustodyClient();
    private BigDecimal lastPrice = BigDecimal.ZERO;
    private int tradedVolume = 0;

    public OrderBook(String assetSymbol) {
        this.assetSymbol = assetSymbol;
        this.buyOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice).reversed().thenComparing(Order::getTimestamp));
        this.sellOrders = new PriorityQueue<>(Comparator.comparing(Order::getPrice).thenComparing(Order::getTimestamp));
    }

    public void addOrder(Order order) {
        lock.lock();
        try {
            if (order.getSide() == OrderSide.BUY) {
                buyOrders.add(order);
            } else {
                sellOrders.add(order);
            }
            matchOrders();
        } finally {
            lock.unlock();
        }
    }

    private void matchOrders() {
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
            Order bestBuy = buyOrders.peek();
            Order bestSell = sellOrders.peek();

            if (bestBuy.getPrice().compareTo(bestSell.getPrice()) >= 0) {
                int tradeQuantity = Math.min(bestBuy.getQuantity(), bestSell.getQuantity());
                BigDecimal tradePrice = bestSell.getPrice();
                double totalValueRequired = tradePrice.doubleValue() * tradeQuantity;

                boolean isBuyerValid;
                try {
                    isBuyerValid = custodyClient.validateInvestorBalance(bestBuy.getInvestorId(), assetSymbol, totalValueRequired, "BUY");
                } catch (RuntimeException e) {
                    // REQUISITO 11: Circuit Breaker. Para de processar esse ativo imediatamente.
                    System.err.println("CRITICO: Custodia offline! Negociacoes de " + assetSymbol + " suspensas.");
                    break;
                }

                if (!isBuyerValid) {
                    buyOrders.poll();
                    continue;
                }

                buyOrders.poll();
                sellOrders.poll();

                // Atualiza Metricas
                this.lastPrice = tradePrice;
                this.tradedVolume += tradeQuantity;

                Trade trade = new Trade(assetSymbol, bestBuy.getInvestorId(), bestSell.getInvestorId(), tradeQuantity, tradePrice);
                System.out.println("MATCH: " + trade);

                if (bestBuy.getQuantity() > tradeQuantity) {
                    buyOrders.add(new Order(bestBuy.getBrokerId(), bestBuy.getInvestorId(), bestBuy.getAssetSymbol(), bestBuy.getSide(), bestBuy.getQuantity() - tradeQuantity, bestBuy.getPrice()));
                }
                if (bestSell.getQuantity() > tradeQuantity) {
                    sellOrders.add(new Order(bestSell.getBrokerId(), bestSell.getInvestorId(), bestSell.getAssetSymbol(), bestSell.getSide(), bestSell.getQuantity() - tradeQuantity, bestSell.getPrice()));
                }
            } else {
                break;
            }
        }
    }

    public BigDecimal getLastPrice() { return lastPrice; }
    public int getTradedVolume() { return tradedVolume; }
    public String getAssetSymbol() { return assetSymbol; }
}