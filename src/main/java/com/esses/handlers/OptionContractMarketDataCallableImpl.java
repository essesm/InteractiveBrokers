package com.esses.handlers;

import com.esses.ib.TickType;
import com.esses.models.Option;
import com.esses.models.OptionChain;
import com.esses.utils.DateUtils;
import com.ib.client.Contract;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static com.google.common.base.Preconditions.checkState;

@Slf4j
public class OptionContractMarketDataCallableImpl implements OptionContractMarketDataCallable {
    private final OptionChain.Builder builder;
    private final Contract contract;
    private final CompletableFuture<OptionChain> future;
    private int outstandingRequests;

    public OptionContractMarketDataCallableImpl(OptionChain.Builder builder, Contract contract, int outstandingRequests, CompletableFuture<OptionChain> future) {
        this.builder = builder;
        this.contract = contract;
        this.outstandingRequests = outstandingRequests;
        this.future = future;
    }

    @Override
    public void onReceiveOptionContractComputationDetails(TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        if (tickType == TickType.DELAYED_MODEL_OPTION) {
            builder.add(new Option(contract.description(), contract.symbol(), contract.right(), LocalDate.parse(contract.lastTradeDateOrContractMonth(), DateUtils.DATE_FORMAT), contract.strike(), optPrice, impliedVol, delta));
        }
    }

    @Override
    public void onRequestOptionContractMarketDataEnd() {
        if (--outstandingRequests == 0) {
            OptionChain optionChain = builder.build();
            checkState(future.complete(optionChain));
        }
    }
}
