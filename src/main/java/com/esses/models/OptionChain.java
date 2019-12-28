package com.esses.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Value
public class OptionChain {
    @JsonProperty private final List<Option> options;

    @NoArgsConstructor
    public static class Builder {

        private final TreeSet<Option> options = new TreeSet<>();

        public void add(Option option) {
            options.add(option);
        }

        public OptionChain build() {
            return new OptionChain(new ArrayList<>(options));
        }
    }
}
