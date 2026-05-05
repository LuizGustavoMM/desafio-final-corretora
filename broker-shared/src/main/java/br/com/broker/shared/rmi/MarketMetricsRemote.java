package br.com.broker.shared.rmi;

import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MarketMetricsRemote extends Remote {

    BigDecimal getLastPrice(String assetSymbol) throws RemoteException;

    int getTradedVolume(String assetSymbol) throws RemoteException;

}