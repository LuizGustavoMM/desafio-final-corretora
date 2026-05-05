package br.com.broker.shared.rmi;

import br.com.broker.shared.domain.Order;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OrderSubmissionRemote extends Remote {


    //Recebe uma ordem encaminhada pelo Gateway e a insere no Livro de Ofertas.

    void submitOrder(Order order) throws RemoteException;

}