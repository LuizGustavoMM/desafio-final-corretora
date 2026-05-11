package br.com.broker.core.rmi;

import br.com.broker.core.engine.OrderBook;
import br.com.broker.shared.rmi.MarketMetricsRemote;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class MarketMetricsImpl extends UnicastRemoteObject implements MarketMetricsRemote {

    private final Map<String, OrderBook> orderBooks;

    public MarketMetricsImpl(Map<String, OrderBook> orderBooks) throws RemoteException {
        super();
        this.orderBooks = orderBooks;
    }

    @Override
    public BigDecimal getLastPrice(String assetSymbol) throws RemoteException {
        OrderBook book = orderBooks.get(assetSymbol);
        return (book != null) ? book.getLastPrice() : BigDecimal.ZERO;
    }

    @Override
    public int getTradedVolume(String assetSymbol) throws RemoteException {
        OrderBook book = orderBooks.get(assetSymbol);
        return (book != null) ? book.getTradedVolume() : 0;
    }
}