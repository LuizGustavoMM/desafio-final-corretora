package br.com.broker.custody.service;

import br.com.broker.shared.grpc.BalanceRequest;
import br.com.broker.shared.grpc.BalanceResponse;
import br.com.broker.shared.grpc.CustodyServiceGrpc;
import br.com.broker.shared.grpc.SettlementEvent;
import br.com.broker.shared.grpc.SettlementRequest;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CustodyServiceImpl extends CustodyServiceGrpc.CustodyServiceImplBase {

    private final Map<String, Double> mockDatabase = new ConcurrentHashMap<>();

    public CustodyServiceImpl() {
        // Colocando um saldo falso para testes
        mockDatabase.put("investidor-123", 10000.0);
    }

    @Override
    public void validateBalance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        System.out.println("[Custódia] Validando saldo para investidor: " + request.getInvestorId());

        boolean hasBalance = true;
        String errorMsg = "";

        // Lógica ultra simplificada para simular a validação financeira exigida no trabalho
        if (request.getOrderSide().equals("BUY")) {
            double currentBalance = mockDatabase.getOrDefault(request.getInvestorId(), 0.0);
            if (currentBalance < request.getAmountRequired()) {
                hasBalance = false;
                errorMsg = "Saldo em conta insuficiente.";
            }
        }

        BalanceResponse response = BalanceResponse.newBuilder()
                .setIsValid(hasBalance)
                .setErrorMessage(errorMsg)
                .build();

        // Envia a resposta de volta ao Core
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void streamSettlements(SettlementRequest request, StreamObserver<SettlementEvent> responseObserver) {
        System.out.println("[Custódia] Core " + request.getBrokerCoreId() + " conectou no streaming de liquidação.");

        // O gRPC exige o envio imediato ou a guarda desse 'responseObserver' para uso futuro.
        // envia um evento de "Boas-vindas/Conexão OK" simulando uma liquidação inicial.
        SettlementEvent welcomeEvent = SettlementEvent.newBuilder()
                .setTradeId("SYS-000")
                .setStatus("STREAMING_ATIVO")
                .build();

        responseObserver.onNext(welcomeEvent);

    }
}