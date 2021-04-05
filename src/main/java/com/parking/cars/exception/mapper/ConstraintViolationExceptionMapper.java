package com.parking.cars.exception.mapper;

import com.parking.cars.exception.model.Error;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, "application/problem+json")
                .entity(new Error()
                        .fields(getInvalidFields(e.getConstraintViolations()))
                        .code(400)
                        .details(e.getMessage())
                        .title("Bad Request")
                ).build();
    }

    private List<Error.ErrorField> getInvalidFields(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(v -> { return new Error.ErrorField()
                        .field(getErrorFieldName(v))
                        .message(v.getMessage());
                }).collect(Collectors.toList());
    }

    private String getErrorFieldName(ConstraintViolation<?> violation) {
        return violation.getPropertyPath().toString();
    }
}
