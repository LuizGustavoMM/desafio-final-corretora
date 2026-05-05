package br.com.broker.core.server;

import br.com.broker.core.rmi.OrderSubmissionImpl;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CoreServer {

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando o Core da Corretora...");

            // Cria o registro RMI na porta padrão 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Instancia a implementação de submissão de ordens
            OrderSubmissionImpl orderSubmissionService = new OrderSubmissionImpl();

            // Registra o serviço com um "nome" para que o Gateway possa procurá-lo
            registry.rebind("OrderSubmissionService", orderSubmissionService);

            // TODO: No futuro, também vamos instanciar e registrar aqui o MarketMetricsImpl

            System.out.println("Servidor RMI do Core online. Aguardando chamadas remotas...");

        } catch (Exception e) {
            System.err.println("Erro crítico ao iniciar o CoreServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}