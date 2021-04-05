package com.parking.cars.exception.mapper;

import com.parking.cars.exception.ParkingCarException;
import com.parking.cars.exception.model.Error;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ParkingCarExceptionMapper implements ExceptionMapper<ParkingCarException> {
    @Override
    public Response toResponse(ParkingCarException e) {
        return Response.status(e.responseStatus)
                .header(HttpHeaders.CONTENT_TYPE, "application/problem+json")
                .entity(new Error()
                        .details(e.getMessage())
                        .code(e.code.getValue())
                        .title("Not Found")
                ).build();
    }
}
