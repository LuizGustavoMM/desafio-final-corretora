package br.com.broker.custody.service;

import br.com.broker.shared.grpc.BalanceRequest;
import br.com.broker.shared.grpc.BalanceResponse;
import br.com.broker.shared.grpc.CustodyServiceGrpc;
import br.com.broker.shared.grpc.SettlementEvent;
import br.com.broker.shared.grpc.SettlementRequest;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustodyServiceImpl extends CustodyServiceGrpc.CustodyServiceImplBase {

    private final Map<String, Double> mockDatabase = new ConcurrentHashMap<>();
    private final List<StreamObserver<SettlementEvent>> observers = new CopyOnWriteArrayList<>();

    public CustodyServiceImpl() {
        // Saldo inicial do investidor
        mockDatabase.put("investidor-123", 100000.0);
    }

    @Override
    public void validateBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        boolean hasBalance = true;
        String errorMsg = "";
        double updatedBalance = 0.0; // Variável para guardar o saldo atualizado

        if (request.getOrderSide().equals("BUY")) {
            double currentBalance = mockDatabase.getOrDefault(request.getInvestorId(), 0.0);
            if (currentBalance < request.getAmountRequired()) {
                hasBalance = false;
                errorMsg = "Saldo insuficiente.";
            } else {
                // Calcula o novo saldo e atualiza o banco de dados simulado
                updatedBalance = currentBalance - request.getAmountRequired();
                mockDatabase.put(request.getInvestorId(), updatedBalance);
            }
        } else {
            // Se for venda, apenas pegamos o saldo atual (na vida real, a venda adicionaria dinheiro)
            updatedBalance = mockDatabase.getOrDefault(request.getInvestorId(), 0.0);
        }

        BalanceResponse response = BalanceResponse.newBuilder()
                .setIsValid(hasBalance)
                .setErrorMessage(errorMsg)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

        // Dispara o evento de liquidação no Streaming COM O NOVO SALDO
        if (hasBalance) {
            for (StreamObserver<SettlementEvent> observer : observers) {
                try {
                    SettlementEvent event = SettlementEvent.newBuilder()
                            .setTradeId("TRD-" + System.currentTimeMillis())
                            .setInvestorId(request.getInvestorId())
                            .setNewBalance(updatedBalance) // <-- ENVIANDO O SALDO ATUALIZADO
                            .setStatus("LIQUIDADO")
                            .build();
                    observer.onNext(event);
                } catch (Exception e) {
                    observers.remove(observer);
                }
            }
        }
    }

    @Override
    public void streamSettlements(SettlementRequest request, StreamObserver<SettlementEvent> responseObserver) {
        System.out.println("[Custódia] Novo Core conectado no streaming: " + request.getBrokerCoreId());
        observers.add(responseObserver);

        SettlementEvent welcomeEvent = SettlementEvent.newBuilder()
                .setTradeId("SYS-000")
                .setStatus("STREAMING_ATIVO")
                .build();
        responseObserver.onNext(welcomeEvent);
    }
}