package com.esses.ib.ewrapper;

import com.esses.handlers.OptionContractDetailsCallable;
import com.esses.handlers.OptionContractMarketDataCallable;
import com.esses.ib.TickType;
import com.ib.client.ContractDetails;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

/**
 * This class maintains the reqId counter and the reqId to callback map for each type of request.
 * The "call" methods are public, while all the "on" methods are scoped to this package (for use in the EWrapperImpl).
 */
@Slf4j
public class EWrapperCallbackHandlers {

    /**
     * Singleton because we don't want to reuse the same reqId for different requests.
     */
    private static final EWrapperCallbackHandlers instance = new EWrapperCallbackHandlers();
    private EWrapperCallbackHandlers() { }

    private final AtomicInteger counter = new AtomicInteger();
    private final ConcurrentHashMap<Integer, OptionContractDetailsCallable> optionChainResponseHandlers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, OptionContractMarketDataCallable> optionContractResponseHandlers = new ConcurrentHashMap<>();

    public static EWrapperCallbackHandlers getInstance() {
        return instance;
    }

    public void call(IntConsumer request, OptionContractDetailsCallable callback) {
        call(request, callback, optionChainResponseHandlers);
    }

    public void call(IntConsumer request, OptionContractMarketDataCallable callback) {
        call(request, callback, optionContractResponseHandlers);
    }

    /**
     * A slightly over-engineered helper to make requests and register callbacks. This
     * method will take care of the reqId counter logic so no other place in the code has to.
     *
     * @param request a request that requires a reqId (like methods on the EClientSocket)
     * @param callback the callback to be executed when the request is fulfilled
     * @param callbackMap the map of reqId to callback methods
     * @param <T> the callback type
     */
    private <T extends EWrapperCallbacks> void call(IntConsumer request, T callback, ConcurrentHashMap<Integer, T> callbackMap) {
        int requestId = counter.getAndIncrement();
        callbackMap.put(requestId, callback);
        request.accept(requestId);
    }

    void onReceiveOptionContractDetails(int reqId, ContractDetails contractDetails) {
        optionChainResponseHandlers.get(reqId).onReceiveOptionContractDetails(contractDetails);
    }

    void onRequestOptionChainEnd(int reqId) {
        optionChainResponseHandlers.remove(reqId).onRequestOptionChainEnd();
    }

    void onReceiveOptionContractMarketDataPrice(int reqId, TickType tickType, double price) {
        optionContractResponseHandlers.get(reqId).onReceiveOptionContractMarketDataPrice(tickType, price);
    }

    void onReceiveOptionContractMarketDataSize(int reqId, TickType tickType, int size) {
        optionContractResponseHandlers.get(reqId).onReceiveOptionContractMarketDataSize(tickType, size);
    }

    void onReceiveOptionContractMarketDataString(int reqId, TickType tickType, String value) {
        optionContractResponseHandlers.get(reqId).onReceiveOptionContractMarketDataString(tickType, value);
    }

    void onReceiveOptionComputationDetails(int reqId, TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {
        optionContractResponseHandlers.get(reqId).onReceiveOptionContractComputationDetails(tickType, impliedVol, delta, optPrice, pvDividend, gamma, vega, theta, undPrice);
    }

    void onRequestOptionContractMarketDataEnd(int reqId) {
        optionContractResponseHandlers.remove(reqId).onRequestOptionContractMarketDataEnd();
    }
}
