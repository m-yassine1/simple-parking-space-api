package com.parking.cars.request;

import com.parking.cars.exception.model.Error;
import io.vertx.core.http.HttpServerRequest;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class IpRequestFilter implements ContainerRequestFilter {
    @Context
    HttpServerRequest request;

    @Inject
    IpFilterService service;

    @Override
    public void filter(ContainerRequestContext context) throws IOException {
        IpFilterService.LimitingAddress address = service.getAddress(request.remoteAddress().toString());
        if(address.times > 10) {
            context.abortWith(Response.ok(new Error()
                    .code(Error.ErrorCode.TOO_MANY_REQUESTS.getValue())
                    .details("You can only send requests every 10 seconds")
                    .title("Limiting Requests")
            ).build());
        } else {
            service.invalidateCache(address.address);
            service.cacheAddress(address.address, ++address.times);
        }
    }
}
