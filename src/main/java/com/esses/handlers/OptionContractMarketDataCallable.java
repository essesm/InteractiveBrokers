package com.esses.handlers;


import com.esses.ib.TickType;
import com.esses.ib.ewrapper.EWrapperCallbacks;

public interface OptionContractMarketDataCallable extends EWrapperCallbacks {

    void onReceiveOptionContractComputationDetails(TickType tickType, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice);

    void onRequestOptionContractMarketDataEnd();

    default void onReceiveOptionContractMarketDataPrice(TickType tickType, double price) { }

    default void onReceiveOptionContractMarketDataSize(TickType tickType, int size) { }

    default void onReceiveOptionContractMarketDataString(TickType tickType, String value) { }
}
