package br.com.broker.core.rmi;

import br.com.broker.core.engine.OrderBook;
import br.com.broker.shared.domain.Order;
import br.com.broker.shared.rmi.OrderSubmissionRemote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class OrderSubmissionImpl extends UnicastRemoteObject implements OrderSubmissionRemote {

    private final Map<String, OrderBook> orderBooks;

    public OrderSubmissionImpl(Map<String, OrderBook> orderBooks) throws RemoteException {
        super();
        this.orderBooks = orderBooks;
    }

    @Override
    public void submitOrder(Order order) throws RemoteException {
        OrderBook book = orderBooks.computeIfAbsent(order.getAssetSymbol(), OrderBook::new);
        book.addOrder(order);
    }
}