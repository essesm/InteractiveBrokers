package com.esses.resources;

import com.codahale.metrics.health.HealthCheck;
import com.esses.InteractiveBrokersAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InteractiveBrokersHealthCheck extends HealthCheck {

    private final InteractiveBrokersAPI api;

    @Override
    protected Result check() {
        return api.isConnected() ? Result.healthy() : Result.unhealthy("Not connected to InteractiveBrokers");
    }
}
