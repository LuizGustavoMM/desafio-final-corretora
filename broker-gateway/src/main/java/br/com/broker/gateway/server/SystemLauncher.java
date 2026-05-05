package br.com.broker.gateway.server;

import br.com.broker.core.server.CoreServer;
import br.com.broker.custody.server.CustodyServer;

public class SystemLauncher {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println(" INICIANDO AMBIENTE DA CORRETORA (3 em 1) ");
        System.out.println("==========================================");

        // 1. Sobe o Sistema de Custódia (gRPC) em uma Thread separada
        new Thread(() -> {
            try {
                CustodyServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Pausa de 3 segundos para garantir que a porta 9090 abriu
        sleep(3000);

        // 2. Sobe o Módulo Core (RMI) em uma Thread separada
        new Thread(() -> {
            try {
                CoreServer.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Pausa de 3 segundos para garantir que o registro RMI na 1099 abriu
        sleep(3000);

        // 3. Sobe o Gateway de Corretoras (Sockets) na Thread principal
        System.out.println("\n-> Iniciando o Gateway por último...");
        try {
            GatewayServer.main(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}