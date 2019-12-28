package com.esses;

import com.esses.ib.ewrapper.EWrapperImpl;
import com.esses.options.IncompleteOptionContract;
import com.esses.options.OptionChain;
import com.ib.client.Types;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        InteractiveBrokersAPI api = new EWrapperImpl().getAPI();
        IncompleteOptionContract put = new IncompleteOptionContract("AAPL", LocalDate.of(2020, Month.JANUARY, 17), Types.Right.Put);
        OptionChain optionChain = api.requestOptionChain(put).get();
        optionChain.print();
    }
}
