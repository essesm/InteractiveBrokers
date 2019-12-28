package com.esses.handlers;

import com.esses.ib.ewrapper.EWrapperCallbackHandlers;
import com.esses.models.OptionChain;
import com.ib.client.ContractDetails;
import com.ib.client.EClientSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class OptionContractDetailsCallableImpl implements OptionContractDetailsCallable {

    private final EClientSocket client;
    private final CompletableFuture<OptionChain> future;
    private final OptionChain.Builder builder = new OptionChain.Builder();

    private int outstandingRequests = 0;

    @Override
    public void onReceiveOptionContractDetails(ContractDetails contractDetails) {
        OptionContractMarketDataCallableImpl handler = new OptionContractMarketDataCallableImpl(builder, contractDetails.contract(), ++outstandingRequests, future);
        EWrapperCallbackHandlers.getInstance().call(reqId ->
                client.reqMktData(reqId, contractDetails.contract(), "", true, null), handler);
    }
}
