package br.com.broker.core.server;

import br.com.broker.core.engine.OrderBook;
import br.com.broker.core.rmi.MarketMetricsImpl;
import br.com.broker.core.rmi.OrderSubmissionImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoreServer {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            // Mapa compartilhado entre o processador de ordens e o painel de metricas
            Map<String, OrderBook> globalOrderBooks = new ConcurrentHashMap<>();

            OrderSubmissionImpl orderSubmissionService = new OrderSubmissionImpl(globalOrderBooks);
            MarketMetricsImpl marketMetricsService = new MarketMetricsImpl(globalOrderBooks);

            registry.rebind("OrderSubmissionService", orderSubmissionService);
            registry.rebind("MarketMetricsService", marketMetricsService);

            System.out.println("Servidor RMI do Core online (Ordens e Metricas dispiniveis).");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}