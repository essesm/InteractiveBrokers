package com.esses.handlers;

import com.esses.ib.TickType;
import com.esses.options.OptionChain;
import com.ib.client.Contract;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkState;

@Slf4j
public class OptionContractMarketDataCallableImpl implements OptionContractMarketDataCallable {
    private final OptionChain.Builder optionChainBuilder;
    private final Contract contract;
    private final CompletableFuture<OptionChain> future;
    private int outstandingRequests;

    public OptionContractMarketDataCallableImpl(OptionChain.Builder optionChainBuilder, Contract contract, int outstandingRequests, CompletableFuture<OptionChain> future) {
        this.optionChainBuilder = optionChainBuilder;
        this.contract = contract;
        this.outstandingRequests = outstandingRequests;
        this.future = future;
    }

    @Override
    public void onReceiveOptionContractComputationDetails(TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        if (tickType == TickType.DELAYED_MODEL_OPTION) {
            optionChainBuilder.addOption(contract, impliedVol, delta, optPrice, undPrice);
        }
    }

    @Override
    public void onRequestOptionContractMarketDataEnd() {
        if (--outstandingRequests == 0) {
            OptionChain optionChain = optionChainBuilder.build();
            checkState(future.complete(optionChain));
        }
    }
}
