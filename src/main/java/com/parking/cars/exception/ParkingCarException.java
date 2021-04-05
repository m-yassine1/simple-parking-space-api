package com.parking.cars.exception;

import com.parking.cars.exception.model.Error;

import javax.ws.rs.core.Response;

public class ParkingCarException extends Exception {
    public String message;
    public String title;
    public Error.ErrorCode code;
    public Response.Status responseStatus;

    public ParkingCarException(String message, String title, Error.ErrorCode code) {
        super(message);
        this.title = title;
        this.message = message;
        this.code = code;
        this.responseStatus = Response.Status.BAD_REQUEST;
    }

    public ParkingCarException(String message, String title, Response.Status responseStatus, Error.ErrorCode code) {
        super(message);
        this.title = title;
        this.message = message;
        this.code = code;
        this.responseStatus = responseStatus;
    }
}
