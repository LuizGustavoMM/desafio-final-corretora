package br.com.broker.gateway.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GatewayServer {

    private static final int PORT = 8080;
    // Limita o servidor a lidar com até 100 corretoras simultaneamente
    private static final int MAX_CONCURRENT_BROKERS = 100;

    public static void main(String[] args) {
        System.out.println("Iniciando Gateway de Corretoras na porta " + PORT + "...");

        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CONCURRENT_BROKERS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Gateway pronto para receber conexões TCP.");

            // Loop infinito: O servidor fica sempre ativo aguardando clientes
            while (true) {
                // Bloqueia até que uma corretora se conecte
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova corretora conectada: " + clientSocket.getInetAddress().getHostAddress());

                // Cria a tarefa de tratamento e entrega para uma Thread separada executar
                BrokerConnectionHandler handler = new BrokerConnectionHandler(clientSocket);
                threadPool.execute(handler);
            }

        } catch (IOException e) {
            System.err.println("Erro crítico no GatewayServer: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
}