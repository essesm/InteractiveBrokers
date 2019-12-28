package com.esses.handlers;

import com.esses.ib.ewrapper.EWrapperCallbackHandlers;
import com.esses.options.OptionChain;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class OptionContractDetailsCallableImpl implements OptionContractDetailsCallable {

    private final EClientSocket client;
    private final CompletableFuture<OptionChain> future;
    private final OptionChain.Builder optionChainBuilder;

    private int outstandingRequests = 0;

    public OptionContractDetailsCallableImpl(EClientSocket client, CompletableFuture<OptionChain> future) {
        this.client = client;
        this.future = future;
        this.optionChainBuilder = new OptionChain.Builder();
    }

    @Override
    public void onReceiveOptionContractDetails(ContractDetails contractDetails) {
        OptionContractMarketDataCallableImpl handler = new OptionContractMarketDataCallableImpl(optionChainBuilder, contractDetails.contract(), ++outstandingRequests, future);
        EWrapperCallbackHandlers.getInstance().call(reqId ->
                client.reqMktData(reqId, contractDetails.contract(), "", true, null), handler);
    }
}
