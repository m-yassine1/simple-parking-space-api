package com.parking.cars.exception.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {
    public Integer code;
    public List<ErrorField> fields;
    public String details;
    public String title;

    public Error code(Integer code) {
        this.code = code;
        return this;
    }

    public Error details(String details) {
        this.details = details;
        return this;
    }

    public Error title(String title) {
        this.title = title;
        return this;
    }

    public Error fields(List<ErrorField> fields) {
        this.fields = List.copyOf(fields);
        return this;
    }

    public static class ErrorField {
        public String field;
        public String message;

        public ErrorField field(String field) {
            this.field = field;
            return this;
        }

        public ErrorField message(String message) {
            this.message = message;
            return this;
        }
    }

    public enum ErrorCode {
        PARKING_FULL(1),
        CAR_ALREADY_PARKED(2),
        INVALID_PARKING_SPACE(3),
        PARKING_SPACE_EMPTY(4),
        SLOT_ID_OR_CAR_ID_REQUIRED(5),
        CAR_NOT_FOUND(6),
        INVALID_SELECTION(7),
        TOO_MANY_REQUESTS(8);
        int value;
        ErrorCode(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }
    }
}
