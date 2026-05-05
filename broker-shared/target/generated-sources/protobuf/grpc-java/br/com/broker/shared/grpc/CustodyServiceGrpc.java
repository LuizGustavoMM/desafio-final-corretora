package br.com.broker.shared.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.56.0)",
    comments = "Source: custody.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CustodyServiceGrpc {

  private CustodyServiceGrpc() {}

  public static final String SERVICE_NAME = "custody.CustodyService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<br.com.broker.shared.grpc.BalanceRequest,
      br.com.broker.shared.grpc.BalanceResponse> getValidateBalanceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ValidateBalance",
      requestType = br.com.broker.shared.grpc.BalanceRequest.class,
      responseType = br.com.broker.shared.grpc.BalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<br.com.broker.shared.grpc.BalanceRequest,
      br.com.broker.shared.grpc.BalanceResponse> getValidateBalanceMethod() {
    io.grpc.MethodDescriptor<br.com.broker.shared.grpc.BalanceRequest, br.com.broker.shared.grpc.BalanceResponse> getValidateBalanceMethod;
    if ((getValidateBalanceMethod = CustodyServiceGrpc.getValidateBalanceMethod) == null) {
      synchronized (CustodyServiceGrpc.class) {
        if ((getValidateBalanceMethod = CustodyServiceGrpc.getValidateBalanceMethod) == null) {
          CustodyServiceGrpc.getValidateBalanceMethod = getValidateBalanceMethod =
              io.grpc.MethodDescriptor.<br.com.broker.shared.grpc.BalanceRequest, br.com.broker.shared.grpc.BalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ValidateBalance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.broker.shared.grpc.BalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.broker.shared.grpc.BalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CustodyServiceMethodDescriptorSupplier("ValidateBalance"))
              .build();
        }
      }
    }
    return getValidateBalanceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<br.com.broker.shared.grpc.SettlementRequest,
      br.com.broker.shared.grpc.SettlementEvent> getStreamSettlementsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamSettlements",
      requestType = br.com.broker.shared.grpc.SettlementRequest.class,
      responseType = br.com.broker.shared.grpc.SettlementEvent.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<br.com.broker.shared.grpc.SettlementRequest,
      br.com.broker.shared.grpc.SettlementEvent> getStreamSettlementsMethod() {
    io.grpc.MethodDescriptor<br.com.broker.shared.grpc.SettlementRequest, br.com.broker.shared.grpc.SettlementEvent> getStreamSettlementsMethod;
    if ((getStreamSettlementsMethod = CustodyServiceGrpc.getStreamSettlementsMethod) == null) {
      synchronized (CustodyServiceGrpc.class) {
        if ((getStreamSettlementsMethod = CustodyServiceGrpc.getStreamSettlementsMethod) == null) {
          CustodyServiceGrpc.getStreamSettlementsMethod = getStreamSettlementsMethod =
              io.grpc.MethodDescriptor.<br.com.broker.shared.grpc.SettlementRequest, br.com.broker.shared.grpc.SettlementEvent>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamSettlements"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.broker.shared.grpc.SettlementRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  br.com.broker.shared.grpc.SettlementEvent.getDefaultInstance()))
              .setSchemaDescriptor(new CustodyServiceMethodDescriptorSupplier("StreamSettlements"))
              .build();
        }
      }
    }
    return getStreamSettlementsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CustodyServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CustodyServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CustodyServiceStub>() {
        @java.lang.Override
        public CustodyServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CustodyServiceStub(channel, callOptions);
        }
      };
    return CustodyServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CustodyServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CustodyServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CustodyServiceBlockingStub>() {
        @java.lang.Override
        public CustodyServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CustodyServiceBlockingStub(channel, callOptions);
        }
      };
    return CustodyServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CustodyServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CustodyServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CustodyServiceFutureStub>() {
        @java.lang.Override
        public CustodyServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CustodyServiceFutureStub(channel, callOptions);
        }
      };
    return CustodyServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void validateBalance(br.com.broker.shared.grpc.BalanceRequest request,
        io.grpc.stub.StreamObserver<br.com.broker.shared.grpc.BalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getValidateBalanceMethod(), responseObserver);
    }

    /**
     */
    default void streamSettlements(br.com.broker.shared.grpc.SettlementRequest request,
        io.grpc.stub.StreamObserver<br.com.broker.shared.grpc.SettlementEvent> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getStreamSettlementsMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CustodyService.
   */
  public static abstract class CustodyServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CustodyServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CustodyService.
   */
  public static final class CustodyServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CustodyServiceStub> {
    private CustodyServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CustodyServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CustodyServiceStub(channel, callOptions);
    }

    /**
     */
    public void validateBalance(br.com.broker.shared.grpc.BalanceRequest request,
        io.grpc.stub.StreamObserver<br.com.broker.shared.grpc.BalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getValidateBalanceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void streamSettlements(br.com.broker.shared.grpc.SettlementRequest request,
        io.grpc.stub.StreamObserver<br.com.broker.shared.grpc.SettlementEvent> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getStreamSettlementsMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CustodyService.
   */
  public static final class CustodyServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CustodyServiceBlockingStub> {
    private CustodyServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CustodyServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CustodyServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public br.com.broker.shared.grpc.BalanceResponse validateBalance(br.com.broker.shared.grpc.BalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getValidateBalanceMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<br.com.broker.shared.grpc.SettlementEvent> streamSettlements(
        br.com.broker.shared.grpc.SettlementRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getStreamSettlementsMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CustodyService.
   */
  public static final class CustodyServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CustodyServiceFutureStub> {
    private CustodyServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CustodyServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CustodyServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<br.com.broker.shared.grpc.BalanceResponse> validateBalance(
        br.com.broker.shared.grpc.BalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getValidateBalanceMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VALIDATE_BALANCE = 0;
  private static final int METHODID_STREAM_SETTLEMENTS = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VALIDATE_BALANCE:
          serviceImpl.validateBalance((br.com.broker.shared.grpc.BalanceRequest) request,
              (io.grpc.stub.StreamObserver<br.com.broker.shared.grpc.BalanceResponse>) responseObserver);
          break;
        case METHODID_STREAM_SETTLEMENTS:
          serviceImpl.streamSettlements((br.com.broker.shared.grpc.SettlementRequest) request,
              (io.grpc.stub.StreamObserver<br.com.broker.shared.grpc.SettlementEvent>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getValidateBalanceMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              br.com.broker.shared.grpc.BalanceRequest,
              br.com.broker.shared.grpc.BalanceResponse>(
                service, METHODID_VALIDATE_BALANCE)))
        .addMethod(
          getStreamSettlementsMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              br.com.broker.shared.grpc.SettlementRequest,
              br.com.broker.shared.grpc.SettlementEvent>(
                service, METHODID_STREAM_SETTLEMENTS)))
        .build();
  }

  private static abstract class CustodyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CustodyServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return br.com.broker.shared.grpc.CustodyProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CustodyService");
    }
  }

  private static final class CustodyServiceFileDescriptorSupplier
      extends CustodyServiceBaseDescriptorSupplier {
    CustodyServiceFileDescriptorSupplier() {}
  }

  private static final class CustodyServiceMethodDescriptorSupplier
      extends CustodyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CustodyServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CustodyServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CustodyServiceFileDescriptorSupplier())
              .addMethod(getValidateBalanceMethod())
              .addMethod(getStreamSettlementsMethod())
              .build();
        }
      }
    }
    return result;
  }
}
