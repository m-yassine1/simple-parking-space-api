package com.parking.cars.exception.mapper;

import com.parking.cars.exception.model.Error;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .header(HttpHeaders.CONTENT_TYPE, "application/problem+json")
                .entity(new Error()
                        .details(e.getMessage())
                        .code(404)
                        .title("Not Found")
                ).build();
    }
}
