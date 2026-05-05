package br.com.broker.core.rmi;

import br.com.broker.core.engine.OrderBook;
import br.com.broker.shared.domain.Order;
import br.com.broker.shared.rmi.OrderSubmissionRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OrderSubmissionImpl extends UnicastRemoteObject implements OrderSubmissionRemote {

    // Um mapa thread-safe para guardar os livros de ofertas de diferentes ativos (ex: PETR4, VALE3)
    private final Map<String, OrderBook> orderBooks;

    public OrderSubmissionImpl() throws RemoteException {
        super();
        this.orderBooks = new ConcurrentHashMap<>();
    }

    @Override
    public void submitOrder(Order order) throws RemoteException {
        System.out.println("[Core RMI] Ordem recebida do Gateway: " + order.getOrderId());

        // Pega o livro de ofertas do ativo (ou cria um novo se não existir)
        OrderBook book = orderBooks.computeIfAbsent(order.getAssetSymbol(), OrderBook::new);

        // Adiciona a ordem no livro (que fará o processamento thread-safe)
        book.addOrder(order);
    }
}