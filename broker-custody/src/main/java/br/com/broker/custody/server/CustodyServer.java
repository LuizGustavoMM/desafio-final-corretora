package br.com.broker.custody.server;

import br.com.broker.custody.service.CustodyServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CustodyServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 9090;

        // Constrói e inicia o servidor gRPC
        Server server = ServerBuilder.forPort(port)
                .addService(new CustodyServiceImpl())
                .build()
                .start();

        System.out.println("Servidor de Custódia (gRPC) iniciado na porta " + port);

        // Adiciona um gancho para desligar o servidor se o programa for fechado
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Desligando o servidor gRPC...");
            if (server != null) {
                server.shutdown();
            }
            System.err.println("Servidor desligado.");
        }));

        // Mantém a Thread principal viva aguardando conexões
        server.awaitTermination();
    }
}