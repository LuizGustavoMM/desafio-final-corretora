package br.com.broker.core.grpc;

import br.com.broker.shared.grpc.BalanceRequest;
import br.com.broker.shared.grpc.BalanceResponse;
import br.com.broker.shared.grpc.CustodyServiceGrpc;
import br.com.broker.shared.grpc.SettlementEvent;
import br.com.broker.shared.grpc.SettlementRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class CustodyClient {

    private final CustodyServiceGrpc.CustodyServiceBlockingStub blockingStub;

    public CustodyClient() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
        this.blockingStub = CustodyServiceGrpc.newBlockingStub(channel);

        CustodyServiceGrpc.CustodyServiceStub asyncStub = CustodyServiceGrpc.newStub(channel);
        SettlementRequest req = SettlementRequest.newBuilder().setBrokerCoreId("CORE-01").build();

        // Fica escutando as atualizações de saldo e liquidação da Custódia em tempo real
        asyncStub.streamSettlements(req, new StreamObserver<SettlementEvent>() {
            @Override
            public void onNext(SettlementEvent value) {
                // <-- IMPRIMINDO A NOTIFICAÇÃO COMPLETA COM O SALDO
                if (!value.getTradeId().equals("SYS-000")) {
                    System.out.println("[Streaming] 💰 Notificação de Liquidação (" + value.getTradeId() +
                            ") | Investidor: " + value.getInvestorId() +
                            " | Novo Saldo: R$ " + String.format("%.2f", value.getNewBalance()) +
                            " | Status: " + value.getStatus());
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("[Streaming] Conexão com Custódia perdida!");
            }

            @Override
            public void onCompleted() { }
        });
    }

    public boolean validateInvestorBalance(String investorId, String assetSymbol, double amountRequired, String side) {
        BalanceRequest request = BalanceRequest.newBuilder()
                .setInvestorId(investorId)
                .setAssetSymbol(assetSymbol)
                .setAmountRequired(amountRequired)
                .setOrderSide(side)
                .build();

        try {
            BalanceResponse response = blockingStub.validateBalance(request);
            return response.getIsValid();
        } catch (Exception e) {
            throw new RuntimeException("CUSTODY_OFFLINE");
        }
    }
}