package br.com.broker.gateway.client;

import br.com.broker.shared.rmi.MarketMetricsRemote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MonitorPanel {

    public static void main(String[] args) {
        try {
            System.out.println("Conectando ao painel de metricas da Bolsa...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MarketMetricsRemote metricsService = (MarketMetricsRemote) registry.lookup("MarketMetricsService");

            String[] ativos = {"PETR4", "VALE3"};

            while (true) {
                System.out.println("\n--- COTACAO EM TEMPO REAL ---");
                for (String ativo : ativos) {
                    System.out.println(ativo + " | Ultimo Preco: R$ " + metricsService.getLastPrice(ativo) +
                            " | Volume Negociado: " + metricsService.getTradedVolume(ativo));
                }
                Thread.sleep(3000);
            }

        } catch (Exception e) {
            System.err.println("Erro no painel: " + e.getMessage());
        }
    }
}