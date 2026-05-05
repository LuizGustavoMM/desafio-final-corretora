package br.com.broker.core.grpc;

import br.com.broker.shared.grpc.BalanceRequest;
import br.com.broker.shared.grpc.BalanceResponse;
import br.com.broker.shared.grpc.CustodyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CustodyClient {

    private final CustodyServiceGrpc.CustodyServiceBlockingStub blockingStub;

    public CustodyClient() {
        // Cria um canal conectando no servidor de Custódia (Módulo 3)
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext() // usePlaintext desabilita SSL/TLS, ideal para testes locais
                .build();

        // O BlockingStub faz com que a Thread espere a resposta do servidor para continuar
        this.blockingStub = CustodyServiceGrpc.newBlockingStub(channel);
    }

    public boolean validateInvestorBalance(String investorId, String assetSymbol, double amountRequired, String side) {
        BalanceRequest request = BalanceRequest.newBuilder()
                .setInvestorId(investorId)
                .setAssetSymbol(assetSymbol)
                .setAmountRequired(amountRequired)
                .setOrderSide(side)
                .build();

        try {
            // Faz a chamada remota via gRPC
            BalanceResponse response = blockingStub.validateBalance(request);

            if (!response.getIsValid()) {
                System.out.println("[Core] ERROR: Negócio bloqueado pela Custódia: " + response.getErrorMessage());
            }
            return response.getIsValid();

        } catch (Exception e) {
            System.err.println("[Core] Warning: Sistema de Custódia Indisponível! Abortando validação.");
            // Se a custódia cair, retornamos false para suspender as negociações, cumprindo a regra!
            return false;
        }
    }
}