package com.parking.cars.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {
    @JsonProperty("car_number")
    @NotBlank(message = "The car id is required")
    @Pattern(regexp = "^[A-Z][A-Z0-9]*$",message = "Invalid car id it should contain alphanumeric characters (Capital letters only) and starts with a letter")
    public String id;
}
