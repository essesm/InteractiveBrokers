package com.esses.handlers;

import com.esses.ib.ewrapper.EWrapperCallbacks;
import com.ib.client.ContractDetails;

public interface OptionContractDetailsCallable extends EWrapperCallbacks {

    void onReceiveOptionContractDetails(ContractDetails contractDetails);

    default void onRequestOptionChainEnd() { }
}
