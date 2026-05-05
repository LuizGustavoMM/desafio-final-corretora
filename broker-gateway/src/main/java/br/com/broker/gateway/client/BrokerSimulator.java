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

    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final int NUM_CORRETORAS = 5; // Simula 5 conexões simultâneas
    private static final int ORDENS_POR_CORRETORA = 100; // Cada uma envia 100 ordens

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Iniciando Simulador de Corretoras...");
        ExecutorService pool = Executors.newFixedThreadPool(NUM_CORRETORAS);

        for (int i = 1; i <= NUM_CORRETORAS; i++) {
            final String brokerName = "Corretora-" + i;
            pool.execute(() -> simularEnvioDeOrdens(brokerName));
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("Simulação finalizada. Verifique os logs do Core!");
    }

    private static void simularEnvioDeOrdens(String brokerName) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Random random = new Random();
            String[] ativos = {"PETR4", "VALE3"}; // Focando em dois ativos para forçar o "Match"

            for (int i = 0; i < ORDENS_POR_CORRETORA; i++) {
                // Lembra que no MockDatabase da Custódia liberamos saldo para o "investidor-123"
                String investorId = (random.nextBoolean()) ? "investidor-123" : "investidor-404";
                String ativo = ativos[random.nextInt(ativos.length)];
                OrderSide side = (random.nextBoolean()) ? OrderSide.BUY : OrderSide.SELL;
                int quantidade = (random.nextInt(5) + 1) * 100; // Lotes de 100 a 500

                // Gera preços próximos (entre R$ 20.00 e R$ 21.00) para aumentar a chance de fechar negócio
                double precoBase = 20.00 + random.nextDouble();
                BigDecimal preco = BigDecimal.valueOf(precoBase).setScale(2, BigDecimal.ROUND_HALF_UP);

                Order ordem = new Order(brokerName, investorId, ativo, side, quantidade, preco);
                out.writeObject(ordem);
                out.flush();

                // Pequena pausa para não entupir o buffer de rede instantaneamente
                Thread.sleep(10);
            }

            System.out.println("[" + brokerName + "] Terminou de enviar " + ORDENS_POR_CORRETORA + " ordens.");

        } catch (Exception e) {
            System.err.println("Erro na " + brokerName + ": " + e.getMessage());
        }
    }
}