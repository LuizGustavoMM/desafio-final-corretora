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

    // Instancia o cliente gRPC para falar com a Custódia
    private final CustodyClient custodyClient = new CustodyClient();

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

                // --- VALIDACAO COM O SISTEMA DE CUSTODIA (gRPC) ---
                double totalValueRequired = tradePrice.doubleValue() * tradeQuantity;
                boolean isBuyerValid = custodyClient.validateInvestorBalance(
                        bestBuy.getInvestorId(), assetSymbol, totalValueRequired, "BUY");

                if (!isBuyerValid) {
                    System.out.println("-> Descartando ordem do investidor " + bestBuy.getInvestorId() + " por falta de saldo ou indisponibilidade.");
                    buyOrders.poll(); // Remove o comprador sem saldo da fila
                    continue; // Tenta o próximo par
                }

                // Retira as ordens das filas (O negócio vai acontecer)
                buyOrders.poll();
                sellOrders.poll();

                Trade trade = new Trade(assetSymbol, bestBuy.getInvestorId(), bestSell.getInvestorId(), tradeQuantity, tradePrice);
                System.out.println("NEGÓCIO FECHADO: " + trade);

                // Lida com execuções parciais
                if (bestBuy.getQuantity() > tradeQuantity) {
                    buyOrders.add(new Order(bestBuy.getBrokerId(), bestBuy.getInvestorId(), bestBuy.getAssetSymbol(), bestBuy.getSide(), bestBuy.getQuantity() - tradeQuantity, bestBuy.getPrice()));
                }
                if (bestSell.getQuantity() > tradeQuantity) {
                    sellOrders.add(new Order(bestSell.getBrokerId(), bestSell.getInvestorId(), bestSell.getAssetSymbol(), bestSell.getSide(), bestSell.getQuantity() - tradeQuantity, bestSell.getPrice()));
                }

            } else {
                break; // Precos nao casam
            }
        }
    }

    public String getAssetSymbol() {
        return assetSymbol;
    }
}