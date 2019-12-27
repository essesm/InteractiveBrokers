package com.esses.options;

import com.ib.client.Contract;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkState;

@Slf4j
public class OptionChain {

    private final Collection<OptionWithMarketData> options;
    private OptionChain(Collection<OptionWithMarketData> options) {
        this.options = options;
    }

    public static class Builder {
        private final Collection<OptionWithMarketData> optionChain;

        public Builder() {
            this.optionChain = new TreeSet<>();
        }

        public void addOption(Contract contract, double impliedVol, double delta, double optPrice, double undPrice) {
            checkState(optionChain.add(new OptionWithMarketData(contract, impliedVol, delta, optPrice, undPrice)));
        }

        public OptionChain build() {
            return new OptionChain(optionChain);
        }
    }

    public void print() {
        options.forEach(OptionWithMarketData::print);
    }

    private static class OptionWithMarketData implements Comparable<OptionWithMarketData> {
        private final Contract contract;
        private final double impliedVol;
        private final double delta;
        private final double optPrice;
        private final double undPrice;

        OptionWithMarketData(Contract contract, double impliedVol, double delta, double optPrice, double undPrice) {
            this.contract = contract;
            this.impliedVol = impliedVol;
            this.delta = delta;
            this.optPrice = optPrice;
            this.undPrice = undPrice;
        }

        public void print() {
            log.info("{}: delta ({}), price ({}), undPrice ({}), impliedVol ({})", contract.description(), delta, optPrice, undPrice, impliedVol);
        }

        @Override
        public int compareTo(OptionWithMarketData optionWithMarketData) {
            return Double.compare(contract.strike(), optionWithMarketData.contract.strike());
        }
    }
}
