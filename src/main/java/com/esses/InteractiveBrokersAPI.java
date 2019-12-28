package com.esses;

import com.esses.handlers.OptionContractDetailsCallableImpl;
import com.esses.ib.ewrapper.EWrapperCallbackHandlers;
import com.esses.options.IncompleteOptionContract;
import com.esses.options.OptionChain;
import com.ib.client.EClientSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class InteractiveBrokersAPI {

    private final EClientSocket clientSocket;

    public InteractiveBrokersAPI(EClientSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public CompletableFuture<OptionChain> requestOptionChain(IncompleteOptionContract put) {
        CompletableFuture<OptionChain> future = new CompletableFuture<>();
        EWrapperCallbackHandlers.getInstance().call(reqId -> clientSocket.reqContractDetails(reqId, put), new OptionContractDetailsCallableImpl(clientSocket, future));
        return future;
    }
}
