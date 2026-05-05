package br.com.broker.gateway.server;

import br.com.broker.shared.domain.Order;
import br.com.broker.shared.rmi.OrderSubmissionRemote;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BrokerConnectionHandler implements Runnable {

    private final Socket clientSocket;

    public BrokerConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            System.out.println("Canal de comunicação aberto com a corretora.");

            while (true) {
                Order incomingOrder = (Order) in.readObject();
                System.out.println("[Gateway] Ordem recebida: " + incomingOrder);
                routeOrderToCore(incomingOrder);
            }

        } catch (EOFException e) {
            System.out.println("Corretora encerrou a conexão normalmente.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro na comunicação com a corretora: " + e.getMessage());
        } finally {
            fecharConexao();
        }
    }

    // Unica versão do metodo RMI
    private void routeOrderToCore(Order order) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            OrderSubmissionRemote coreService = (OrderSubmissionRemote) registry.lookup("OrderSubmissionService");
            coreService.submitOrder(order);

            System.out.println("   -> Ordem " + order.getOrderId() + " enviada com sucesso ao Core via RMI.");

        } catch (Exception e) {
            System.err.println("Falha ao enviar ordem ao Core via RMI: " + e.getMessage());
        }
    }

    private void fecharConexao() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erro ao fechar socket: " + e.getMessage());
        }
    }
}