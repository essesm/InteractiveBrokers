package com.esses.handlers;

import com.esses.ib.TickType;
import com.esses.options.OptionChain;
import com.ib.client.Contract;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class OptionContractMarketDataCallableImpl implements OptionContractMarketDataCallable {
    private final OptionChain.Builder optionChainBuilder;
    private final Contract contract;
    private final Consumer<OptionChain> callback;
    private int outstandingRequests;

    public OptionContractMarketDataCallableImpl(OptionChain.Builder optionChainBuilder, Contract contract, int outstandingRequests, Consumer<OptionChain> callback) {
        this.optionChainBuilder = optionChainBuilder;
        this.contract = contract;
        this.outstandingRequests = outstandingRequests;
        this.callback = callback;
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
            OptionChain build = optionChainBuilder.build();
            callback.accept(build);
        }
    }
}
