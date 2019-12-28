package com.esses.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ib.client.Types;
import lombok.Value;

import java.time.LocalDate;

@Value
public class Option implements Comparable<Option> {

    @JsonProperty private final String description;
    @JsonProperty private final String ticker;
    @JsonProperty private final Types.Right right;
    @JsonProperty private final LocalDate expirationDate;
    @JsonProperty private final double strike;
    @JsonProperty private final double price;
    @JsonProperty private final double delta;
    @JsonProperty private final double impliedVolatility;

    @Override
    public int compareTo(Option option) {
        return Double.compare(strike, option.getStrike());
    }
}
