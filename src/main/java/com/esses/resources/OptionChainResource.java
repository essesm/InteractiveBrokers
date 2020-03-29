package com.esses.resources;

import com.esses.InteractiveBrokersAPI;
import com.esses.models.Option;
import com.esses.models.OptionChain;
import com.esses.models.internal.IncompleteOptionContract;
import com.ib.client.Types;
import io.dropwizard.jersey.jsr310.LocalDateParam;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

@Path("/options")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class OptionChainResource {

    private final InteractiveBrokersAPI api;

    @GET
    @Path("/{right}/{ticker}/{date}")
    public OptionChain optionChain(
            @PathParam("right") String right,
            @PathParam("ticker") String ticker,
            @PathParam("date") LocalDateParam date,
            @QueryParam("delta") Double delta) throws ExecutionException, InterruptedException {
        IncompleteOptionContract put = new IncompleteOptionContract(ticker, date.get(), Types.Right.get(right.toUpperCase()));
        OptionChain optionChain = api.requestOptionChain(put).get();

        if (delta != null) {
            Option option = optionChain.getOptions().stream().min(Comparator.comparingDouble(o -> Math.abs(o.getDelta() - delta))).orElseThrow(NotFoundException::new);
            optionChain = new OptionChain(Collections.singletonList(option));
        }

        return optionChain;
    }
}
