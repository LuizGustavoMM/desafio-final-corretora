package br.com.broker.gateway.client;

import br.com.broker.shared.domain.Order;
import br.com.broker.shared.domain.OrderSide;

import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BrokerSimulator {

    // Mudar valores abaixo para aumentar ou diminuir o número de corretoras e ordens    
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final int NUM_CORRETORAS = 3;
    private static final int ORDENS_POR_CORRETORA = 20;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando Simulador de Corretoras (Modo Visualização Lenta)...");
        ExecutorService pool = Executors.newFixedThreadPool(NUM_CORRETORAS);

        for (int i = 1; i <= NUM_CORRETORAS; i++) {
            final String brokerName = "Corretora-" + i;
            pool.execute(() -> simularEnvioDeOrdens(brokerName));
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
        System.out.println("Simulação finalizada.");
    }

    private static void simularEnvioDeOrdens(String brokerName) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Random random = new Random();
            String[] ativos = {"PETR4", "VALE3"}; 

            for (int i = 0; i < ORDENS_POR_CORRETORA; i++) {
                String investorId = (random.nextBoolean()) ? "investidor-123" : "investidor-404"; 
                String ativo = ativos[random.nextInt(ativos.length)];
                OrderSide side = (random.nextBoolean()) ? OrderSide.BUY : OrderSide.SELL;
                int quantidade = (random.nextInt(5) + 1) * 100; 
                
                double precoBase = 20.00 + random.nextDouble(); 
                BigDecimal preco = BigDecimal.valueOf(precoBase).setScale(2, BigDecimal.ROUND_HALF_UP);

                Order ordem = new Order(brokerName, investorId, ativo, side, quantidade, preco);
                out.writeObject(ordem);
                out.flush();

                System.out.println("[" + brokerName + "] Enviou ordem de " + side + " para " + ativo);
                
                // Sorteia um tempo de espera entre 500ms e 2500ms
                int tempoDeEspera = 500 + random.nextInt(2000); 
                Thread.sleep(tempoDeEspera); 
            }

            System.out.println(">>> " + brokerName + " encerrou suas operações no pregão de hoje.");

        } catch (Exception e) {
            System.err.println("Erro na " + brokerName + ": " + e.getMessage());
        }
    }
}
