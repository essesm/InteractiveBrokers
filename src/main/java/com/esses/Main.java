package com.esses;

import com.esses.ib.ewrapper.EWrapperImpl;
import com.esses.resources.OptionChainResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        InteractiveBrokersAPI api = new EWrapperImpl().getAPI();
        environment.jersey().register(new OptionChainResource(api));
    }
}
