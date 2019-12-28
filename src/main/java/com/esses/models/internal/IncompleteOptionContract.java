package com.esses.models.internal;

import com.esses.utils.DateUtils;
import com.ib.client.Contract;
import com.ib.client.Types;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * IncompleteOptionContracts are Contracts, but without a strike price.
 */
@Slf4j
public class IncompleteOptionContract extends Contract {

    public IncompleteOptionContract(String ticker, LocalDate expirationDate, Types.Right right) {
        symbol(checkNotNull(ticker));
        secType(Types.SecType.OPT);
        currency("USD");
        exchange("CBOE");
        lastTradeDateOrContractMonth(DateUtils.DATE_FORMAT.format(checkNotNull(expirationDate)));
        right(checkNotNull(right));
        multiplier("100");
    }
}
